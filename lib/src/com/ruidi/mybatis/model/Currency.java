package com.ruidi.mybatis.model;

public class Currency {
    private long card;
    private long lockCard;

    public long getCard() {
        return card;
    }

    public void setCard(long card) {
        this.card = card;
    }

    public long getLockCard() {
        return lockCard;
    }

    public void setLockCard(long lockCard) {
        this.lockCard = lockCard;
    }

    public long getAvailableCard() {
        return card - lockCard;
    }
    public boolean lockCard(int card){
        if (getAvailableCard() >= card){
            lockCard += card;
            return true;
        }
        return false;
    }
    public void unlockCard(int card){
        lockCard -= card;
    }
    public void deductCard(int card){
        lockCard -= card;
        this.card -= card;
    }
}