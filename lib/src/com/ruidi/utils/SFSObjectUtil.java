package com.ruidi.utils;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;


public class SFSObjectUtil {
    private ISFSObject object;
    private SFSExtension extension;
    public SFSObjectUtil(ISFSObject object, SFSExtension extension){
        this.object = object;
        this.extension = extension;
    }
    public int getInt(String key,int defaultValue){
        if (object.containsKey(key)){
            Object o = object.get(key).getObject();
            if (o instanceof Integer)
                return (int) o;
            else {
                extension.trace("没有找到键名:{} 的值",key);
                return defaultValue;
            }
        }else {
            extension.trace("没有找到键名:{}",key);
            return defaultValue;
        }
    }
    public boolean getBool(String key,boolean defaultValue){
        if (object.containsKey(key)){
            Object o = object.get(key).getObject();
            if (o instanceof Boolean)
                return (Boolean) o;
            else {
                extension.trace("没有找到键名:{} 的值",key);
                return defaultValue;
            }
        }else {
            extension.trace("没有找到键名:{}",key);
            return defaultValue;
        }
    }
    public String getUTFString(String key,String defaultValue){
        if (object.containsKey(key)){
            Object o = object.get(key).getObject();
            if (o instanceof String)
                return (String) o;
            else {
                extension.trace("没有找到键名:{} 的值",key);
                return defaultValue;
            }
        }else {
            extension.trace("没有找到键名:{}",key);
            return defaultValue;
        }
    }
}
