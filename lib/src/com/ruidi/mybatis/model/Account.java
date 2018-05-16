package com.ruidi.mybatis.model;

import com.google.gson.annotations.SerializedName;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class Account {
    @SerializedName("uid")
    private long uid;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("openid")
    private String openid;
    @SerializedName("unionid")
    private String unionid;
    @SerializedName("headimgurl")
    private String headimgurl;
    @SerializedName("province")
    private String province;
    @SerializedName("city")
    private String city;
    @SerializedName("country")
    private String country;
    @SerializedName("language")
    private String language;
    @SerializedName("sex")
    private int sex;
    private long registerTime;
    private long lastLoginTime;
    private int agentid;
    private Currency currency;
    private boolean loginEnable;
    private String ip;
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public int getAgentid() {
        return agentid;
    }

    public void setAgentid(int agentid) {
        this.agentid = agentid;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public boolean isLoginEnable() {
        return loginEnable;
    }
    public void setLoginEnable(boolean loginEnable) {
        this.loginEnable = loginEnable;
    }
    public String getUnionid() {
        return unionid;
    }
    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
    public ISFSObject toSFSObject(){
        ISFSObject object = new SFSObject();
        object.putLong("uid",uid);
        object.putUtfString("nick",nickname);
        object.putUtfString("headimg",headimgurl);
        object.putUtfString("ip",ip);
        object.putLong("regTime",registerTime);
        object.putLong("card",getCard());
        object.putInt("sex",sex);
        return object;
    }
    public long getCard(){
        return currency.getCard();
    }
    public synchronized boolean lockCard(int card){
        return currency.lockCard(card);
    }
    public synchronized void unlockCard(int card){
        currency.unlockCard(card);
    }
    public synchronized void deductCard(int card){
        currency.deductCard(card);
    }
}
