package com.ruidi.gamedata;


import com.ruidi.gamedata.var.TabVar;
import com.ruidi.mybatis.model.Account;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameData {
    private Room room;
    private int seat;
    private int bet;
    private List<Byte> hand;
    private Account account;
    private User user;
    private boolean show = false;
    private TabVar.PaiType type;
    private int max;
    private byte[] sendHand;
    private Map<GameData,Integer> ma;//玩家买码
    private int maBet = -1; // 小于0 未选择, 0 不买,大于0 买好了
    private int qz = 0; // 0 未选择,1 抢庄,2 不抢
    private boolean deducted;//是否扣费
    private int points;//当局输赢
    private int total;//总输赢
    private int qzCount;
    private int tuiCount;
    private int bankerCount;
    private boolean connected;

    public void setSendHand() {
        sendHand = ArrayUtils.toPrimitive(hand.toArray(new Byte[hand.size()]));
    }

    public byte[] getSendHand() {
        return sendHand;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    private int score;
    private boolean ready;
    public GameData(Room room){
        this.room = room;
        seat = -1;
        hand = new ArrayList<>();
        ma = new HashMap<>();
        deducted = false;
        connected = true;
    }
    public void takeBanker(){
        bankerCount++;
    }
    public void qz(){
        qzCount++;
    }

    public List<Byte> getHand() {
        return hand;
    }

    public Room getRoom() {
        return room;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    public ISFSObject toSFSObject(){
        ISFSObject object = new SFSObject();
        object.putInt("seat",seat);
        object.putBool("ready",ready);
        object.putInt("score",score);
        return object;
    }


    public void setBet(int bet) {
        this.bet = bet;
    }

    public int getBet() {
        return bet;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TabVar.PaiType getType() {
        return type;
    }

    public void setType(TabVar.PaiType type) {
        this.type = type;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public boolean isShow() {
        return !show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public int getMaBet() {
        return maBet;
    }

    public void setMaBet(int maBet) {
        this.maBet = maBet;
    }

    public Map<GameData, Integer> getMa() {
        return ma;
    }
    public void win(int s){
        score +=s;
        points +=s;
        total += s;
    }
    public void lose(int s){
        score -= s;
        points -= s;
        total -= s;
    }
    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getQz() {
        return qz;
    }

    public void setQz(int qz) {
        this.qz = qz;
    }

    public boolean isDeducted() {
        return deducted;
    }
    public void setDeducted(boolean deducted) {
        this.deducted = deducted;
    }
    public void initNextGame() {
        bet = 0;
        hand.clear();
        show = false;
        ma.clear();
        maBet = 0;
        qz = 0;
        ready = false;
        points = 0;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
