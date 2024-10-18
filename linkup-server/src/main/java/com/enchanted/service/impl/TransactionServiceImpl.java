package com.enchanted.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.constant.OrderConstant;
import com.enchanted.constant.TransactionConstant;
import com.enchanted.entity.Transaction;
import com.enchanted.entity.User;
import com.enchanted.mapper.TransactionMapper;
import com.enchanted.mapper.UserMapper;
import com.enchanted.service.ITransactionService;
import com.enchanted.service.IUserService;
import com.enchanted.util.ConversionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.math.BigDecimal;

@Service
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, Transaction> implements ITransactionService {

    @Autowired
    private IUserService userService;

    @Autowired
    private TransactionMapper transactionMapper;

    /*C*/
    @Retryable(value = {CannotGetJdbcConnectionException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    @Override
    public boolean save(Transaction transaction) {
        transaction.setCreatedAt(new java.util.Date());
        transaction.setIdentifier(generateTransactionIdentifier());
        int inserted = transactionMapper.insert(transaction);
        return retBool(inserted);
    }

    private String generateTransactionIdentifier() {
        // Date format for the identifier
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = dateFormat.format(new Date());

        // Generate a random 6-digit number
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000); // Ensures a 6-digit number

        // Combine all parts to create the identifier
        return "LK-TXN-" + currentTime + "-" + randomNumber;
    }

    /*R*/
    @Override
    public Page<Transaction> search(Map<String, Object> params, int page, int size) {
        IPage<Transaction> transactionPage = new Page<>(page, size);
        transactionPage = transactionMapper.search(transactionPage, params);
        return (Page<Transaction>) transactionPage;
    }

    /*U*/
    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        Transaction transaction = transactionMapper.selectById(id);
        if (transaction == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(Transaction.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                if (!classField.getType().isAssignableFrom(value.getClass())) {
                    Object convertedValue = ConversionUtils.convertValueToRequiredType(value, classField.getType());
                    ReflectionUtils.setField(classField, transaction, convertedValue);
                } else {
                    ReflectionUtils.setField(classField, transaction, value);
                }
            }
        });

        int updated = transactionMapper.updateById(transaction);
        return retBool(updated);
    }

    @Override
    public boolean updateLookingCoin(Transaction transaction) {
        User user = userService.getById(transaction.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        BigDecimal amount = transaction.getAmount();
        String paymentMethod = transaction.getPaymentMethod();
        Integer transactionType = transaction.getTransactionType();

        // Initialize flags for success
        boolean transactionsSaved = false;
        boolean userUpdated = false;

        // Deposit looking coins
        if (transactionType.equals(TransactionConstant.ADDITION)) {
            // Using balance to buy looking coins
            if (OrderConstant.BALANCE.equals(paymentMethod)) {
                if (user.getBalance().compareTo(amount) < 0) {
                    throw new IllegalArgumentException("Insufficient balance");
                }
                // Deduct balance
                user.setBalance(user.getBalance().subtract(amount));
                // Add looking coins
                user.setLookingCoins(user.getLookingCoins().add(amount));

                // Create Balance Deduction Transaction
                Transaction balanceTransaction = new Transaction();
                balanceTransaction.setUserId(user.getId());
                balanceTransaction.setCurrencyType(TransactionConstant.BALANCE_CURRENCY);
                balanceTransaction.setTransactionType(TransactionConstant.DEDUCTION);
                balanceTransaction.setAmount(amount.negate());
                balanceTransaction.setBalanceAfter(user.getBalance());
                balanceTransaction.setDescription("Purchase Looking Coins");
                balanceTransaction.setDescriptionCn("购买领克币");
                balanceTransaction.setCreatedAt(new Date());
                balanceTransaction.setIdentifier(generateTransactionIdentifier());
                // Save Balance Transaction
                boolean balanceTransactionSaved = this.save(balanceTransaction);

                // Create Looking Coin Addition Transaction
                Transaction lookingCoinTransaction = new Transaction();
                lookingCoinTransaction.setUserId(user.getId());
                lookingCoinTransaction.setCurrencyType(TransactionConstant.LOOKING_COIN_CURRENCY);
                lookingCoinTransaction.setTransactionType(TransactionConstant.ADDITION);
                lookingCoinTransaction.setAmount(amount);
                lookingCoinTransaction.setBalanceAfter(user.getLookingCoins());
                lookingCoinTransaction.setDescription("Purchase Looking Coins");
                lookingCoinTransaction.setDescriptionCn("购买领克币");
                lookingCoinTransaction.setCreatedAt(new Date());
                lookingCoinTransaction.setIdentifier(generateTransactionIdentifier());
                // Save Looking Coin Transaction
                boolean lookingCoinTransactionSaved = this.save(lookingCoinTransaction);

                transactionsSaved = balanceTransactionSaved && lookingCoinTransactionSaved;
            }
            // Using external payment method (e.g., WECHAT)
            else if (OrderConstant.WECHAT.equals(paymentMethod)) {
                // Add looking coins
                user.setLookingCoins(user.getLookingCoins().add(amount));

                // Create Looking Coin Addition Transaction
                Transaction lookingCoinTransaction = new Transaction();
                lookingCoinTransaction.setUserId(user.getId());
                lookingCoinTransaction.setCurrencyType(TransactionConstant.LOOKING_COIN_CURRENCY);
                lookingCoinTransaction.setTransactionType(TransactionConstant.ADDITION);
                lookingCoinTransaction.setAmount(amount);
                lookingCoinTransaction.setBalanceAfter(user.getLookingCoins());
                lookingCoinTransaction.setDescription("Purchase Looking Coins");
                lookingCoinTransaction.setDescriptionCn("购买领克币");
                lookingCoinTransaction.setCreatedAt(new Date());
                lookingCoinTransaction.setIdentifier(generateTransactionIdentifier());
                // Save Looking Coin Transaction
                transactionsSaved = this.save(lookingCoinTransaction);
            }
            else {
                throw new IllegalArgumentException("Invalid payment method");
            }
        }
        // Withdraw looking coins
        else if (transactionType.equals(TransactionConstant.DEDUCTION)) {
            if (user.getLookingCoins().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient Looking Coins");
            }
            // Deduct looking coins
            user.setLookingCoins(user.getLookingCoins().subtract(amount));
            // Add balance
            user.setBalance(user.getBalance().add(amount));

            // Create Looking Coin Deduction Transaction
            Transaction lookingCoinTransaction = new Transaction();
            lookingCoinTransaction.setUserId(user.getId());
            lookingCoinTransaction.setCurrencyType(TransactionConstant.LOOKING_COIN_CURRENCY);
            lookingCoinTransaction.setTransactionType(TransactionConstant.DEDUCTION);
            lookingCoinTransaction.setAmount(amount.negate());
            lookingCoinTransaction.setBalanceAfter(user.getLookingCoins());
            lookingCoinTransaction.setDescription("Withdraw Looking Coins");
            lookingCoinTransaction.setDescriptionCn("提现领克币");
            lookingCoinTransaction.setCreatedAt(new Date());
            lookingCoinTransaction.setIdentifier(generateTransactionIdentifier());
            // Save Looking Coin Transaction
            boolean lookingCoinTransactionSaved = this.save(lookingCoinTransaction);

            // Create Balance Addition Transaction
            Transaction balanceTransaction = new Transaction();
            balanceTransaction.setUserId(user.getId());
            balanceTransaction.setCurrencyType(TransactionConstant.BALANCE_CURRENCY);
            balanceTransaction.setTransactionType(TransactionConstant.ADDITION);
            balanceTransaction.setAmount(amount);
            balanceTransaction.setBalanceAfter(user.getBalance());
            balanceTransaction.setDescription("Withdraw Looking Coins");
            balanceTransaction.setDescriptionCn("提现领克币");
            balanceTransaction.setCreatedAt(new Date());
            balanceTransaction.setIdentifier(generateTransactionIdentifier());
            // Save Balance Transaction
            boolean balanceTransactionSaved = this.save(balanceTransaction);

            transactionsSaved = lookingCoinTransactionSaved && balanceTransactionSaved;
        }
        else {
            throw new IllegalArgumentException("Invalid transaction type");
        }

        // Update the user's balances
        userUpdated = userService.updateById(user);

        return transactionsSaved && userUpdated;
    }



    /*D*/
    @Override
    public boolean delete(Long id) {
        Transaction transaction = transactionMapper.selectById(id);
        transaction.setIsDeleted(1);
        int updated = transactionMapper.updateById(transaction);
        return updated > 0;
    }
}
