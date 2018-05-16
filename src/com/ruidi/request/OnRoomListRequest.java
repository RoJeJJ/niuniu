package com.ruidi.request;

import com.ruidi.gamedata.GameLogic;
import com.ruidi.gamedata.var.TabVar;
import com.ruidi.mybatis.model.Account;
import com.ruidi.utils.NNRoomManager;
import com.ruidi.utils.NNUserManager;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import java.util.List;

public class OnRoomListRequest extends BaseClientRequestHandler{
    @Override
    public void handleClientRequest(User user, ISFSObject isfsObject) {
        Account account = NNUserManager.getAccount(user);
        List<Room> rooms = NNRoomManager.getJoinedRooms(account.getUid());
        ISFSObject object = new SFSObject();
        ISFSArray array = new SFSArray();
        for (Room room:rooms){
            if (room.isActive()){
                GameLogic tabVar = NNRoomManager.getLogic(room);
                ISFSObject o = new SFSObject();
                o.putUtfString("name",room.getName());
                o.putInt("round", tabVar.getRound().getRound());
                o.putInt("mp", tabVar.getMaxPerson());
                o.putInt("cp", tabVar.getCurPerson());
                array.addSFSObject(o);
            }
            object.putSFSArray("table",array);
            send("roomList",object,user);
        }
    }
}
