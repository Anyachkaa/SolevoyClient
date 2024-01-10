package ru.itskekoff.client.module.impl.bots;

import ru.itskekoff.client.clickgui.settings.impl.StringSetting;
import ru.itskekoff.client.module.Category;
import ru.itskekoff.client.module.Module;

public class BotRegister extends Module {
    public static StringSetting botPassword;

    public BotRegister() {
        super("BotAutoRegister", "Боты регистрируются", Category.Bots);
        botPassword = new StringSetting("password", "Password", "8941ak421", 16);
        addSettings(botPassword);
    }
}
