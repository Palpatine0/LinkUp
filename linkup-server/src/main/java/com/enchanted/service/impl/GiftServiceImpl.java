package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.constant.TransactionConstant;
import com.enchanted.entity.Conversation;
import com.enchanted.entity.Gift;
import com.enchanted.entity.Transaction;
import com.enchanted.entity.User;
import com.enchanted.mapper.ConversationMapper;
import com.enchanted.mapper.GiftMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IConversationService;
import com.enchanted.service.IGiftService;
import com.enchanted.service.ITransactionService;
import com.enchanted.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Service
public class GiftServiceImpl extends ServiceImpl<GiftMapper, Gift> implements IGiftService {

    @Autowired
    private GiftMapper giftMapper;

    @Autowired
    private IUserService userService;

    @Autowired
    private ITransactionService transactionService;

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private IConversationService conversationService;

    /* C */
    @Override
    public boolean save(Gift gift) {
        return giftMapper.insert(gift) > 0;
    }

    /**
     * Update Looking Coins for sender and recipient and update the conversation time limit
     *
     * @param senderId
     * @param recipientId
     * @param giftId
     * @return
     */
    @Override
    public Long purchase(Long senderId, Long recipientId, Long giftId) {

        Gift gift = giftMapper.selectById(giftId);
        if (gift == null) {
            throw new IllegalArgumentException("Gift not found");
        }
        BigDecimal recipientShare = gift.getPrice().divide(new BigDecimal(2));
        Integer chatDuration = gift.getChatDuration();

        // Fetch the sender (User A)
        User sender = userService.getById(senderId);
        if (sender == null) {
            throw new IllegalArgumentException("Sender not found");
        }

        // Check if sender has enough looking coins
        if (sender.getLookingCoins().compareTo(gift.getPrice()) < 0) {
            throw new IllegalArgumentException("Insufficient looking coins to purchase the gift");
        }

        // Deduct the full gift price from the sender's looking coins
        sender.setLookingCoins(sender.getLookingCoins().subtract(gift.getPrice()));
        userService.updateById(sender);

        // Fetch the recipient (Servant)
        User recipient = userService.getById(recipientId);
        if (recipient == null) {
            throw new IllegalArgumentException("Recipient not found");
        }

        // Add half of the gift price to the recipient's looking coins balance
        recipient.setLookingCoins(recipient.getLookingCoins().add(recipientShare));
        userService.updateById(recipient);

        // Check if an active conversation exists between the sender and the recipient
        QueryWrapper<Conversation> wrapper = new QueryWrapper<>();
        wrapper.eq("client_id", senderId);
        wrapper.eq("servant_id", recipientId);
        Conversation activeConversation = conversationMapper.selectOne(wrapper);

        Long conversationId;
        Date newExpirationTime;

        if (activeConversation == null) {
            // No active conversation found, create a new one with a new expiration time
            Conversation newConversation = new Conversation();
            newConversation.setClientId(senderId);
            newConversation.setServantId(recipientId);
            newExpirationTime = new Date(System.currentTimeMillis() + (chatDuration * 60 * 1000));
            newConversation.setExpirationTime(newExpirationTime);
            newConversation.setServantResponseRequired(1);
            newConversation.setLastClientMessageTime(new Date());
            newConversation.setGiftId(giftId);
            conversationService.save(newConversation);
            conversationId = newConversation.getId();
        } else {
            // Update the existing conversation by extending its expiration time
            Date currentExpirationTime = activeConversation.getExpirationTime();

            // If the current expiration time is in the past, reset it to now and add the duration
            if (currentExpirationTime == null || currentExpirationTime.before(new Date())) {
                newExpirationTime = new Date(System.currentTimeMillis() + (chatDuration * 60 * 1000));
            } else {
                // Add the new duration to the current expiration time
                newExpirationTime = new Date(currentExpirationTime.getTime() + (chatDuration * 60 * 1000));
            }

            activeConversation.setExpirationTime(newExpirationTime);
            activeConversation.setServantResponseRequired(1);
            activeConversation.setLastClientMessageTime(new Date());
            activeConversation.setGiftId(giftId);
            conversationService.updateById(activeConversation);
            conversationId = activeConversation.getId();
        }

        // Record the transaction for the sender
        Transaction senderTransaction = new Transaction();
        senderTransaction.setCurrencyType(TransactionConstant.LOOKING_COIN_CURRENCY);
        senderTransaction.setUserId(sender.getId());
        senderTransaction.setAmount(gift.getPrice().negate());  // Negative value to indicate deduction
        senderTransaction.setBalanceAfter(sender.getLookingCoins());
        senderTransaction.setTransactionType(TransactionConstant.DEDUCTION);  // Deduction
        senderTransaction.setDescription(TransactionConstant.CLIENT_CONVERSATION_GIFT);
        senderTransaction.setDescriptionCn(TransactionConstant.CLIENT_CONVERSATION_GIFT_CN);
        senderTransaction.setConversationId(conversationId);
        transactionService.save(senderTransaction);

        // Record the transaction for the recipient
        Transaction recipientTransaction = new Transaction();
        recipientTransaction.setCurrencyType(TransactionConstant.LOOKING_COIN_CURRENCY);
        recipientTransaction.setUserId(recipient.getId());
        recipientTransaction.setAmount(recipientShare);  // Positive value to indicate addition
        recipientTransaction.setBalanceAfter(recipient.getLookingCoins());
        recipientTransaction.setTransactionType(TransactionConstant.ADDITION);  // Addition
        recipientTransaction.setDescription(TransactionConstant.SERVANT_CONVERSATION_GIFT);
        recipientTransaction.setDescriptionCn(TransactionConstant.SERVANT_CONVERSATION_GIFT_CN);
        recipientTransaction.setConversationId(conversationId);
        transactionService.save(recipientTransaction);

        return conversationId;
    }


    /* R */
    @Override
    public Page<Gift> search(Map<String, Object> params, int page, int size) {
        IPage<Gift> giftPage = new Page<>(page, size);
        giftPage = giftMapper.search(giftPage, params);
        return (Page<Gift>) giftPage;
    }

    /* U */
    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        Gift gift = giftMapper.selectById(id);
        if (gift == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(Gift.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                ReflectionUtils.setField(classField, gift, value);
            }
        });

        int updated = giftMapper.updateById(gift);
        return updated > 0;
    }

    /* D */
    @Override
    public boolean delete(Long id) {
        Gift gift = giftMapper.selectById(id);
        gift.setIsDeleted(1);
        int updated = giftMapper.updateById(gift);
        return updated > 0;
    }
}
