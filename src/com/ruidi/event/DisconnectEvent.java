package com.ruidi.event;

import com.ruidi.gamedata.GameData;
import com.ruidi.gamedata.GameLogic;
import com.ruidi.mybatis.model.Account;
import com.ruidi.utils.NNRoomManager;
import com.ruidi.utils.NNUserManager;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class DisconnectEvent extends BaseServerEventHandler{
    @Override
    public void handleServerEvent(ISFSEvent isfsEvent) {
        User user = (User) isfsEvent.getParameter(SFSEventParam.USER);
        Account account = NNUserManager.getAccount(user);
        GameData data = NNUserManager.getGameData(user);
        if (data == null){
            NNUserManager.ONLINE_USER.remove(account.getUid());
        }else {
            Room room = data.getRoom();
            if (room != null && room.isActive()){
                GameLogic logic = NNRoomManager.getLogic(room);
                logic.onDisconnect(data);
            }
        }
    }
}
