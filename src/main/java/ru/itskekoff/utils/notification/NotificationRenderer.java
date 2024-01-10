package ru.itskekoff.utils. notification;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.math.MathHelper;
import ru.itskekoff.client.SolevoyClient;
import ru.itskekoff.client.render.RenderUtils;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class NotificationRenderer {
    private static final List<Notification> notifications = new CopyOnWriteArrayList<>();

    public static void queue(String title, String content, int second) {
        notifications.add(new Notification(title, content, second * 1000, Minecraft.getMinecraft().fontRenderer));
    }

    public static void publish(ScaledResolution sr) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!notifications.isEmpty()) {
            if (SolevoyClient.getInstance().getModuleManager().getModule("Notification").isToggled()) {
                int y = sr.getScaledHeight() - 40;
                double better;
                for (Notification notification : notifications) {
                    if (mc.world != null && mc.player != null) {
                        better = Minecraft.getMinecraft().fontRenderer.getStringWidth(notification.getTitle() + " " + notification.getContent());

                        if (notification.getTimer().hasReached((float) notification.getTime() / 2)) {
                            notification.notificationTimeBarWidth = 360;
                        } else {
                            notification.notificationTimeBarWidth = MathHelper.EaseOutBack((float) notification.notificationTimeBarWidth, 0, (float) (4 * deltaTime()));
                        }

                        if (notification.getTimer().hasReached(notification.getTime())) {
                            notification.x = MathHelper.EaseOutBack((float) notification.x, (float) (notification.sr.getScaledWidth() - better), (float) (5 * deltaTime()));
                            notification.y = MathHelper.EaseOutBack((float) notification.y, (float) y, (float) (5 * deltaTime()));
                        } else {
                            notification.x = MathHelper.EaseOutBack((float) notification.x, (float) (notification.sr.getScaledWidth() + 50), (float) (5 * deltaTime()));
                            notification.y = MathHelper.EaseOutBack((float) notification.y, (float) y, (float) (5 * deltaTime()));
                            if (notification.x > notification.sr.getScaledWidth() + 24 && mc.player != null && mc.world != null && !GameSettings.showDebugInfo) {
                                notifications.remove(notification);
                            }
                        }
                        GlStateManager.pushMatrix();
                        GlStateManager.disableBlend();
                        RenderUtils.drawSmoothRect(notification.x - 30, notification.y - 13, notification.sr.getScaledWidth() - 5, notification.y + 12.0f, new Color(0, 0, 0, 195));
                        Minecraft.getMinecraft().fontRenderer.drawString(notification.getTitle(), (int) (notification.x - 26), (int) (notification.y - 9), -1);
                        RenderUtils.drawSmoothRect(notification.x - 30, notification.y + 1, notification.sr.getScaledWidth() - 5, notification.y + 1, Color.gray);
                        Minecraft.getMinecraft().fontRenderer.drawString(notification.getContent(), (int) (notification.x - 26), (int) (notification.y + 2), -1);
                        GlStateManager.popMatrix();
                        y -= 30;
                    } else {
                        notifications.remove(notification);
                    }
                }
            }
        }
    }


    public static double deltaTime() {
        return Minecraft.getDebugFPS() > 0 ? (1.0000 / Minecraft.getDebugFPS()) : 1;
    }
}