package com.ruidi.event;

import com.ruidi.mybatis.MyBatisHelper;
import com.ruidi.utils.RoomNameManager;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class ServerReadyEvent extends BaseServerEventHandler{
    @Override
    public void handleServerEvent(ISFSEvent isfsEvent) throws SFSException {
        MyBatisHelper.init(getParentExtension().getConfigProperties());
        RoomNameManager.init();
    }
}
