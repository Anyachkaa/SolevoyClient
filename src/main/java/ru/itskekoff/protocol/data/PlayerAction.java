package ru.itskekoff.protocol.data;

public enum PlayerAction {
    START_DIGGING,
    CANCEL_DIGGING,
    FINISH_DIGGING,
    DROP_ITEM_STACK,
    DROP_ITEM,
    RELEASE_USE_ITEM,
    SWAP_HANDS;

    private PlayerAction() {
    }
}
