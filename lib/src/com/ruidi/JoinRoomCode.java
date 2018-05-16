package com.ruidi;

public enum JoinRoomCode {
    SUCCESS(0),NO_SUCH_ROOM(1),FORBIDDEN(2),JOIN_FAIL(3),ALREADY_JOIN(4),JOINED_OTHER_ROOM(5);
    private int code;
    JoinRoomCode(int i) {
        code = i;
    }

    public int getCode() {
        return code;
    }
}
