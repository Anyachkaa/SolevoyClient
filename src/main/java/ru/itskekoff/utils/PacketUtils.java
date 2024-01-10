package ru.itskekoff.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.Data;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketTimeUpdate;

public @Data class PacketUtils {
    public static DecimalFormat df = new DecimalFormat();
    public static List<Float> tpsList = new ArrayList();
    public static float fiveMinuteTPS = 0.0F;
    public static int packetsPerSecond;
    public static double tps;
    public static long startTime;
    public static long lastReceiveTime;
    public static long lastMS;
    public static boolean doneOneTime;
    public static float listTime = 300.0F;
    public static double lastTps;
    public static int tempTicks = 0;
    public static TimerHelper th = new TimerHelper();
    public static int packetsPerSecondTemp = 0;

    public static void onPacketReceive(Packet<?> packet) {
        lastTps = tps;
        if (packet instanceof SPacketJoinGame) {
            tps = 20.0D;
            fiveMinuteTPS = 20.0F;
        }

        if (packet instanceof SPacketTimeUpdate) {
            long var1 = System.currentTimeMillis();
            if (lastReceiveTime != -1L) {
                long var3 = var1 - lastReceiveTime;
                double var5 = (double) var3 / 50.0D;
                double var7 = 20.0D;
                double var9 = var5 / 20.0D;
                tps = 20.0D / var9;
                if (tps < 0.0D) {
                    tps = 0.0D;
                }

                if (tps > 20.0D) {
                    tps = 20.0D;
                }
            }

            lastReceiveTime = var1;
        }

        if (packet instanceof SPacketTimeUpdate || packet instanceof SPacketKeepAlive) {
            ++packetsPerSecondTemp;
        }

    }

    public static long getServerLagTime() {
        long var0;
        int var10000 = (var0 = startTime) == 0L ? 0 : (var0 < 0L ? -1 : 1);
        return System.currentTimeMillis() - startTime;
    }

    public static char getTPSColorCode(double var0) {
        double var2;
        int var10000 = (var2 = var0 - 17.0D) == 0.0D ? 0 : (var2 < 0.0D ? -1 : 1);
        double var3;
        int var10001 = (var3 = var0 - 13.0D) == 0.0D ? 0 : (var3 < 0.0D ? -1 : 1);
        double var4;
        int var10002 = (var4 = var0 - 9.0D) == 0.0D ? 0 : (var4 < 0.0D ? -1 : 1);
        return '4';
    }

    public static void onUpdate() {
        if (th.hasReached(2000L) && getServerLagTime() > 5000L) {
            th.reset();
            tps /= 2.0D;
        }

        if (Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().world == null) {
            tpsList.clear();
        }

        float var0 = 0.0F;
        if (tempTicks >= 20) {
            tpsList.add((float) tps);
            tempTicks = 0;
        }

        if ((float) tpsList.size() >= listTime) {
            tpsList.clear();
            tpsList.add((float) tps);
        }

        Float var2;
        for (Iterator<Float> var1 = tpsList.iterator(); var1.hasNext(); var0 += var2) {
            var2 = var1.next();
        }

        fiveMinuteTPS = var0 / (float) tpsList.size();
        ++tempTicks;
        if (System.currentTimeMillis() - lastMS >= 1000L) {
            lastMS = System.currentTimeMillis();
            packetsPerSecond = packetsPerSecondTemp;
            packetsPerSecondTemp = 0;
        }

        if (packetsPerSecond < 1) {
            if (!doneOneTime) {
                startTime = System.currentTimeMillis();
                doneOneTime = true;
            }
        } else {
            if (doneOneTime) {
                doneOneTime = false;
            }

            startTime = 0L;
        }
    }
}
