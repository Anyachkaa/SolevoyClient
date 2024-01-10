package ru.itskekoff.event.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import org.lwjgl.input.Keyboard;
import ru.itskekoff.client.SolevoyClient;
import ru.itskekoff.event.EventTarget;
import ru.itskekoff.event.impl.*;
import ru.itskekoff.event.types.Priority;

import java.util.concurrent.TimeUnit;

public class SimpleHandler {

    private final SolevoyClient client = SolevoyClient.getInstance();
    private final Minecraft mc = client.mc;

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!(mc.currentScreen instanceof GuiChat)) {
            mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
            mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
            mc.gameSettings.keyBindBack.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
            mc.gameSettings.keyBindLeft.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
            mc.gameSettings.keyBindRight.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
            mc.gameSettings.keyBindSprint.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode());
        }
    }

    @EventTarget(
            value = Priority.HIGHEST
    )
    public void onKeyTyped(EventKeyType event) {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null)
            return;
        try {
            client.getModuleManager().getModuleList().forEach(module -> {
                if (module.getKey() == event.getKey()) {
                    module.toggle();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
