package ru.itskekoff.bots.utils;

import lombok.Data;

public @Data class Timer {
    public int elapsedTicks;
    public float syncSysClock;

    public float timerSpeed = 1.0F;

    private long lastSyncSysClock;
    private float tps;

    public Timer(float tps) {
        this.tps = 1000.0F / tps;
        this.lastSyncSysClock = System.currentTimeMillis();
    }

    public void updateTimer() {
        long i = System.currentTimeMillis();
        this.syncSysClock = (float) (i - this.lastSyncSysClock) * this.timerSpeed / this.tps;
        this.lastSyncSysClock = i;
        this.elapsedTicks += this.syncSysClock;
    }
}
