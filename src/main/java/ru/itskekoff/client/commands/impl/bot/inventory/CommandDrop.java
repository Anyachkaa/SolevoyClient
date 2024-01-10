package ru.itskekoff.client.commands.impl.bot.inventory;


import ru.itskekoff.client.commands.Command;
import ru.itskekoff.client.commands.CommandInfo;
import ru.itskekoff.protocol.data.WindowAction;
import ru.itskekoff.utils.ChatUtil;
import ru.itskekoff.utils.ThreadUtils;

@CommandInfo(
        prefix = "drop",
        description = "Боты выкидывают предметы из инвентаря"
)
public class CommandDrop extends Command {

    @Override
    public void onCommand(String[] args) throws Exception {
        client.getBots().forEach(bot -> {
            if (bot.getInventory() != null && bot.getInventory().getItems().size() > 0) {
                Thread worker = new Thread(() -> {
                    for (short i = 0; i < bot.getInventory().getItems().size(); i++) {
                        if (bot.getInventory().getItems().get(i) != null && bot.getInventory().getItems().get(i).getId() != 0) {
                            bot.getInventory().slotClick(bot, i, 1, WindowAction.DROP_ITEM);
                            ThreadUtils.sleep(1000L);
                        }
                    }
                });

                worker.setPriority(1);
                worker.start();
            }
        });

        ChatUtil.sendChatMessage("&fБоты начали выкидывать предметы.", true);
    }
}