package com.enchanted.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.constant.ThirdPartyConstant;
import com.enchanted.entity.Bank;
import com.enchanted.entity.BankCard;
import com.enchanted.mapper.BankCardMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.mapper.BankMapper;
import com.enchanted.service.IBankCardService;
import com.enchanted.util.ConversionUtils;
import com.enchanted.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Service
public class BankCardServiceImpl extends ServiceImpl<BankCardMapper, BankCard> implements IBankCardService {

    @Autowired
    private BankMapper bankMapper;

    @Autowired
    private BankCardMapper bankCardMapper;

    @Autowired
    private HttpClientUtil httpClientUtil;

    /* C */
    @Override
    public boolean save(BankCard bankCard) {
        return bankCardMapper.insert(bankCard) > 0;
    }

    @Override
    public Map bankCardValidation(Long userId, String name, String idCardNumber, String bankCardNumber) {
        QueryWrapper<BankCard> bankCardWrapper = new QueryWrapper<>();
        bankCardWrapper.eq("user_id", userId);
        bankCardWrapper.eq("identifier", bankCardNumber);
        BankCard existedBankCard = bankCardMapper.selectOne(bankCardWrapper);
        if (existedBankCard != null) {
            throw new IllegalArgumentException("Existed bank card");
        }
        Map<String, Object> resultMap = new HashMap<>();
        String url = ThirdPartyConstant.BANK_CARD_AUTH_URL;
        String appCode = ThirdPartyConstant.BANK_CARD_AUTH_CODE;

        Map<String, String> params = new HashMap<>();
        params.put("idcard", idCardNumber);
        params.put("name", name);
        params.put("cardno", bankCardNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + appCode);

        String response = httpClientUtil.sendHttpGet(url, params, headers);
        JSONObject jsonObject = JSON.parseObject(response);
        JSONObject resultObject = jsonObject.getJSONObject("data");

        if (jsonObject.getInteger("code") == 0) {
            resultMap.put("result", "Success");
        } else if (jsonObject.getInteger("code") == 1) {
            throw new IllegalArgumentException("Match failed");
        } else if (jsonObject.getInteger("code") == 2) {
            throw new IllegalArgumentException("Input error");
        } else {
            throw new IllegalArgumentException("Invalid card");
        }

        String resultAbbr = resultObject.getString("abbr");
        String resultCardName = resultObject.getString("cardName");
        String resultBankName = resultObject.getString("bankName");
        String resultCardType = resultObject.getString("cardType");
        BankCard bankCard = new BankCard();
        bankCard.setUserId(userId);
        bankCard.setIdentifier(bankCardNumber);
        bankCard.setName(resultCardName);
        if (resultCardType.equals("借记卡")) {
            bankCard.setType(0);
        } else if (resultCardType.equals("贷记卡")) {
            bankCard.setType(1);
        }
        QueryWrapper<Bank> bankWrapper = new QueryWrapper<>();
        bankWrapper.eq("abbr", resultAbbr);
        Bank bank = bankMapper.selectOne(bankWrapper);
        if (bank != null) {
            bankCard.setBankId(bank.getId());
            bankCardMapper.insert(bankCard);
        } else {
            Bank newBank = new Bank();
            newBank.setAbbr(resultAbbr);
            newBank.setName(resultBankName);
            bankMapper.insert(newBank);
            bankCard.setBankId(newBank.getId());
            bankCardMapper.insert(bankCard);
        }
        return resultMap;
    }

    /* R */
    @Override
    public Page<BankCard> search(Map<String, Object> params, int page, int size) {
        IPage<BankCard> bankCardPage = new Page<>(page, size);
        bankCardPage = bankCardMapper.search(bankCardPage, params);
        return (Page<BankCard>) bankCardPage;
    }

    @Override
    public Page<Bank> searchBank(Map<String, Object> params, int page, int size) {
        IPage<Bank> bankPage = new Page<>(page, size);
        bankPage = bankCardMapper.searchBank(bankPage, params);
        return (Page<Bank>) bankPage;
    }

    /* U */
    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        BankCard bankCard = bankCardMapper.selectById(id);
        if (bankCard == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(BankCard.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                if (!classField.getType().isAssignableFrom(value.getClass())) {
                    Object convertedValue = ConversionUtils.convertValueToRequiredType(value, classField.getType());
                    ReflectionUtils.setField(classField, bankCard, convertedValue);
                } else {
                    ReflectionUtils.setField(classField, bankCard, value);
                }
            }
        });

        int updated = bankCardMapper.updateById(bankCard);
        return updated > 0;
    }

    /* D */
    @Override
    public boolean delete(Long id) {
        BankCard bankCard = bankCardMapper.selectById(id);
        bankCard.setIsDeleted(1);
        int updated = bankCardMapper.updateById(bankCard);
        return updated > 0;
    }
}
