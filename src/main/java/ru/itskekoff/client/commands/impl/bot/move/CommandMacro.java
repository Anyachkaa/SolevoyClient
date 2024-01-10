package ru.itskekoff.client.commands.impl.bot.move;

import ru.itskekoff.bots.Bot;
import ru.itskekoff.bots.macro.Macro;
import ru.itskekoff.bots.macro.MacroRecord;
import ru.itskekoff.client.commands.Command;
import ru.itskekoff.client.commands.CommandInfo;
import ru.itskekoff.protocol.data.Position;
import ru.itskekoff.utils.ChatUtil;
import ru.itskekoff.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.Optional;

@CommandInfo(
        prefix = "macro",
        description = "Макросы",
        usage = "play loop [имя макроса] | play [имя макроса] | rec [имя макроса] | delay [задержка] | stop | list | remove [имя макроса]"
)
public class CommandMacro extends Command {

    @Override
    public void onCommand(String[] args) throws Exception {
        if (!args[0].equalsIgnoreCase("list") && client.isMother()) {
            ChatUtil.sendChatMessage("Выключите Mother!", true);
            return;
        }

        if (args[0].equalsIgnoreCase("remove")) {
            if (client.getMacroManager().getMacros().stream().anyMatch(macro -> macro.getName().equals(args[1]))) {
                client.getMacroManager().removeMacro(client.getMacroManager().getMacroFromString(args[1]));
                ChatUtil.sendChatMessage("Удален макрос: &b" + args[1], true);
            }
            return;
        }

        if (args[0].equalsIgnoreCase("rec")) {
            if (client.getMacroManager().isMacroRecord() || client.getMacroManager().isMacro()) {
                ChatUtil.sendChatMessage("Остановите запись/проигрывание макроса!", true);
                return;
            }

            String name = args[1].replaceAll("[^a-zA-Z\\d_-]", "");

            if (client.getMacroManager().getMacros().stream().anyMatch(macro -> macro != null && macro.getName().equals(name))) {
                ChatUtil.sendChatMessage("Уже есть такой макрос!", true);
                return;
            }

            client.getMacroManager().setMacroRecord(true);
            client.getMacroManager().setCurrentMacro(new Macro(args[1], new ArrayList<>()));
            client.getMacroManager().setCurrentMacroRecord(new MacroRecord(new ArrayList<>()));

            ChatUtil.sendChatMessage("Начинаю записывать макрос &c" + args[1], true);

            client.getTaskExecutor().execute(() -> {
                while (client.isConnected() && client.getMacroManager().isMacroRecord()) {
                    Position old = client.getPosition();
                    ThreadUtils.sleep(30L);

                    if (!client.getMacroManager().isMacroRecord())
                        break;
                    if (old != null) {
                        double xChange = 0, yChange = 0, zChange = 0;
                        try {
                            xChange = client.getPosition().getX() - old.getX();
                            yChange = client.getPosition().getY() - old.getY();
                            zChange = client.getPosition().getZ() - old.getZ();
                        } catch (Exception ignored) {
                        }
                        client.getMacroManager().getCurrentMacroRecord().setPosChange(new MacroRecord.PosChange(xChange, yChange, zChange, old.getYaw(), old.getPitch()));
                        client.getMacroManager().getCurrentMacro().getRecords().add(client.getMacroManager().getCurrentMacroRecord());
                        client.getMacroManager().setCurrentMacroRecord(new MacroRecord(new ArrayList<>()));
                    }
                }
            });
        } else if (args[0].equalsIgnoreCase("stop")) {
            if (client.getMacroManager().isMacroRecord()) {
                client.getMacroManager().setMacroRecord(false);
                client.getMacroManager().getMacros().add(client.getMacroManager().getCurrentMacro());
                client.getMacroManager().saveAsFile(client.getMacroManager().getCurrentMacro());
                ChatUtil.sendChatMessage("Записан макрос &c" + client.getMacroManager().getCurrentMacro().getName(), true);

                client.getMacroManager().setCurrentMacro(null);
            } else if (client.getMacroManager().isMacro()) {
                for (Bot bot : client.getBots()) {
                    bot.setMacro(false);
                    bot.setMacroComplete(false);
                    bot.setMacroIndex(0);
                }

                ChatUtil.sendChatMessage("Остановлено проигрывание макроса &c" + client.getMacroManager().getCurrentMacro().getName(), true);

                client.getMacroManager().setMacro(false);
                client.getMacroManager().setCurrentMacro(null);
            } else {
                ChatUtil.sendChatMessage("Сейчас не записывается/проигрывается ни одного макроса", true);
            }
        } else if (args[0].equalsIgnoreCase("play")) {
            if (client.getMacroManager().isMacroRecord() || client.getMacroManager().isMacro()) {
                ChatUtil.sendChatMessage("Остановите запись/проигрывание макроса!", true);
                return;
            }

            String name = args[1];
            Optional<Macro> opt = client.getMacroManager().getMacros().stream()
                    .filter(macro -> macro.getName().equals(name))
                    .findFirst();

            if (opt.isPresent()) {
                client.getMacroManager().setMacro(true);
                client.getMacroManager().setCurrentMacro(opt.get());

                ChatUtil.sendChatMessage("Начинаю проигрывать макрос &c" + client.getMacroManager().getCurrentMacro().getName(), true);

                for (Bot bot : client.getBots()) {
                    ThreadUtils.sleep(client.getMacroManager().getMacroDelay());
                    bot.setMacro(true);
                }

                new Thread(() -> {
                    while (client.isConnected() && client.getMacroManager().isMacro()) {
                        boolean compl = true;

                        for (Bot bot : client.getBots()) {
                            if (!bot.isMacroComplete()) {
                                compl = false;
                                break;
                            }
                        }

                        if (compl) {
                            ChatUtil.sendChatMessage("Все боты проиграли макрос &c" + client.getMacroManager().getCurrentMacro().getName(), true);

                            for (Bot bot : client.getBots()) {
                                bot.setMacro(false);
                                bot.setMacroComplete(false);
                                bot.setMacroIndex(0);
                            }

                            client.getMacroManager().setMacro(false);
                            client.getMacroManager().setCurrentMacro(null);

                            break;
                        }

                        ThreadUtils.sleep(500L);
                    }
                }).start();
            } else {
                ChatUtil.sendChatMessage("Такого макроса не найдено", true);
            }
        } else if (args[0].equalsIgnoreCase("delay")) {
            int delay = Integer.parseInt(args[1]);

            client.getMacroManager().setMacroDelay(delay);
            ChatUtil.sendChatMessage("Задержка установлена на &c" + delay + "мс", true);
        } else if (args[0].equalsIgnoreCase("list")) {
            ChatUtil.sendChatMessage("Макросы:", true);

            for (Macro macro : client.getMacroManager().getMacros()) {
                if (macro != null) {
                    ChatUtil.sendChatMessage("&7(o) &f" + macro.getName() + " (&b" + macro.getRecords().size() + " &frecords)", false);
                }
            }
        } else if (args[0].equalsIgnoreCase("loop")) {
            if (client.getMacroManager().isMacroRecord() || client.getMacroManager().isMacro()) {
                ChatUtil.sendChatMessage("Остановите запись/проигрывание макроса!", true);
                return;
            }

            String name = args[1];
            Optional<Macro> opt = client.getMacroManager().getMacros().stream().filter(macro -> macro.getName().equals(name)).findFirst();

            if (opt.isPresent()) {
                client.getMacroManager().setMacro(true);
                client.getMacroManager().setCurrentMacro(opt.get());

                ChatUtil.sendChatMessage("Начинаю проигрывать макрос &c" + client.getMacroManager().getCurrentMacro().getName(), true);

                for (Bot bot : client.getBots()) {
                    ThreadUtils.sleep(client.getMacroManager().getMacroDelay());
                    bot.setMacro(true);
                }

                new Thread(() -> {
                    while (client.isConnected() && client.getMacroManager().isMacro()) {
                        boolean compl = true;

                        for (Bot bot : client.getBots()) {
                            if (!bot.isMacroComplete()) {
                                compl = false;
                                break;
                            }
                        }

                        if (compl) {
                            for (Bot bot : client.getBots()) {
                                bot.setMacro(false);
                                bot.setMacroComplete(false);
                                bot.setMacroIndex(0);
                            }

                            client.getMacroManager().setMacro(false);
                            client.getMacroManager().setCurrentMacro(null);
                            client.getMacroManager().setMacro(true);
                            client.getMacroManager().setCurrentMacro(opt.get());
                            for (Bot bot : client.getBots()) {
                                ThreadUtils.sleep(client.getMacroManager().getMacroDelay());
                                bot.setMacro(true);
                            }
                        }

                        ThreadUtils.sleep(500L);
                    }
                }).start();
            } else {
                ChatUtil.sendChatMessage("&cТакого макроса не найдено", true);
            }
        }
    }
}
