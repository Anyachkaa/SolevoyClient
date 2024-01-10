package ru.itskekoff.client.commands;

import net.minecraft.client.Minecraft;
import ru.itskekoff.client.SolevoyClient;
import ru.itskekoff.client.commands.impl.CommandHelp;
import ru.itskekoff.client.commands.impl.bot.connection.CommandConnect;
import ru.itskekoff.event.impl.EventSendMessage;
import ru.itskekoff.utils.ChatUtil;
import ru.itskekoff.utils.ReflectionUtil;

import java.util.*;

public class CommandManager {
    private final HashMap<String, Long> cooldown = new HashMap<>();

    public final List<Command> commands = new ArrayList<>();
    public final List<Command> botCommands = new ArrayList<>();

    private final SolevoyClient client = SolevoyClient.getInstance();

    public void init() {
        commands.addAll(ReflectionUtil.getClasses("ru.itskekoff.client.commands.impl", Command.class));
        botCommands.addAll(ReflectionUtil.getClasses("ru.itskekoff.client.commands.impl.bot", Command.class));

        commands.removeIf(cmd -> cmd.getClass().getPackageName().contains("bot"));
    }

    public void onCommand(EventSendMessage event) {
        String message = event.getMessage();
        final String[] args = message.substring(".".length()).split(" ");

        Optional<Command> optionalCommand = commands.stream().filter(cmd -> cmd.getPrefix().equalsIgnoreCase(args[0])).findFirst();

        if (optionalCommand.isEmpty()) {
            optionalCommand = commands.stream().filter(cmd -> cmd.getPrefix() != null && cmd.getPrefix().equalsIgnoreCase(args[0])).findFirst();

            if (optionalCommand.isEmpty()) {
                if (message.startsWith(".bots") && args.length > 1) {
                    optionalCommand = botCommands.stream().filter(cmd -> cmd.getPrefix() != null && cmd.getPrefix().equalsIgnoreCase(args[1])).findFirst();

                    if (optionalCommand.isEmpty()) {
                        event.setCancelled(true);
                        ChatUtil.sendChatMessage("&cКоманда не найдена", true);
                        return;
                    }
                } else {
                    event.setCancelled(true);
                    ChatUtil.sendChatMessage("&cКоманда не найдена", true);
                    return;
                }
            }
        }

        final Command command = optionalCommand.get();
        final String packageName = command.getClass().getPackage().getName();
        final String commandType = packageName.substring(packageName.lastIndexOf(".")).substring(1);
        if (commandType.equals("bot") && SolevoyClient.getInstance().getBots().size() <= 0) {
            if (!(command instanceof CommandConnect)) {
                ChatUtil.sendChatMessage("&cПодключите ботов чтобы использовать эту команду.", true);
                return;
            }
        }
        try {
            if (!(command instanceof CommandHelp) && cooldown.containsKey(Minecraft.getMinecraft().player.getName())) {
                long secondsLeft = cooldown.get(Minecraft.getMinecraft().player.getName()) / 1000L + 1000L - System.currentTimeMillis() / 1000L;

                if (secondsLeft > 0L) {
                    String seconds = switch ((int) secondsLeft) {
                        case 0, 8, 7, 6, 5, 9 -> "секунд";
                        case 1 -> "секунду";
                        case 2, 4, 3 -> "секунды";
                        default -> "";
                    };
                    event.setCancelled(true);
                    ChatUtil.sendChatMessage("Подождите &b" + secondsLeft + " &f" + seconds + " перед использованием команды.", true);
                    return;
                }

                cooldown.put(Minecraft.getMinecraft().player.getName(), System.currentTimeMillis());
            }

            Optional<Command> finalOptionalCommand = optionalCommand;
            client.getTaskExecutor().execute(() -> {
                try {
                    event.setCancelled(true);
                    command.onCommand(botCommands.contains(finalOptionalCommand.get()) ? Arrays.copyOfRange(args, 2, args.length) : Arrays.copyOfRange(args, 1, args.length));
                } catch (Exception e) {
                    e.printStackTrace();
                    ChatUtil.sendChatMessage("&fИспользование: ." + (message.startsWith(".bots") ? "bots " : "") + command.getPrefix() + " " + command.getUsage(), true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ChatUtil.sendChatMessage("&fИспользование: ." + (message.startsWith(".bots") ? "bots " : "") + command.getPrefix() + " " + command.getUsage(), true);
        }
    }
}
