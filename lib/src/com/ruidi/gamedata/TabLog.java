package com.ruidi.gamedata;

import com.ruidi.gamedata.var.TabVar;
import com.ruidi.utils.PokerUtil;
import com.ruidi.utils.SFSObjectUtil;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;

import java.util.*;

public abstract class TabLog extends TabVar {
    public TabLog(ISFSObject object, SFSExtension extension) {
        SFSObjectUtil objectUtil = new SFSObjectUtil(object, extension);
        int t = objectUtil.getInt("type",  GameType.niuniushangzhuang.getType());
        type = GameType.valueOf(t, extension);
        t = objectUtil.getInt("baseScore", BaseScore.B1.getBase());
        baseScore = BaseScore.valueOf(t, extension);
        t = objectUtil.getInt("round", Round.R1.getRound());
        round = Round.valueOf(t, extension);
        t = objectUtil.getInt("autostart", AutoStart.START0.getNum());
        autoStart = AutoStart.valueOf(type, t, extension);

        if (type == GameType.niuniushangzhuang || type == GameType.ziyouqiangzhuang) {
            tui = null;
            qzMul = null;
        } else {
            t = objectUtil.getInt("tui", Tui.TUI0.getMul());
            tui = Tui.valueOf(t, extension);
            t = objectUtil.getInt("qz", QZMul.QZ1.getMul());
            qzMul = QZMul.valueOf(t, extension);
        }
        t = objectUtil.getInt("rule", DouRule.DR1.getCode());
        rule = DouRule.valueOf(t, extension);
        AA = objectUtil.getBool("AA", false);
        shunziniu = objectUtil.getBool("szn", true);
        wuhuaniu = objectUtil.getBool("whn", true);
        tonghuaniu = objectUtil.getBool("thn", true);
        huluniu = objectUtil.getBool("hln", true);
        zhadanniu = objectUtil.getBool("zdn", true);
        xiaoshuainiu = objectUtil.getBool("xsn", true);
        entryAfterStart = objectUtil.getBool("eas", true);
        twist = objectUtil.getBool("twi", true);
        betLimit = this.type != GameType.niuniushangzhuang && objectUtil.getBool("bl", false);
        ma = this.type != GameType.barenmingpai && this.type != GameType.gongpainiuniu && objectUtil.getBool("ma", false);
        joker = this.type != GameType.niuniushangzhuang && this.type != GameType.ziyouqiangzhuang && objectUtil.getBool("joker", false);
        seatData = new GameData[getMaxPerson()];
        lookUser = new ArrayList<>();
        joinedUser = new LinkedList<>();
        curGames = new ArrayList<>();
        bankers = new ArrayList<>();
    }
    protected void analyzeHand(GameData data) {
        if (data.getHand().size() != 5)
            return;
        int joker = 0;
        List<Byte> hand = data.getHand();
        List<Byte> temp;
        Collections.sort(hand);
        data.setMax(hand.get(hand.size() - 1));
        Iterator<Byte> it = hand.iterator();
        while (it.hasNext()) {
            if (PokerUtil.getPokerColor(it.next()) > 3) {
                joker++;
                it.remove();
            }
        }
        //小帅牛
        boolean touhua = true;
        if (xiaoshuainiu) {
            temp = new ArrayList<>(hand);
            boolean xsn = true;
            for (int i = 0; i < temp.size() - 1; i++) {
                byte color = PokerUtil.getPokerColor(temp.get(i));
                byte face = PokerUtil.getPokerFace(temp.get(i));
                if (color != PokerUtil.getPokerColor(temp.get(i + 1))) {
                    xsn = false;
                    touhua = false;
                    break;
                }
                if (face != PokerUtil.getPokerFace(temp.get(i + 1))) {
                    if (face >= 13) {
                        xsn = false;
                        break;
                    }
                    if (joker > 0) {
                        temp.add(i + 1, PokerUtil.generateSingleCard(color, (byte) (face + 1)));
                        joker--;
                    } else {
                        xsn = false;
                        break;
                    }
                }
            }
            if (xsn) {
                data.setType(PaiType.xiaoshuainiu);
                return;
            }
        }
        if (zhadanniu || huluniu) {
            Map<Byte, Integer> faceCount = new HashMap<>();
            for (Byte h : hand) {
                byte face = PokerUtil.getPokerFace(h);
                faceCount.merge(face, 1, (a, b) -> a + b);
            }
            if (faceCount.size() == 2) {
                if (zhadanniu) {
                    for (int v : faceCount.values()) {
                        if (4 - v <= joker) {
                            data.setType(PaiType.zhadanniu);
                            return;
                        }
                    }
                }
                if (huluniu) {
                    for (int v : faceCount.values()) {
                        if (3 - v <= joker) {
                            data.setType(PaiType.huluniu);
                            return;
                        }
                    }
                }
            }
        }
        if (tonghuaniu) {
            if (touhua) {
                data.setType(PaiType.tonghuaniu);
                return;
            }
        }
        if (wuhuaniu) {
            boolean wuhua = true;
            for (byte b : hand) {
                byte face = PokerUtil.getPokerFace(b);
                if (face < 10) {
                    wuhua = false;
                    break;
                }
            }
            if (wuhua) {
                data.setType(PaiType.wuhuaniu);
                return;
            }
        }
        if (zhadanniu) {
            for (int i = 0; i < hand.size(); i++) {
                byte face = PokerUtil.getPokerFace(hand.get(i));
                int sameSize = 0;
                for (Byte h : hand) {
                    byte f = PokerUtil.getPokerFace(h);
                    if (f == face)
                        sameSize++;
                }
                if (sameSize + joker >= 4) {
                    data.setType(PaiType.zhadanniu);
                    return;
                }
            }
        }
        if (shunziniu) {
            boolean szn = true;
            temp = new ArrayList<>(hand);
            for (int i = 0; i < temp.size() - 1; i++) {
                byte curface = PokerUtil.getPokerFace(temp.get(i));
                byte nextFace = PokerUtil.getPokerFace(temp.get(i + 1));
                if (curface != nextFace - 1) {
                    if (joker > 0) {
                        temp.add(i + 1, PokerUtil.generateSingleCard(PokerUtil.getPokerColor(temp.get(i)), nextFace));
                        joker--;
                    } else {
                        szn = false;
                        break;
                    }
                }
            }
            if (szn) {
                data.setType(PaiType.shunziniu);
                return;
            }
        }
        int totalValue = 0;
        int value = -1;
        for (byte b : hand)
            totalValue += PokerUtil.getPokerValue(b);
        //有两个王,可以任意组成牛牛
        if (joker == 2) {
            data.setType(PaiType.niuniu);
            return;
        } else if (joker == 1) {
            //如果带一张王,手牌中任意三张成牛,则可以组成牛牛
            for (Byte h : hand) {
                int v = PokerUtil.getPokerValue(h);
                if ((totalValue - value) % 10 == 0) {
                    data.setType(PaiType.niuniu);
                    return;
                }
            }
            //如果任意三张不能成牛,取两张牌之和取余的最大值
            for (int i = 0; i < hand.size() - 1; i++) {
                for (int j = 0; j < hand.size(); j++) {
                    int valueI = PokerUtil.getPokerValue(hand.get(i));
                    int valueJ = PokerUtil.getPokerValue(hand.get(j));
                    int total = (valueI + valueJ) % 10;
                    if (value == -1)
                        value = total;
                    else if (total > value)
                        value = total;
                }
            }
        } else { //没有王,即没有癞子
            outloop:
            for (int i = 0; i < hand.size() - 2; i++) {
                for (int j = i + 1; j < hand.size() - 1; j++) {
                    int value1 = PokerUtil.getPokerValue(hand.get(i));
                    int value2 = PokerUtil.getPokerValue(hand.get(j));
                    int total = value1 + value2;
                    if ((totalValue - total) % 10 == 0) {
                        value = total%10;
                        break outloop;
                    }
                }
            }
        }
        if (value == -1) {
            data.setType(PaiType.meiniu);
            return;
        }
        switch (value) {
            case 0:
                data.setType(PaiType.niuniu);
                break;
            case 1:
                data.setType(PaiType.niu1);
                break;
            case 2:
                data.setType(PaiType.niu2);
                break;
            case 3:
                data.setType(PaiType.niu3);
                break;
            case 4:
                data.setType(PaiType.niu4);
                break;
            case 5:
                data.setType(PaiType.niu5);
                break;
            case 6:
                data.setType(PaiType.niu6);
                break;
            case 7:
                data.setType(PaiType.niu7);
                break;
            case 8:
                data.setType(PaiType.niu8);
                break;
            case 9:
                data.setType(PaiType.niu9);
                break;
        }
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    void initNewGame() {
        pokers = PokerUtil.generateSuitCard(joker);
        curRound++;
    }
    public List<User> getJoinedUser() {
        return joinedUser;
    }
    public Round getRound() {
        return round;
    }
    public boolean isAA() {
        return !AA;
    }
    boolean allBet() {
        for (GameData data : curGames) {
            if (data.getBet() == 0 || data.getMaBet() < 0) {
                return false;
            }
        }
        return true;
    }
    boolean allShow() {
        for (GameData data : curGames) {
            if (data.isShow())
                return false;
        }
        showAction = false;
        return true;
    }
    public int getMaxPerson() {
        if (type == GameType.barenmingpai)
            return 8;
        else
            return 6;
    }
    public int getCost() {
        if (round == Round.R1) {
            if (!AA) {
                if (type == GameType.barenmingpai)
                    return 5;
                else return 4;
            } else
                return 1;
        } else if (round == Round.R2) {
            if (!AA) {
                if (type == GameType.barenmingpai)
                    return 8;
                else return 6;
            } else return 2;
        } else {
            if (!AA) {
                if (type == GameType.barenmingpai)
                    return 12;
                else return 9;
            } else return 3;
        }
    }
    boolean checkStart(boolean auto) {
        curGames.clear();
        for (GameData data : seatData) {
            if (data != null) {
                if (data.isReady())
                    curGames.add(data);
                else
                    return false;
            }
        }
        if (!auto) {
            return curGames.size() >= 2;
        }
        switch (autoStart) {
            case START4:
            case START5:
            case START6:
            case START7:
            case START8:
                if (curGames.size() >= autoStart.getNum())
                    return true;
        }
        return false;
    }
    public int getCurPerson() {
        int n = 2;
        for (GameData data : seatData) {
            if (data != null)
                n++;
        }
        return n;
    }
    void takeSeat(GameData gameData) {
        for (int i = 0; i < seatData.length; i++) {
            if (seatData[i] == null) {
                seatData[i] = gameData;
                seatData[i].setSeat(i);
                break;
            }
        }
    }
    GameData getGamerByUid(int uid){
        for (GameData data:curGames){
            if (data.getAccount().getUid() == uid)
                return data;
        }
        return null;
    }
    boolean allQZ(){
        for (GameData data:curGames){
            if (data.getQz() == 0)
                return false;
        }
        return true;
    }
    public void setOwnerId(long uid){
        this.ownerId = uid;
    }
    void initNextGame(){
        for (GameData data:curGames){
           data.initNextGame();
        }
    }
}
