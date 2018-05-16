package com.ruidi.utils;

import com.ruidi.JoinRoomCode;
import com.ruidi.SeatCode;
import com.ruidi.gamedata.GameData;
import com.ruidi.mybatis.model.Account;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;


public class Utils {
    public static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void sendJoinRoom(SFSExtension extension, User user, JoinRoomCode code){
        ISFSObject object = new SFSObject();
        object.putInt("code",code.getCode());
        extension.send("joinRoom",object,user);
    }
    public static void sendSeatDown(SFSExtension extension, User user, SeatCode code){
        ISFSObject object = new SFSObject();
        object.putInt("code",code.getCode());
        if (code == SeatCode.SUCCESS){
            GameData data = NNUserManager.getGameData(user);
            object.putSFSObject("account", data.getAccount().toSFSObject());
            object.putSFSObject("gamdata", data.toSFSObject());
            extension.send("seatDown",object,extension.getParentRoom().getUserList());
        }else
            extension.send("seatDown",object,user);
    }
    public static void sendCard(SFSExtension extension,User user){
        Account account = NNUserManager.getAccount(user);
        ISFSObject object = new SFSObject();
        object.putLong("card",account.getCard());
        extension.send("card",object,user);
    }
}
