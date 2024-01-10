package ru.itskekoff.client.commands.impl.bot.move;

import ru.itskekoff.client.commands.Command;
import ru.itskekoff.client.commands.CommandInfo;
import ru.itskekoff.utils.ChatUtil;

@CommandInfo(
        prefix = "rotation",
        description = "Меняет положение головы ботов",
        usage = "[yaw] [pitch]"
)
public class CommandRotation extends Command {

    @Override
    public void onCommand(String[] args) throws Exception {
        int yaw = Integer.parseInt(args[0]);
        int pitch = Integer.parseInt(args[1]);

        if (yaw > 180 || yaw < -180 || pitch > 90 || pitch < -90) {
            ChatUtil.sendChatMessage("&cВы неправильно указали yaw или pitch!", true);
            return;
        }

        client.getBots().forEach(bot -> {
            bot.setYaw(yaw);
            bot.setPitch(pitch);
        });

        ChatUtil.sendChatMessage("Боты изменили расположение головы.", true);
    }
}