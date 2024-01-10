package ru.itskekoff.client.commands.impl.bot.connection;

import ru.itskekoff.bots.Bot;
import ru.itskekoff.client.commands.Command;
import ru.itskekoff.client.commands.CommandInfo;
import ru.itskekoff.utils.ChatUtil;


@CommandInfo(
        prefix = "disconnect",
        description = "Отключает ботов/бота",
        usage = "[all / кол-во]"
)
public class CommandDisconnect extends Command {

    @Override
    public void onCommand(String[] args) throws Exception {
        if (args[0].equalsIgnoreCase("all")) {
            for (Bot bot : client.getBots()) {
                try {
                    client.getBots().remove(bot);
                    bot.getSession().setDisconnectedManually(true);
                    bot.getSession().getChannel().close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            ChatUtil.sendChatMessage("Отключены все боты.", true);
        } else {
            int count = Integer.parseInt(args[0]);

            if (count >= client.getBots().size()) {
                ChatUtil.sendChatMessage("Это число больше чем у вас подключённых ботов!", true);
                return;
            }

            if (count <= 0) {
                ChatUtil.sendChatMessage("Некорректное число", true);
                return;
            }

            int i = 0;
            for (Bot bot : client.getBots()) {
                if (i >= count) {
                    break;
                }

                try {
                    client.getBots().remove(bot);
                    bot.getSession().setDisconnectedManually(true);
                    bot.getSession().getChannel().close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                i++;
            }

            ChatUtil.sendChatMessage("Отключено " + count + " ботов.", true);
        }
    }
}