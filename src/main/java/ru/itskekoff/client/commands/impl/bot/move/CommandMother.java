package ru.itskekoff.client.commands.impl.bot.move;

import ru.itskekoff.bots.Bot;
import ru.itskekoff.bots.mother.MotherRecord;
import ru.itskekoff.client.commands.Command;
import ru.itskekoff.client.commands.CommandInfo;
import ru.itskekoff.utils.ChatUtil;
import ru.itskekoff.utils.ThreadUtils;

import java.util.ArrayList;

@CommandInfo(
        prefix = "mother",
        description = "Боты повторяют действия игрока",
        usage = "[on/off] | delay [мс]"
)
public class CommandMother extends Command {

    @Override
    public void onCommand(String[] args) throws Exception {
        if (client.getMacroManager().isMacro() || client.getMacroManager().isMacroRecord()) {
            ChatUtil.sendChatMessage("Остановите запись/проигрывание макроса!", true);
            return;
        }

        if (args[0].equalsIgnoreCase("on")) {
            if (client.isMother())
                return;

            client.setMother(true);
            client.getMotherRecords().clear();

            client.getTaskExecutor().execute(() -> {
                while (client.isConnected() && client.isMother()) {
                    client.setCurrentMotherRecord(new MotherRecord(client.getPosition(), new ArrayList<>()));
                    ThreadUtils.sleep(50L);
                    client.getMotherRecords().add(client.getCurrentMotherRecord());
                }
            });

            ChatUtil.sendChatMessage("Включён &bMother", true);

            for (Bot bot : client.getBots()) {
                if (!client.isMother())
                    break;

                ThreadUtils.sleep(client.getMotherDelay());
                bot.setMother(true);
            }
        } else if (args[0].equalsIgnoreCase("off")) {
            client.setMother(false);

            for (Bot bot : client.getBots()) {
                bot.setMother(false);
                bot.setMotherIndex(0);
            }

            ThreadUtils.sleep(100L);
            client.getMotherRecords().clear();
            client.setCurrentMotherRecord(null);

            ChatUtil.sendChatMessage("Выключен &bMother", true);
        } else if(args[0].equalsIgnoreCase("delay")) {
            client.setMotherDelay(Integer.parseInt(args[1]));
            ChatUtil.sendChatMessage("Поставлена задержка на &b" + client.getMotherDelay() + "&fмс", true);
        }
    }
}
