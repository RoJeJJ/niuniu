package com.ruidi.event;

import com.ruidi.mybatis.model.Account;
import com.ruidi.gamedata.GameData;
import com.ruidi.utils.NNUserManager;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

import java.util.concurrent.locks.ReentrantLock;

public class JoinZoneEvent extends BaseServerEventHandler{
    @Override
    public void handleServerEvent(ISFSEvent isfsEvent) throws SFSException {
        User user = (User) isfsEvent.getParameter(SFSEventParam.USER);
        user.setProperty("joinLock",new ReentrantLock());
        Account account = NNUserManager.getAccount(user);
        GameData gameData = NNUserManager.GAMEDATA_STORE.get(account.getUid());
        if (gameData != null){
            gameData.setAccount(account);
            gameData.setUser(user);
            NNUserManager.setGameData(user,gameData);
        }
        NNUserManager.ONLINE_USER.put(account.getUid(),user);
        GameData data = NNUserManager.getGameData(user);
        if (data != null){
            Room room = data.getRoom();
            if (room != null && room.isActive())
                getApi().joinRoom(user,room);
        }
    }
}
