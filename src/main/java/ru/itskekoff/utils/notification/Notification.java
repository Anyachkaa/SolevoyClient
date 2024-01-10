package ru.itskekoff.utils.notification;

import lombok.Data;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import ru.itskekoff.utils.ScreenHelper;
import ru.itskekoff.utils.TimerHelper;

public @Data class Notification {
    public final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    public static final int HEIGHT = 30;
    private final String title;
    private final String content;
    private final int time;
    private final TimerHelper timer;
    private final FontRenderer fontRenderer;
    public double x = sr.getScaledWidth();
    public double y = sr.getScaledHeight();
    public double notificationTimeBarWidth;
    private final ScreenHelper screenHelper;

    public Notification(String title, String content, int second, FontRenderer fontRenderer) {
        this.title = title;
        this.content = content;
        this.time = second;
        this.timer = new TimerHelper();
        this.fontRenderer = fontRenderer;
        this.screenHelper = new ScreenHelper((sr.getScaledWidth() - getWidth() + getWidth()), (sr.getScaledHeight() - 60));
    }

    public final int getWidth() {
        return Math.max(100, Math.max(this.fontRenderer.getStringWidth(this.title), this.fontRenderer.getStringWidth(this.content)) + 90);
    }

}