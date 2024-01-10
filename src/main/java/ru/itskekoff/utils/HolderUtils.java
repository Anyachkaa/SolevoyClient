package ru.itskekoff.utils;

import lombok.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public @Data class HolderUtils {
    private double TPS = -1.0D;
    private long lastPacketMS = -1L;
    private TimerHelper TIME_HELPER = new TimerHelper();
    private List<Long> TPS_TIMES = new CopyOnWriteArrayList<>();
}
