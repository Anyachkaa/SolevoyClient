package ru.itskekoff.client.commands.impl.bot.inventory;

import ru.itskekoff.client.commands.Command;
import ru.itskekoff.client.commands.CommandInfo;
import ru.itskekoff.protocol.data.WindowAction;
import ru.itskekoff.utils.ChatUtil;


@CommandInfo(
        prefix = "invclick",
        description = "Боты кликают в открытом гуи",
        usage = "[айди слота]"
)
public class CommandInvClick extends Command {


    @Override
    public void onCommand(String[] args) throws Exception {
        client.getBots().forEach(bot -> {
            if (bot.getOpenContainer() != null) {
                bot.getOpenContainer().slotClick(bot, Short.parseShort(args[0]), 0, WindowAction.CLICK_ITEM);
            }
        });

        ChatUtil.sendChatMessage("Боты кликнули на слот &b" + args[0], true);
    }
}
