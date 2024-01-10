package ru.itskekoff.client.commands.impl;

import ru.itskekoff.client.SolevoyClient;
import ru.itskekoff.client.commands.Command;
import ru.itskekoff.client.commands.CommandInfo;
import ru.itskekoff.utils.ChatUtil;
import ru.itskekoff.utils.StringUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@CommandInfo(
        prefix = "help",
        description = "полезные команды",
        usage = "[страница]"
)
public class CommandHelp extends Command {

    @Override
    public void onCommand(String[] args) {
        List<Command> list = new ArrayList<>(SolevoyClient.getInstance().getCommandManager().botCommands);
        List<Command> Ghoul = new ArrayList<>(SolevoyClient.getInstance().getCommandManager().commands);
        list.sort(Comparator.comparingInt(s -> StringUtil.getStringWidth(s.getPrefix())));
        Ghoul.sort(Comparator.comparingInt(s -> StringUtil.getStringWidth(s.getPrefix())));
        list.addAll(Ghoul);
        int i = 1;
        if (args.length == 1) {
            i = Integer.parseInt(args[0]);
        }
        int k = i - 1;
        int l = Math.min(i * 10, list.size());
        int j = (list.size() - 1) / 10;
        int f = j + 1;

        if (i < 1 || i > f) {
            ChatUtil.sendChatMessage("&fТакой страницы нет!", false);
            return;
        }

        ChatUtil.sendChatMessage("&fПомощь &7(&b" + i + "&7/&b" + f + "&7)", true);

        for (int i1 = k * 10; i1 < l; ++i1) {
            final Command command = list.get(i1);
            String description = command.getDesc();
            String usage = command.getUsage();
            if (command.getDesc() == null) description = "&r&7Описание не указано!";
            if (command.getUsage() == null) usage = "";
            String message = "&7(o) &b" + "." + (SolevoyClient.getInstance().getCommandManager().botCommands.contains(command) ? "bots " : "") + command.getPrefix() + " &f" + usage + " (" + description + ")";
            ChatUtil.sendChatMessage(message, false);
        }

    }
}
