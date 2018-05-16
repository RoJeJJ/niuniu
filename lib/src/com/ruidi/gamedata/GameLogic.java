package com.ruidi.gamedata;

import com.ruidi.JoinRoomCode;
import com.ruidi.SeatCode;
import com.ruidi.mybatis.model.Account;
import com.ruidi.mybatis.service.AccountService;
import com.ruidi.utils.NNUserManager;
import com.ruidi.utils.Utils;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSJoinRoomException;
import com.smartfoxserver.v2.extensions.SFSExtension;
import com.smartfoxserver.v2.util.TaskScheduler;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class GameLogic extends SendTabMsg {
    private final ReentrantLock GAME_LOCK = new ReentrantLock();
    private final ReentrantLock BET_LOCK = new ReentrantLock();
    private final ReentrantLock SHOW_LOCK = new ReentrantLock();
    private final ReentrantLock QZ_LOCK = new ReentrantLock();
    private TaskScheduler taskScheduler;

    public GameLogic(ISFSObject object, SFSExtension extension) {
        super(object, extension);
    }

    public void setExtension(SFSExtension extension) {
        super.setExtension(extension);
        taskScheduler = SmartFoxServer.getInstance().getTaskScheduler();
    }

    public void onJoin(User user) {
        GameData data = NNUserManager.getGameData(user);
        if (data != null) {
            if (data.getRoom() == room) {
                Utils.sendJoinRoom(extension, user, JoinRoomCode.ALREADY_JOIN);
                return;
            } else {
                Utils.sendJoinRoom(extension, user, JoinRoomCode.JOINED_OTHER_ROOM);
                return;
            }
        }
        GAME_LOCK.lock();
        if (!entryAfterStart && gameStart) {
            Utils.sendJoinRoom(extension, user, JoinRoomCode.FORBIDDEN);
            GAME_LOCK.unlock();
            return;
        }
        if (GAME_LOCK.isLocked())
            GAME_LOCK.unlock();
        try {
            GameData newData = new GameData(room);
            NNUserManager.setGameData(user, newData);
            extension.getApi().joinRoom(user, room);
        } catch (SFSJoinRoomException e) {
            e.printStackTrace();
            NNUserManager.removeGameData(user);
            Utils.sendJoinRoom(extension, user, JoinRoomCode.JOIN_FAIL);
        }
    }

    public void seatDown(User user) {
        GameData data = NNUserManager.getGameData(user);
        GAME_LOCK.lock();
        if (data.getRoom() != room) {
            Utils.sendSeatDown(extension, user, SeatCode.OUT_OF_ROOM);
            GAME_LOCK.unlock();
            return;
        }
        if (data.getSeat() != -1) {
            Utils.sendSeatDown(extension, user, SeatCode.ALREADY_SEAT);
            GAME_LOCK.unlock();
            return;
        }
        if (getCurPerson() == getMaxPerson()) {
            Utils.sendSeatDown(extension, user, SeatCode.ROOM_FULL);
            GAME_LOCK.unlock();
            return;
        }
        if (AA) {
            if (data.getAccount().lockCard(getCost())) {
                Utils.sendSeatDown(extension, user, SeatCode.NO_MORE_CARD);
                GAME_LOCK.unlock();
                return;
            }
        }
        //坐下
        takeSeat(data);
        taskScheduler.schedule(()-> AccountService.lockCard(data.getAccount()),0, TimeUnit.MILLISECONDS);
        Utils.sendSeatDown(extension,user,SeatCode.SUCCESS);

        //通知其他人,房间人数更新
        sendPersonCountChange();

        if (checkStart(true)){
            gameStart = true;
            GAME_LOCK.unlock();
            startGame();
        }
        if (GAME_LOCK.isLocked())
            GAME_LOCK.unlock();
    }

    private void startGame() {
        initNewGame();
        sendStartGame();
        if (type == GameType.niuniushangzhuang){
            if (banker == null){
                int index = new Random().nextInt(curGames.size());
                banker = curGames.get(index);
                sendBanker(banker,true);
                Utils.sleep(RANDOM_BANKER_ANIMATION);
            }else{
                sendBanker(banker,false);
            }
            banker.takeBanker();
            Utils.sleep(1000);
            gameBet();
        }else if (type == GameType.ziyouqiangzhuang){
            qzAction = true;
            start_qz_time = System.currentTimeMillis();
            sendQZAction();
            taskScheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    QZ_LOCK.lock();
                    for (GameData data:curGames){
                        if (data.getQz() == 0) {
                            data.setQz(-1);
                            sendQZ(data);
                        }
                    }
                    boolean aqz = allQZ();
                    if (aqz)
                        qzAction = false;
                    QZ_LOCK.unlock();
                    if (aqz)
                        randomBanker();
                }
            }, (int) QZ_ACTION_TIME,TimeUnit.MILLISECONDS);
        }
    }
    private void gameBet() {
        start_bet_time = System.currentTimeMillis();
        betAction = true;
        sendBetAction();
        taskScheduler.schedule(() -> {
            BET_LOCK.lock();
            betAction = false;
            for (GameData data:curGames){
                if (data.getBet() == 0) {
                    data.setBet(baseScore.getBase());
                    sendBet(data,data);
                }
                if (data.getMaBet() < 0)
                    data.setMaBet(0);
            }
            BET_LOCK.unlock();
            dealCard();
        }, (int) BET_ACTION_TIME,TimeUnit.MILLISECONDS);
    }
    public void onBet(GameData data, int bet,int uid) {
        if (!betAction)
            return;
        GameData target = getGamerByUid(uid);
        if (target == null)
            return;
        if (!ma && data != target)
            return;
        BET_LOCK.lock();
        boolean ma = false;
        if (target == data){ //自己下注
           if (bet == 0){
               if (data.getMaBet() < 0){
                   ma = true;
                   data.setMaBet(0);
               }
           }else if (data.getBet() == 0 && (bet == baseScore.getBase() || bet == baseScore.getBase() * 2 /*todo 推注*/)){
                ma = true;
                data.setBet(bet);
               data.setMaBet(0);
                sendBet(data,target);
            }
        }else if (data.getMaBet() < 0){
            if (bet == baseScore.getBase() || bet == baseScore.getBase() * 2){
                ma = true;
                data.setMaBet(bet);
                target.getMa().put(data,bet);
                sendBet(data,target);
            }
        }
        if (ma){
            boolean ab = allBet();
            if (ab)
                betAction = false;
            BET_LOCK.unlock();
            if (ab)
                dealCard();
        }else
            BET_LOCK.unlock();
    }
    public void onQiangZhuang(GameData data,boolean q) {
        QZ_LOCK.lock();
        if (!qzAction)
            return;
        boolean qz = false;
        if (data.getQz() == 0){
            qz = true;
            data.setQz(q?1:-1);
            if (data.getQz() == 1) {
                bankers.add(data);
                data.qz();
            }
            sendQZ(data);
        }
        if (qz){
            boolean aqz = allQZ();
            if (aqz)
                qzAction = false;
            QZ_LOCK.unlock();
            if (aqz)
                randomBanker();
        }else
            QZ_LOCK.unlock();
    }

    private void randomBanker() {
        if (bankers.size() == 0){
            int index = new Random().nextInt(curGames.size());
            banker = curGames.get(index);
            sendBanker(banker,true);
            Utils.sleep(RANDOM_BANKER_ANIMATION);
        }else if (bankers.size() == 1){
            banker = bankers.get(0);
            sendBanker(banker,false);
        }else {
            int index = new Random().nextInt(bankers.size());
            banker = bankers.get(index);
            sendBanker(banker,true);
            Utils.sleep(RANDOM_BANKER_ANIMATION);
        }
        banker.takeBanker();
        gameBet();
    }

    private void dealCard() {
        if (type == GameType.niuniushangzhuang || type == GameType.ziyouqiangzhuang){
            start_show_hand = System.currentTimeMillis();
            for (GameData data:curGames){
                data.getHand().clear();
                for (int i=0;i<5;i++) {
                    data.getHand().add(pokers.remove(0));
                }
                data.setSendHand();
                analyzeHand(data);
            }
            showAction = true;
            start_show_hand = System.currentTimeMillis();
            sendDeal();
            taskScheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    SHOW_LOCK.lock();
                    showAction = false;
                    for (GameData data:curGames){
                        if (!data.isShow()){
                            data.setShow(true);
                            sendShow(data);
                        }
                    }
                    SHOW_LOCK.unlock();
                    settlement();
                }
            }, (int) (SHOW_HAND_TIME),TimeUnit.MILLISECONDS);
        }else {
            for (GameData data:curGames){
                data.getHand().clear();
                for (int i=0;i<4;i++) {
                    data.getHand().add(pokers.remove(0));
                }
                //发送给客户端
                ISFSObject object = new SFSObject();
                object.putBool("self",true);
                object.putByteArray("hand", data.getSendHand());
                object.putLong("show",SHOW_HAND_TIME);
                extension.send("deal",object,data.getUser());
            }
        }
    }

    public void showHand(GameData data) {
        SHOW_LOCK.lock();
        if (!showAction)
            return;
        boolean show = false;
        if (!data.isShow()){
            show = true;
            data.setShow(true);
            sendShow(data);
        }
        if (show){
            boolean as = allShow();
            if (as)
                showAction = false;
            SHOW_LOCK.unlock();
            if (as)
                settlement();
        }else
            SHOW_LOCK.unlock();
    }
    private void settlement() {
        for (GameData data : curGames) {
            boolean win;
            if (data != banker) {
                win = data.getType().getCode() > banker.getType().getCode()
                        || data.getType().getCode() >= banker.getType().getCode() && data.getMax() > banker.getMax();
                if (win) {
                    int s = data.getBet() * data.getType().getTime(rule);
                    data.win(s);
                    banker.lose(s);
                    if (data.getMa().size() == 1) {
                        Map.Entry<GameData, Integer> entry = data.getMa().entrySet().iterator().next();
                        s = entry.getValue() * data.getType().getTime(rule);
                        entry.getKey().win(s);
                        banker.lose(s);
                    }
                } else {
                    int s = data.getBet() * banker.getType().getTime(rule);
                    data.lose(s);
                    banker.win(s);
                    if (data.getMa().size() == 1) {
                        Map.Entry<GameData, Integer> entry = data.getMa().entrySet().iterator().next();
                        s = entry.getValue() * banker.getType().getTime(rule);
                        entry.getKey().lose(s);
                        banker.win(s);
                    }
                }
            }
        }
        sendSettle();
        deductCard();
        if (curRound >= round.getRound()){
            extension.getApi().removeRoom(room);
        }else
            initNextGame();
    }


    /**
     * 扣卡
     */
    private void deductCard() {
        if (!AA && curRound == 1){
            User owner = NNUserManager.ONLINE_USER.get(ownerId);
            if (owner == null) //房主不在线
                AccountService.deductCard(ownerId,getCost());
            else{
                Account account = NNUserManager.getAccount(owner);
                account.deductCard(getCost());
                AccountService.deductCard(account);
                Utils.sendCard(extension,owner);
            }
        }
        if (AA){
            for (GameData data:curGames){
                if (!data.isDeducted()){
                    data.setDeducted(true);
                    data.getAccount().deductCard(getCost());
                    AccountService.deductCard(data.getAccount());
                    Utils.sendCard(extension,data.getUser());
                }
            }
        }
    }

    public void onStartGame(User user) {
        if (NNUserManager.getAccount(user).getUid() == ownerId)
            return;
        GAME_LOCK.lock();
        boolean s = checkStart(false);
        if (s)
            gameStart = true;
        GAME_LOCK.unlock();
        if (s){
            startGame();
        }
    }

    public void onDisconnect(GameData data) {
        for (GameData d:seatData){
            if (d == data){
                d.setConnected(false);
                sendDisconnect(data);
            }
        }
    }
}
