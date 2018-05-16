package com.ruidi.utils;

import com.ruidi.mybatis.model.Account;
import com.ruidi.gamedata.GameData;
import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.entities.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NNUserManager {
    public static final Map<Long,User> ONLINE_USER = new ConcurrentHashMap<>();
    public static final Map<Long,GameData> GAMEDATA_STORE = new ConcurrentHashMap<>();
    public static void setAccount(ISession session, Account account){
        session.setProperty("acc",account);
    }
    public static Account getAccount(User user){
        return (Account) user.getSession().getProperty("acc");
    }
    public static void setGameData(User user,GameData data){
        user.setProperty("gamedata",data);
    }
    public static void removeGameData(User user){
        user.removeProperty("gamedata");
    }
    public static GameData getGameData(User user){
        return (GameData) user.getProperty("gamedata");
    }

}
