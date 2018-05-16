package com.ruidi.mybatis.dao;

import com.ruidi.mybatis.model.Account;
import org.apache.ibatis.annotations.Param;


public interface AccountMapper {
    Account selectAccountByOpenId(String openid) throws Exception;
    Account selectAccountByUid(long uid) throws Exception;
    void updateAccount(Account account) throws Exception;
    void insertAccount(Account account) throws Exception;
    void insertCurrency(long uid) throws Exception;
    void updateOnline(boolean ol) throws Exception;
    void updateLockCard(Account account) throws Exception;
    void deductCardByUid(@Param("uid") long uid, @Param("cost") int cost) throws Exception;
    void deductCard(Account account) throws Exception;
}
