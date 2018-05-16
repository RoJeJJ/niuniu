package com.ruidi;

public enum  SeatCode {
    SUCCESS(0),OUT_OF_ROOM(1),ALREADY_SEAT(2),ROOM_FULL(3),NO_MORE_CARD(4);
    private int code;
    SeatCode(int i) {
        code = i;
    }

    public int getCode() {
        return code;
    }
}
