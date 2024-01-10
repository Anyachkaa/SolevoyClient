package ru.itskekoff.client.module.impl.misc;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.event.events.ChatEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import ru.itskekoff.client.module.Category;
import ru.itskekoff.client.module.Module;
import ru.itskekoff.event.EventTarget;
import ru.itskekoff.event.impl.EventSendMessage;
import ru.itskekoff.event.types.Priority;

public class ChatCommands extends Module {
    public ChatCommands() {
        super("ChatCommands", "Команды в чате (через точку), так же управляет командами Baritone", Category.Misc);
        setToggled(true);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventTarget(
            value = Priority.HIGHEST
    )
    public void onMessage(EventSendMessage event) {
        ChatEvent eventSend = new ChatEvent(event.getMessage());
        IBaritone baritone = BaritoneAPI.getProvider().getBaritoneForPlayer((EntityPlayerSP) (Object) event.getPlayer());
        if (baritone == null) {
            return;
        }
        baritone.getGameEventHandler().onSendChatMessage(eventSend);
        if (eventSend.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        if (event.getMessage().startsWith(".")) {
            client.getCommandManager().onCommand(event);
            if (!event.isCancelled()) event.setCancelled(true);
        }
        client.handleChat(event.getMessage());
    }
}
