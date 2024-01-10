package ru.itskekoff.client.module.impl.bots;

import ru.itskekoff.client.clickgui.settings.impl.BooleanSetting;
import ru.itskekoff.client.clickgui.settings.impl.SliderSetting;
import ru.itskekoff.client.clickgui.settings.impl.StringSetting;
import ru.itskekoff.client.module.Category;
import ru.itskekoff.client.module.Module;

public class BotBypass extends Module {
    public static BooleanSetting item;
    public static StringSetting itemName;
    public static BooleanSetting math;
    public static BooleanSetting chat;
    public static BooleanSetting differentItem;
    public static BooleanSetting rejoin;
    public static SliderSetting rejoinDelay;
    public static BooleanSetting ping;

    public BotBypass() {
        super("BotBypass", "обходы у ботов", Category.Bots);
        item = new BooleanSetting("Item", "ItemName bypass", true);
        itemName = new StringSetting("ItemName", "Item name", "жми", 32);
        math = new BooleanSetting("Math", "Math", true);
        chat = new BooleanSetting("Chat", "Chat", true);
        differentItem = new BooleanSetting("Diff", "Different Item", true);
        rejoin = new BooleanSetting("Rejoin", "Bot rejoin", true);
        rejoinDelay = new SliderSetting("RejoinDelay", "Bot rejoin delay (sec)", 2, 0, 10, 1);
        ping = new BooleanSetting("Ping", "Ping before join", true);
        addSettings(item, itemName, math, chat, differentItem, rejoin, rejoinDelay, ping);
    }
}
