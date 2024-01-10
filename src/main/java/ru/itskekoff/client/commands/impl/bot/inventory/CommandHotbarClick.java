package ru.itskekoff.client.commands.impl.bot.inventory;

import ru.itskekoff.client.commands.Command;
import ru.itskekoff.client.commands.CommandInfo;
import ru.itskekoff.protocol.packet.impl.client.play.ClientHeldItemChangePacket;
import ru.itskekoff.protocol.packet.impl.client.play.ClientPlayerTryUseItemPacket;
import ru.itskekoff.utils.ChatUtil;
import ru.itskekoff.utils.ThreadUtils;


@CommandInfo(
        prefix = "hotbarclick",
        description = "Меняет слот в хотбаре и кликает на него",
        usage = "[айди слота]"
)
public class CommandHotbarClick extends Command {


    @Override
    public void onCommand(String[] args) throws Exception {
        client.getBots().forEach(bot -> bot.getSession().sendPacket(new ClientHeldItemChangePacket(Integer.parseInt(args[0]))));
        ThreadUtils.sleep(250L);
        client.getBots().forEach(bot -> bot.getSession().sendPacket(new ClientPlayerTryUseItemPacket()));
        ChatUtil.sendChatMessage("Боты успешно кликнули на слот &b" + args[0], true);
    }
}
