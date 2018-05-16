package com.ruidi.utils;

import com.ruidi.gamedata.GameLogic;
import com.smartfoxserver.v2.api.CreateRoomSettings;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.SFSRoomRemoveMode;
import com.smartfoxserver.v2.exceptions.SFSCreateRoomException;
import com.smartfoxserver.v2.extensions.SFSExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NNRoomManager {
    private static Map<Long,List<Room>> userRooms = new ConcurrentHashMap<>();

    public static GameLogic getLogic(Room room){
        return (GameLogic) room.getProperty("nn");
    }
    private static Map<Object,Object> roomProperties(GameLogic gameLogic){
        Map<Object,Object> map = new HashMap<>();
        map.put("nn", gameLogic);
        return map;
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public static List<Room> getJoinedRooms(long uid){
        List<Room> rooms = userRooms.get(uid);
        List<Room> copyRooms;
        if (rooms != null){
            synchronized (rooms) {
                copyRooms = new ArrayList<>(rooms);
            }
        }else
            copyRooms = new ArrayList<>();

        return copyRooms;
    }
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public static void addJoinedRoom(long uid, Room room){
        List<Room> rooms = userRooms.computeIfAbsent(uid, k -> new ArrayList<>());
        synchronized (rooms){
            if (!rooms.contains(room))
                rooms.add(room);
        }
    }
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public static void removeJoinedRoom(int uid, Room room){
        List<Room> rooms = userRooms.get(uid);
        if (rooms != null)
            synchronized (rooms){
                rooms.remove(room);
            }
    }
    public static Room createRoom(GameLogic gameLogic, SFSExtension extension){
        CreateRoomSettings settings = new CreateRoomSettings();
        settings.setName(RoomNameManager.getName());
        settings.setGroupId("normal");
        settings.setAutoRemoveMode(SFSRoomRemoveMode.NEVER_REMOVE);
        settings.setMaxUsers(gameLogic.getMaxPerson());
        settings.setRoomProperties(NNRoomManager.roomProperties(gameLogic));
        CreateRoomSettings.RoomExtensionSettings extensionSettings = new CreateRoomSettings.RoomExtensionSettings("niuniu","com.ruidi.NNGame");
        settings.setExtension(extensionSettings);
        Room room = null;
        try {
            room = extension.getApi().createRoom(extension.getParentZone(),settings,null);
        } catch (SFSCreateRoomException e) {
            e.printStackTrace();
        }
        return room;
    }
}
