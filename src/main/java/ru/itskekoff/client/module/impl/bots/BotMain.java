package ru.itskekoff.client.module.impl.bots;

import ru.itskekoff.bots.nicks.NickLoader;
import ru.itskekoff.client.clickgui.settings.impl.*;
import ru.itskekoff.client.module.Category;
import ru.itskekoff.client.module.Module;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class BotMain extends Module {

    public static BooleanSetting disconnect;
    public static BooleanSetting chat;
    public static BooleanSetting connect;
    public static ListSetting nickType;
    public static StringSetting nickFormat;
    public static ListSetting proxy;

    public BotMain() {
        super("BotMain", "Общие настройки ботов", Category.Bots);
        disconnect = new BooleanSetting("Disconnect", "Display disconnect", true);
        chat = new BooleanSetting("Chat", "View chat", false);
        connect = new BooleanSetting("Connect", "View connect messages", false);
        nickType = new ListSetting("NickType", "Nickname type", "Custom", "IllIlIll", "File", "Custom");
        nickFormat = new StringSetting("NicknameFormat", "Bots nickname", "%d_solevoy", 16);
        proxy = new ListSetting("Proxy", "Proxy type", "Public", "Private", "Public", "No proxy");
        addSettings(disconnect, chat, connect, nickType, nickFormat, proxy);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        try {
            client.getNickLoader().reload();
        } catch (IOException e) {}
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }



}
