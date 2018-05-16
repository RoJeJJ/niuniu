package com.ruidi.request;

import com.ruidi.mybatis.model.Account;
import com.ruidi.utils.NNUserManager;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

public class OnUserInfoRequest extends BaseClientRequestHandler{
    @Override
    public void handleClientRequest(User user, ISFSObject isfsObject) {
        Account account = NNUserManager.getAccount(user);
        ISFSObject object = account.toSFSObject();
        send("userInfo",object,user);
    }
}
