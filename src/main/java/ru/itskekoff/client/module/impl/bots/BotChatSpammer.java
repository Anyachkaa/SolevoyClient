package ru.itskekoff.client.module.impl.bots;

import ru.itskekoff.client.clickgui.settings.impl.*;
import ru.itskekoff.client.module.Category;
import ru.itskekoff.client.module.Module;
import ru.itskekoff.event.EventTarget;
import ru.itskekoff.event.impl.EventUpdate;
import ru.itskekoff.utils.ThreadUtils;

public class BotChatSpammer extends Module {

    private SliderSetting delay;
    private StringSetting message;

    public BotChatSpammer() {
        super("BotChatSpammer", "Спам в чате ботами", Category.Bots);
        delay = new SliderSetting("Delay", "Message delay", 2, 0, 10, 1);
        message = new StringSetting("Message", "Spam message", "none", 256);
        addSettings(delay, message);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        int delay1 = (int) delay.getCurrent();
        String message1 = message.getCurrentText();
        if (client.getBots().size() > 0) {
            client.getBots().forEach(bot -> {
                ThreadUtils.sleep(delay1 * 1000L);
                bot.sendMessage(message1);
            });
        }
    }
}
