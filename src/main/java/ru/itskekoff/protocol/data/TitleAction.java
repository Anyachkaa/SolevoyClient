package ru.itskekoff.protocol.data;

import lombok.Getter;

@Getter
public enum TitleAction {
    TITLE, SUBTITLE, TIMES, HIDE, ACTIONBAR, RESET;

    public static TitleAction getById(int id, int protocol) {
        return switch (id) {
            case 0 -> TITLE;
            case 1 -> SUBTITLE;
            case 2 -> ACTIONBAR;
            case 3 -> TIMES;
            case 4 -> HIDE;
            case 5 -> RESET;
            default -> RESET;
        };
    }

    public int getIdByProtocol(int protocol) {
        return switch (this) {
            case TITLE -> 0;
            case SUBTITLE -> 1;
            case ACTIONBAR -> 2;
            case TIMES -> 3;
            case HIDE -> 4;
            case RESET -> 5;
        };
    }
}
