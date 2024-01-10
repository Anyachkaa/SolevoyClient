package ru.itskekoff.client.commands.impl.bot.chat;

import ru.itskekoff.client.commands.Command;
import ru.itskekoff.client.commands.CommandInfo;
import ru.itskekoff.protocol.packet.impl.client.play.ClientChatPacket;
import ru.itskekoff.utils.ChatUtil;

@CommandInfo(
        prefix = "chat",
        description = "Отправить сообщение в чат от ботов",
        usage = "[сообщение]"
)
public class CommandChat extends Command {

    @Override
    public void onCommand(String[] args) throws Exception {
        StringBuilder msg = new StringBuilder();
        for (String arg : args)
            msg.append(arg).append(" ");

        client.getBots().forEach(bot -> bot.getSession().sendPacket(new ClientChatPacket(msg.toString())));
        ChatUtil.sendChatMessage("Боты отправили сообщение.", true);
    }
}
