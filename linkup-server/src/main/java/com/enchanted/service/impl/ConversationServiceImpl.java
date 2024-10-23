package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.constant.ConversationConstant;
import com.enchanted.constant.TransactionConstant;
import com.enchanted.entity.*;
import com.enchanted.mapper.ConversationMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.mapper.GiftMapper;
import com.enchanted.mapper.MessageMapper;
import com.enchanted.service.IConversationService;
import com.enchanted.service.ITransactionService;
import com.enchanted.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation> implements IConversationService {

    @Autowired
    private IUserService userService;

    @Autowired
    private ITransactionService transactionService;

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private GiftMapper giftMapper;

    /* C */
    @Override
    public boolean save(Conversation conversation) {
        return conversationMapper.insert(conversation) > 0;
    }

    /* R */
    @Override
    public Page<Conversation> search(Map<String, Object> params, int page, int size) {
        IPage<Conversation> conversationPage = new Page<>(page, size);
        conversationPage = conversationMapper.search(conversationPage, params);
        return (Page<Conversation>) conversationPage;
    }

    /* U */
    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        Conversation conversation = conversationMapper.selectById(id);
        if (conversation == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(Conversation.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                ReflectionUtils.setField(classField, conversation, value);
            }
        });

        int updated = conversationMapper.updateById(conversation);
        return updated > 0;
    }

    @Scheduled(fixedDelay = ConversationConstant.SERVANT_PENALTY_MONITOR_DELAY) // Runs every minute
    public void monitorConversations() {
        Date now = new Date();
        Date thresholdTime = new Date(now.getTime() - ConversationConstant.SERVANT_PENALTY_MONITOR_DELAY);
        List<Conversation> overdueConversations = this.getOverdueConversations(thresholdTime);

        for (Conversation conversation : overdueConversations) {
            processUnansweredConversation(conversation);
        }
    }

    public List<Conversation> getOverdueConversations(Date thresholdTime) {
        return conversationMapper.selectOverdueConversations(thresholdTime);
    }
    @Transactional
    public void processUnansweredConversation(Conversation conversation) {
        Long clientId = conversation.getClientId();
        Long servantId = conversation.getServantId();

        User client = userService.getById(clientId);
        User servant = userService.getById(servantId);

        try {
            // Deduct 1 looking coin from servant
            BigDecimal penaltyAmount = BigDecimal.ONE;

            BigDecimal servantNewBalance = servant.getLookingCoins().subtract(penaltyAmount);
            servantNewBalance = servantNewBalance.max(BigDecimal.ZERO); // Ensure balance doesn't go negative
            servant.setLookingCoins(servantNewBalance);
            userService.updateById(servant);

            // Record servant penalty transaction
            Transaction servantPenaltyTransaction = new Transaction();
            servantPenaltyTransaction.setCurrencyType(TransactionConstant.LOOKING_COIN_CURRENCY);
            servantPenaltyTransaction.setUserId(servantId);
            servantPenaltyTransaction.setAmount(penaltyAmount.negate());
            servantPenaltyTransaction.setBalanceAfter(servantNewBalance);
            servantPenaltyTransaction.setTransactionType(TransactionConstant.DEDUCTION);
            servantPenaltyTransaction.setDescription(TransactionConstant.SERVANT_RESPONSE_DELAY_PENALTY);
            servantPenaltyTransaction.setDescriptionCn(TransactionConstant.SERVANT_RESPONSE_DELAY_PENALTY_CN);
            transactionService.save(servantPenaltyTransaction);

            // Check if this is the first message after gift purchase
            boolean isFirstMessageAfterGift = isFirstMessageAfterGiftPurchase(conversation);

            if (isFirstMessageAfterGift) {
                // Refund gift amount to client
                Gift gift = giftMapper.selectById(conversation.getGiftId());
                if (gift != null) {
                    BigDecimal giftAmount = gift.getPrice();
                    BigDecimal recipientShare = giftAmount.divide(new BigDecimal(2));

                    // Refund to client
                    BigDecimal clientNewBalance = client.getLookingCoins().add(giftAmount);
                    client.setLookingCoins(clientNewBalance);
                    userService.updateById(client);

                    // Record client refund transaction
                    Transaction clientRefundTransaction = new Transaction();
                    clientRefundTransaction.setCurrencyType(TransactionConstant.LOOKING_COIN_CURRENCY);
                    clientRefundTransaction.setUserId(clientId);
                    clientRefundTransaction.setAmount(giftAmount);
                    clientRefundTransaction.setBalanceAfter(clientNewBalance);
                    clientRefundTransaction.setTransactionType(TransactionConstant.ADDITION);
                    clientRefundTransaction.setDescription(TransactionConstant.CLIENT_NO_RESPONSE_GIFT_REFUND);
                    clientRefundTransaction.setDescriptionCn(TransactionConstant.CLIENT_NO_RESPONSE_GIFT_REFUND_CN);
                    transactionService.save(clientRefundTransaction);

                    // Deduct servant's share of gift
                    servantNewBalance = servant.getLookingCoins().subtract(recipientShare);
                    servantNewBalance = servantNewBalance.max(BigDecimal.ZERO); // Ensure balance doesn't go negative
                    servant.setLookingCoins(servantNewBalance);
                    userService.updateById(servant);

                    // Record servant gift refund transaction
                    Transaction servantGiftRefundTransaction = new Transaction();
                    servantGiftRefundTransaction.setCurrencyType(TransactionConstant.LOOKING_COIN_CURRENCY);
                    servantGiftRefundTransaction.setUserId(servantId);
                    servantGiftRefundTransaction.setAmount(recipientShare.negate());
                    servantGiftRefundTransaction.setBalanceAfter(servantNewBalance);
                    servantGiftRefundTransaction.setTransactionType(TransactionConstant.DEDUCTION);
                    servantGiftRefundTransaction.setDescription(TransactionConstant.SERVANT_NO_RESPONSE_GIFT_SHARE_DEDUCTION);
                    servantGiftRefundTransaction.setDescriptionCn(TransactionConstant.SERVANT_NO_RESPONSE_GIFT_SHARE_DEDUCTION_CN);
                    transactionService.save(servantGiftRefundTransaction);

                    conversation.setExpirationTime(null);
                }
            }

            // Update conversation to no longer require response
            conversation.setServantResponseRequired(0);
            this.updateById(conversation);

            // Transaction will commit automatically if no exceptions are thrown
        } catch (Exception e) {
            // Rollback transaction if necessary
            // Handle exception
        }
    }
    private boolean isFirstMessageAfterGiftPurchase(Conversation conversation) {
        Long clientId = conversation.getClientId();
        Long conversationId = conversation.getId();
        Date lastClientMessageTime = conversation.getLastClientMessageTime();

        // Fetch the latest gift purchase transaction for this conversation
        QueryWrapper<Transaction> transactionQuery = new QueryWrapper<>();
        transactionQuery
            .eq("user_id", clientId)
            .eq("conversation_id", conversationId)
            .eq("transaction_type", TransactionConstant.DEDUCTION)
            .eq("description", TransactionConstant.CLIENT_CONVERSATION_GIFT)
            .orderByDesc("created_at")
            .last("LIMIT 1");
        Transaction lastGiftPurchaseTransaction = transactionService.getOne(transactionQuery);

        if (lastGiftPurchaseTransaction == null) {
            // No gift purchase found for this conversation
            return false;
        }

        Date giftPurchaseTime = lastGiftPurchaseTransaction.getCreatedAt();

        // Check if the last client message is after the gift purchase time
        if (lastClientMessageTime.before(giftPurchaseTime)) {
            return false;
        }

        // Fetch any client messages in this conversation sent between the gift purchase time and the last client message time, excluding the last message
        QueryWrapper<Message> messageQuery = new QueryWrapper<>();
        messageQuery
            .eq("sender_id", clientId)
            .eq("conversation_id", conversationId)
            .ge("created_at", giftPurchaseTime)
            .lt("created_at", lastClientMessageTime);
        int messageCount = Math.toIntExact(messageMapper.selectCount(messageQuery));

        // If no messages found between gift purchase and last client message, it's the first message after gift purchase
        return messageCount == 0;
    }

    /* D */
    @Override
    public boolean delete(Long id) {
        Conversation conversation = conversationMapper.selectById(id);
        conversation.setIsDeleted(1);
        int updated = conversationMapper.updateById(conversation);
        return updated > 0;
    }
}
