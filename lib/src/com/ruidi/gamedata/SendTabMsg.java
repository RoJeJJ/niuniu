package com.ruidi.gamedata;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;

import java.util.ArrayList;
import java.util.List;

public class SendTabMsg extends TabLog{
    protected SFSExtension extension;
    public SendTabMsg(ISFSObject object, SFSExtension extension) {
        super(object, extension);
    }
    public void setExtension(SFSExtension extension) {
        this.extension = extension;
    }
    //发送玩家抢庄信息
    public void sendQZ(GameData data) {
        ISFSObject object = new SFSObject();
        object.putLong("uid",data.getAccount().getUid());
        object.putInt("seat",data.getSeat());
        object.putBool("qz", data.getQz() == 1);
        extension.send("qz",object,room.getUserList());
    }
    //通知玩家房间人数变化
    public void sendPersonCountChange(){
        ISFSObject object = new SFSObject();
        object.putUtfString("name",room.getName());
        object.putInt("count",getCurPerson());
        extension.send("pcc",object, joinedUser);
    }
    //通知玩家游戏开始
    public void sendStartGame() {
        ISFSObject object = new SFSObject();
        object.putInt("curRound",curRound);
        ISFSArray array = SFSArray.newInstance();
        for (GameData data:curGames){
            ISFSObject o = new SFSObject();
            o.putLong("uid",data.getAccount().getUid());
            o.putInt("seat",data.getSeat());
            array.addSFSObject(o);
        }
        object.putSFSArray("gamer",array);
        extension.send("start",object,room.getUserList());
    }
    //发送庄家信息
    public void sendBanker(GameData data,boolean b){
        ISFSObject object = new SFSObject();
        object.putLong("uid",data.getAccount().getUid());
        object.putInt("seat",data.getSeat());
        object.putBool("random",b);
        extension.send("banker",object,room.getUserList());
    }
    //发送玩家下注和买码信息
    public void sendBet(GameData data,GameData target){
        ISFSObject object = new SFSObject();
        object.putLong("uid",data.getAccount().getUid());
        object.putInt("seat",data.getSeat());
        object.putLong("target_uid",target.getAccount().getUid());
        object.putInt("target_seat",target.getSeat());
        if (data == target){
            object.putInt("bet",data.getBet());
        }else {
            object.putInt("bet",target.getMaBet());
        }
        extension.send("bet",object,room.getUserList());
    }
    //发送结算信息
    public void sendSettle() {
        ISFSObject object = new SFSObject();
        ISFSArray array = SFSArray.newInstance();
        for (GameData data:curGames){
            ISFSObject o = new SFSObject();
            o.putLong("uid",data.getAccount().getUid());
            o.putInt("seat",data.getSeat());
            o.putInt("score",data.getScore());
            o.putInt("points",data.getPoints());
            array.addSFSObject(o);
        }
        object.putSFSArray("settle",array);
        extension.send("settle",object,room.getUserList());
    }
    //玩家亮牌
    public void sendShow(GameData data){
        ISFSObject object = new SFSObject();
        object.putByteArray("hand",data.getSendHand());
        extension.send("show",object,room.getUserList());
    }
    //通知玩家开始抢庄
    public void sendQZAction() {
        List<User> users = new ArrayList<>();
        for (GameData data:curGames){
            users.add(data.getUser());
        }
        extension.send("qzAction",null,users);
    }
    //通知玩家开始下注和买码
    public void sendBetAction(){
        List<User> users = new ArrayList<>();
        for (GameData data:curGames){
            if (data != banker){
                users.add(data.getUser());
            }
        }
        ISFSObject object = new SFSObject();
        object.putLong("time",BET_ACTION_TIME);
//     todo 推注           object.putBool("tui",tui != Tui.TUI0);
        extension.send("betAction",object,users);
    }
    public void sendDeal() {
        if (type == GameType.niuniushangzhuang || type == GameType.ziyouqiangzhuang){
            List<User> users = room.getUserList();
            for (GameData data:curGames){
                users.remove(data.getUser());
                ISFSObject object = new SFSObject();
                object.putByteArray("hand",data.getSendHand());
                object.putLong("show",SHOW_HAND_TIME);
                object.putBool("self",true);
                extension.send("deal",object,data.getUser());
            }
            ISFSObject object = new SFSObject();
            object.putBool("self",false);
            extension.send("deal",object,users);
        }
    }
    void sendDisconnect(GameData data){
        ISFSObject object = new SFSObject();
        object.putLong("uid",data.getAccount().getUid());
        object.putInt("seat",data.getSeat());
        extension.send("disconnect",object,room.getUserList());
    }
    public ISFSObject toSFSObject() {
        ISFSObject object = new SFSObject();
        object.putUtfString("name", room.getName());
        object.putInt("type", type.getType());
        object.putInt("rule", rule.getCode());
        object.putInt("base", baseScore.getBase());
        object.putInt("round", round.getRound());
        object.putBool("aa", AA);
        object.putInt("start_mode", autoStart.getNum());
        object.putBool("xsn", xiaoshuainiu);
        object.putBool("zdn", zhadanniu);
        object.putBool("hln", huluniu);
        object.putBool("thn", tonghuaniu);
        object.putBool("whn", wuhuaniu);
        object.putBool("szn", shunziniu);
        object.putBool("eas", entryAfterStart);
        object.putBool("twist", twist);
        object.putBool("start", gameStart);
        if (type != GameType.niuniushangzhuang)
            object.putBool("bl", betLimit);
        if (type != GameType.barenmingpai)
            object.putBool("ma", ma);
        if (type != GameType.niuniushangzhuang && type != GameType.ziyouqiangzhuang) {
            object.putBool("joker", joker);
            object.putInt("qz", qzMul.getMul());
        }
        ISFSArray array = SFSArray.newInstance();
        for (GameData data : seatData) {
            if (data == null)
                continue;
            ISFSObject o = new SFSObject();
            o.putUtfString("nick", data.getAccount().getNickname());
            o.putLong("uid", data.getAccount().getUid());
            o.putUtfString("ip", data.getAccount().getIp());
            o.putLong("reg", data.getAccount().getRegisterTime());

            o.putInt("score", data.getScore());
            o.putBool("ready", data.isReady());
            // TODO: 2018/5/9
            array.addSFSObject(o);
        }
        object.putSFSArray("user", array);
        return object;
    }
}
