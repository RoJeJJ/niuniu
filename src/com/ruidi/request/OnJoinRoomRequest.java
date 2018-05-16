package com.ruidi.request;

import com.ruidi.JoinRoomCode;
import com.ruidi.gamedata.GameLogic;
import com.ruidi.utils.NNRoomManager;
import com.ruidi.utils.SFSObjectUtil;
import com.ruidi.utils.Utils;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.locks.ReentrantLock;

public class OnJoinRoomRequest extends BaseClientRequestHandler {
    @Override
    public void handleClientRequest(User user, ISFSObject isfsObject) {
        SFSObjectUtil sfsObjectUtil = new SFSObjectUtil(isfsObject, getParentExtension());
        String name = sfsObjectUtil.getUTFString("name", "null");
        Room room = getParentExtension().getParentZone().getRoomByName(name);
        if (room != null && room.isActive()) {
            if (StringUtils.equals("normal", room.getGroupId()))
            // TODO: 2018/5/11  加入俱乐部房间条件
            {
                GameLogic gameLogic = NNRoomManager.getLogic(room);
                ReentrantLock joinLock = (ReentrantLock) user.getProperty("joinLock");
                if (joinLock.tryLock()) {
                    gameLogic.onJoin(user);
                    joinLock.unlock();
                }
                else
                    return;
            }
        }
        //房间不存在
        Utils.sendJoinRoom(getParentExtension(),user,JoinRoomCode.NO_SUCH_ROOM);
    }
}
