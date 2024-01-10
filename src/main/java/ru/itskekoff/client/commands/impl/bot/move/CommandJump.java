package ru.itskekoff.client.commands.impl.bot.move;

import ru.itskekoff.bots.Bot;
import ru.itskekoff.client.commands.Command;
import ru.itskekoff.client.commands.CommandInfo;
import ru.itskekoff.utils.ChatUtil;

@CommandInfo(
        prefix = "jump",
        description = "Боты прыгают"
)
public class CommandJump extends Command {

    @Override
    public void onCommand(String[] args) throws Exception {
        client.getBots().forEach(Bot::jump);
        ChatUtil.sendChatMessage("Боты прыгнули", true);
    }
}
