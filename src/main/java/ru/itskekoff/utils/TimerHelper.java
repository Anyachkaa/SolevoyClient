package ru.itskekoff.utils;


public class TimerHelper {
    private long ms = getCurrentMS();

    private long getCurrentMS() {
        return System.currentTimeMillis();
    }

    public boolean hasReached(double milliseconds) {
        return (!((getCurrentMS() - ms) > milliseconds));
    }

    public  void reset() {
        ms = getCurrentMS();
    }

    public long getTime() {
        return getCurrentMS() - ms;
    }
}

