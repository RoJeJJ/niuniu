package com.ruidi.mybatis.service;

import com.ruidi.mybatis.MyBatisHelper;


import com.ruidi.mybatis.dao.AccountMapper;
import com.ruidi.mybatis.model.Account;
import org.apache.ibatis.session.SqlSession;

public class AccountService {
    public static Account verifyAccount(Account temp){
        try (SqlSession sqlSession = MyBatisHelper.instance().openSession()){
            AccountMapper mapper = sqlSession.getMapper(AccountMapper.class);
            Account account = mapper.selectAccountByOpenId(temp.getOpenid());
            if (account != null) {
                temp.setUid(account.getUid());
                mapper.updateAccount(temp);
                sqlSession.commit();
            }else {
                temp.setLastLoginTime(0);
                temp.setRegisterTime(System.currentTimeMillis());
                temp.setLoginEnable(true);
                temp.setAgentid(0);
                mapper.insertAccount(temp);
                mapper.insertCurrency(temp.getUid());
                sqlSession.commit();
            }
            temp = mapper.selectAccountByUid(temp.getUid());
            return temp;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static void setOnline(boolean ol){
        try (SqlSession sqlSession = MyBatisHelper.instance().openSession()){
            AccountMapper mapper = sqlSession.getMapper(AccountMapper.class);
            mapper.updateOnline(ol);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void lockCard(Account account){
        try (SqlSession sqlSession = MyBatisHelper.instance().openSession()){
            AccountMapper mapper = sqlSession.getMapper(AccountMapper.class);
            mapper.updateLockCard(account);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void deductCard(long uid,int cost){
        try(SqlSession sqlSession = MyBatisHelper.instance().openSession()) {
            AccountMapper mapper = sqlSession.getMapper(AccountMapper.class);
            mapper.deductCardByUid(uid,cost);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void deductCard(Account account){

    }
}
