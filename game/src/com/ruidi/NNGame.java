package com.ruidi;

import com.ruidi.event.OnJoinRoomEvent;
import com.ruidi.gamedata.GameLogic;
import com.ruidi.request.*;
import com.ruidi.utils.NNRoomManager;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.extensions.SFSExtension;

public class NNGame extends SFSExtension {
    @Override
    public void init() {
        Room room = getParentRoom();
        GameLogic gameLogic = NNRoomManager.getLogic(room);
        gameLogic.setExtension(this);
        addEventHandler(SFSEventType.USER_JOIN_ROOM,OnJoinRoomEvent.class);

        //request
        addRequestHandler("seatdown",OnSeatDownRequest.class);
        addRequestHandler("startGame",OnStartGameRequest.class);
        addRequestHandler("bet",OnBetAndMaRequest.class);
        addRequestHandler("show",OnShowRequest.class);
        addRequestHandler("qz",OnQZRequest.class);
    }
}
