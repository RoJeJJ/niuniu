package com.ruidi;

import com.ruidi.event.DisconnectEvent;
import com.ruidi.event.JoinZoneEvent;
import com.ruidi.event.OnLoginEvent;
import com.ruidi.event.ServerReadyEvent;
import com.ruidi.request.OnCreateRoomRequest;
import com.ruidi.request.OnJoinRoomRequest;
import com.ruidi.request.OnRoomListRequest;
import com.ruidi.request.OnUserInfoRequest;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.SFSExtension;

public class Lobby extends SFSExtension {
    @Override
    public void init() {
        addEventHandler(SFSEventType.SERVER_READY,ServerReadyEvent.class);
        addEventHandler(SFSEventType.USER_LOGIN,OnLoginEvent.class);
        addEventHandler(SFSEventType.USER_JOIN_ZONE,JoinZoneEvent.class);
        addEventHandler(SFSEventType.USER_DISCONNECT,DisconnectEvent.class);
        //request
        addRequestHandler("roomList",OnRoomListRequest.class);
        addRequestHandler("userInfo",OnUserInfoRequest.class);
        addRequestHandler("createRoom",OnCreateRoomRequest.class);
        addRequestHandler("joinRoom",OnJoinRoomRequest.class);
    }
}
