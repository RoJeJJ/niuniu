package com.ruidi.request;

import com.ruidi.NNGame;
import com.ruidi.gamedata.GameLogic;
import com.ruidi.utils.NNRoomManager;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;


public class OnSeatDownRequest extends BaseClientRequestHandler {
    @Override
    public void handleClientRequest(User user, ISFSObject isfsObject) {
        NNGame nnGame = (NNGame) getParentExtension();
        Room room = nnGame.getParentRoom();
        GameLogic gameLogic = NNRoomManager.getLogic(room);
        if (room.containsUser(user)){
            gameLogic.seatDown(user);
        }
    }

}
