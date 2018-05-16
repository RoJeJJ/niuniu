package com.ruidi.event;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ruidi.mybatis.model.Account;
import com.ruidi.mybatis.service.AccountService;
import com.ruidi.mybatis.service.ServerConfigService;
import com.ruidi.utils.HttpConnManager;
import com.ruidi.utils.NNUserManager;
import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSConstants;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSErrorCode;
import com.smartfoxserver.v2.exceptions.SFSErrorData;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.exceptions.SFSLoginException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import com.smartfoxserver.v2.extensions.SFSExtension;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

public class OnLoginEvent extends BaseServerEventHandler{
    @Override
    public void handleServerEvent(ISFSEvent isfsEvent) throws SFSException {
        String openId = (String) isfsEvent.getParameter(SFSEventParam.LOGIN_NAME);
        String encryptPass = (String) isfsEvent.getParameter(SFSEventParam.LOGIN_PASSWORD);
        ISFSObject data = (ISFSObject) isfsEvent.getParameter(SFSEventParam.LOGIN_IN_DATA);
        ISFSObject outData = (ISFSObject) isfsEvent.getParameter(SFSEventParam.LOGIN_OUT_DATA);
        ISession session = (ISession) isfsEvent.getParameter(SFSEventParam.SESSION);
        String token = data.getUtfString("token");

        //查询系统维护
        if (ServerConfigService.loginEnable() != 1){
            throwSFSLoginException("系统维护!");
        }
        //验证密码
        String psw = ServerConfigService.password();
        if (StringUtils.isBlank(psw) || !getApi().checkSecurePassword(session,psw,encryptPass)){
            throwSFSLoginException("密码错误!");
        }
        Account account;
        if (token == null){
            account = new Account();
            account.setSex(data.getInt("sex"));
            account.setNickname(data.getUtfString("nickname"));
            account.setOpenid(openId);
            account.setUnionid(openId);
            account.setHeadimgurl(data.getUtfString("headimg"));
        }else {
            //token验证
            if ("".equals(token)) {
                throwSFSLoginException("token错误!");
            }
            JsonObject info = null;
            try {
                info = wxInfo(openId, token);
            } catch (Exception e) {
                throwSFSLoginException("微信验证失败!");
            }
            if (info == null || info.has("errcode")) {
                throwSFSLoginException("微信token验证失败!");
            }
            account = new Gson().fromJson(info, Account.class);
        }
        account = AccountService.verifyAccount(account);
        if (account == null){
            throwSFSLoginException("账户创建失败,请联系管理员!");
        }else {
            if (!account.isLoginEnable())
                throwSFSLoginException("账户被禁止登录,请联系管理员");
            account.setIp(session.getAddress());
            NNUserManager.setAccount(session,account);
            String userName = String.format(account.getNickname() + "{%d}", account.getUid());
            outData.putUtfString(SFSConstants.NEW_LOGIN_NAME, userName);
            AccountService.setOnline(true);
        }
    }

    private JsonObject wxInfo(String openId, String token) throws Exception {
        HttpClient client = HttpConnManager.getHttpClient();
        String url = String.format(getWXUrl(getParentExtension()),token,openId);
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        String reader = EntityUtils.toString(response.getEntity(),"UTF-8");
        return new JsonParser().parse(reader).getAsJsonObject();
    }

    private String getWXUrl(SFSExtension extension){
        return extension.getConfigProperties().getProperty("USERINFO_URI");
    }

    private void throwSFSLoginException(String msg) throws SFSLoginException{
        SFSErrorData errorData = new SFSErrorData(SFSErrorCode.GENERIC_ERROR);
        errorData.addParameter(msg);
        throw new SFSLoginException(msg,errorData);
    }
}
