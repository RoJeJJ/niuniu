package com.ruidi.request;

import com.ruidi.gamedata.GameLogic;
import com.ruidi.mybatis.model.Account;
import com.ruidi.mybatis.service.AccountService;
import com.ruidi.utils.NNRoomManager;
import com.ruidi.utils.NNUserManager;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import java.util.concurrent.TimeUnit;

public class OnCreateRoomRequest extends BaseClientRequestHandler{
    @Override
    public void handleClientRequest(User user, ISFSObject isfsObject) {
        GameLogic gameLogic = new GameLogic(isfsObject,getParentExtension());
        int cost = gameLogic.getCost();
        Account account = NNUserManager.getAccount(user);
        if (!gameLogic.isAA() && account.lockCard(cost)){
            sendCreateRoomErrMsg(user,1,null);//金币不足
            return;
        }
        gameLogic.setOwnerId(account.getUid());
        Room room = NNRoomManager.createRoom(gameLogic,getParentExtension());
        if (room == null){
            if (!gameLogic.isAA())
                account.unlockCard(cost);
            sendCreateRoomErrMsg(user,2,null);
        }else {
            gameLogic.setRoom(room);
            sendCreateRoomErrMsg(user,0, gameLogic);
            if (!gameLogic.isAA())
                SmartFoxServer.getInstance().getTaskScheduler().schedule(() -> AccountService.lockCard(account),0, TimeUnit.MILLISECONDS);
        }
    }
    private void sendCreateRoomErrMsg(User user,int  errcode,GameLogic tabVar){
        ISFSObject object = new SFSObject();
        object.putInt("code",errcode);
        if (errcode == 0 ){
            ISFSObject o = new SFSObject();
            o.putUtfString("name", tabVar.getRoom().getName());
            o.putInt("round", tabVar.getRound().getRound());
            o.putInt("mp", tabVar.getMaxPerson());
            o.putInt("cp", tabVar.getCurPerson());
            object.putSFSObject("room",o);
        }
        send("createRoom",object,user);
    }
}
