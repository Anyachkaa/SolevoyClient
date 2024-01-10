package ru.itskekoff.client.commands.impl.client.config;

import com.mojang.realmsclient.gui.ChatFormatting;
import lombok.Getter;
import lombok.Setter;
import ru.itskekoff.client.commands.Command;
import ru.itskekoff.client.commands.CommandInfo;
import ru.itskekoff.client.configuration.Config;
import ru.itskekoff.client.configuration.ConfigManager;
import ru.itskekoff.utils.ChatUtil;
import ru.itskekoff.utils.notification.NotificationRenderer;

@CommandInfo(
        prefix = "config",
        description = "Конфиг",
        usage = "list | save [name] | load [name] | delete [name]"
)
public class CommandConfig extends Command {

    @Override
    public void onCommand(String[] args) throws Exception {
        String upperCase = args[0].toUpperCase();
        switch (upperCase) {
            case "LOAD":
                if (client.getConfigManager().loadConfig(args[1])) {
                    ChatUtil.sendChatMessage("Успешно &aзагрузил &fконфиг: &b" + "\"" + args[1] + "\"", true);
                    NotificationRenderer.queue("Конфиг", ChatFormatting.GREEN + "Успешно " + ChatFormatting.WHITE + "загрузил конфиг: " + ChatFormatting.AQUA + "\"" + args[1] + "\"", 4);
                    client.getConfigManager().saveConfig("latest");
                } else {
                    ChatUtil.sendChatMessage("Не смог &bзагрузить &fконфиг: &b" + "\"" + args[1] + "\"", true);
                    NotificationRenderer.queue("Конфиг", ChatFormatting.RED + "Не смог " + ChatFormatting.WHITE + "загрузить конфиг: " + ChatFormatting.AQUA + "\"" + args[1] + "\"", 4);
                    client.getConfigManager().saveConfig("latest");
                }
                break;
            case "SAVE":
                if (client.getConfigManager().saveConfig(args[1])) {
                    ChatUtil.sendChatMessage("Успешно &aсохранил &aконфиг: &b" + "\"" + args[1] + "\"", true);
                    NotificationRenderer.queue("Конфиг", ChatFormatting.GREEN + "Успешно " + ChatFormatting.WHITE + "сохранил конфиг: " + ChatFormatting.AQUA + "\"" + args[1] + "\"", 4);
                    ConfigManager.getLoadedConfigs().clear();
                    client.getConfigManager().load();
                } else {
                    ChatUtil.sendChatMessage("Не смог &bсохранить &fконфиг: &b" + "\"" + args[1] + "\"", true);
                    NotificationRenderer.queue("Конфиг", ChatFormatting.RED + "Не смог " + ChatFormatting.WHITE + "сохранить конфиг: " + ChatFormatting.AQUA + "\"" + args[1] + "\"", 4);
                }
                break;
            case "DELETE":
                if (client.getConfigManager().deleteConfig(args[1])) {
                    ChatUtil.sendChatMessage("&aУспешно &fудалил конфиг: &b" + "\"" + args[1] + "\"", true);
                    NotificationRenderer.queue("Конфиг", ChatFormatting.GREEN + "Успешно " + ChatFormatting.WHITE + "удалил конфиг: " + ChatFormatting.AQUA + "\"" + args[1] + "\"", 4);
                    client.getConfigManager().saveConfig("latest");
                } else {
                    ChatUtil.sendChatMessage("Не смог удалить конфиг: &b" + "\"" + args[1] + "\"", true);
                    NotificationRenderer.queue("Конфиг", ChatFormatting.RED + "Не смог " + ChatFormatting.WHITE + "удалить конфиг: " + ChatFormatting.AQUA + "\"" + args[1] + "\"", 4);
                }
                break;
        }
        if (upperCase.equalsIgnoreCase("LIST")) {
            ChatUtil.sendChatMessage("&aКонфиги:", true);
            for (Config config : client.getConfigManager().getContents()) {
                ChatUtil.sendChatMessage("&7(o) &b" + config.getName(), false);
            }
            client.getConfigManager().saveConfig("latest");
        }
    }
}
