package com.ruidi.event;

import com.ruidi.JoinRoomCode;
import com.ruidi.gamedata.GameLogic;
import com.ruidi.mybatis.model.Account;
import com.ruidi.gamedata.GameData;
import com.ruidi.utils.NNRoomManager;
import com.ruidi.utils.NNUserManager;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

import java.util.List;

public class OnJoinRoomEvent extends BaseServerEventHandler{
    @Override
    public void handleServerEvent(ISFSEvent isfsEvent) throws SFSException {
        User user = (User) isfsEvent.getParameter(SFSEventParam.USER);
        Room room = (Room) isfsEvent.getParameter(SFSEventParam.ROOM);
        GameLogic gameLogic = NNRoomManager.getLogic(room);
        GameData gameData = NNUserManager.getGameData(user);
        Account account = NNUserManager.getAccount(user);
        gameLogic.getJoinedUser().add(user);
        NNRoomManager.addJoinedRoom(account.getUid(),room);
        if (gameData.getAccount() == null){ //新加入
            gameData.setAccount(account);
            gameData.setUser(user);
        }else { //重新连接
            if (gameData.getSeat() == -1){ //旁观中,没有坐下
//                send("joined",table.toSFSObject(),user);
            }else { //已经坐下
//                send("joined",table.toSFSObject(),user);

                //通知房间中其他玩家
                List<User> users = room.getUserList();
                users.remove(user);
                ISFSObject ol = new SFSObject();
                ol.putLong("uid",account.getUid());
                ol.putInt("seat",gameData.getSeat());
                send("ol",ol,user);
            }
        }
        ISFSObject object = gameLogic.toSFSObject();
        object.putInt("code", JoinRoomCode.SUCCESS.getCode());
        send("joinRoom", gameLogic.toSFSObject(),user);
    }
}
