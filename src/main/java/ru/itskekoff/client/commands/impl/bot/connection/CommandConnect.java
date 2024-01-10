package ru.itskekoff.client.commands.impl.bot.connection;

import com.mojang.realmsclient.gui.ChatFormatting;
import javassist.util.proxy.Proxy;
import net.minecraft.client.multiplayer.GuiConnecting;
import ru.itskekoff.bots.Bot;
import ru.itskekoff.bots.BotConnection;
import ru.itskekoff.bots.nicks.NickLoader;
import ru.itskekoff.bots.proxy.ProxyBasic;
import ru.itskekoff.bots.proxy.ProxyLoader;
import ru.itskekoff.client.commands.Command;
import ru.itskekoff.client.commands.CommandInfo;
import ru.itskekoff.client.module.impl.bots.BotMain;
import ru.itskekoff.utils.ChatUtil;
import ru.itskekoff.utils.ThreadUtils;
import ru.itskekoff.utils.notification.NotificationRenderer;

import java.util.Random;

@CommandInfo(
        prefix = "connect",
        description = "подключение ботов",
        usage = "[кол-во] [задержка (мин. 1, макс. 10000)]"
)
public class CommandConnect extends Command {

    @Override
    public void onCommand(String[] args) throws Exception {
        if (args[0].equalsIgnoreCase("stop")) {
            client.setConnectingBots(false);
            ChatUtil.sendChatMessage("&fПодключение ботов остановлено.", true);
        } else {
            if (client.isConnectingBots()) {
                ChatUtil.sendChatMessage("&fВы уже подключаете ботов! Остановить подключение - connect stop", true);
                return;
            }
            if (!client.isConnected()) {
                ChatUtil.sendChatMessage("&fПодключитесь к серверу для использования этой команды!", true);
                return;
            }

            int count = Integer.parseInt(args[0]);
            int delay = Integer.parseInt(args[1]);

            if (delay < 1) {
                ChatUtil.sendChatMessage("&fМинимальная задержка - 1мс", true);
                return;
            }

            if (delay > 10000) {
                ChatUtil.sendChatMessage("&fМаксимальная задержка - 10000мс", true);
                return;
            }

            if (count <= 0) {
                ChatUtil.sendChatMessage("&fНекорректно введено кол-во ботов!", true);
                return;
            }

            if (client.getModuleManager().getModule("BotMain").isToggled()) {
                NickLoader nickLoader = client.getNickLoader();
                nickLoader.reload();
                client.setConnectingBots(true);
                if (ProxyLoader.globalProxies.isEmpty()) {
                    ChatUtil.sendChatMessage("Парсинг прокси", true);
                    NotificationRenderer.queue("PROXY", "Парсинг прокси", 3);
                    if (BotMain.proxy.getCurrentMode().equals("Private")) {
                        ProxyLoader.privateProxies.clear();
                        ChatUtil.sendChatMessage(ProxyLoader.parseAll(false, true), true);
                        NotificationRenderer.queue("PROXY", "Загружено " + ChatFormatting.GREEN + ProxyLoader.privateProxies.size() + " прокси", 3);
                    } else {
                        ProxyLoader.globalProxies.clear();
                        ChatUtil.sendChatMessage(ProxyLoader.parseAll(true, true), true);
                        NotificationRenderer.queue("PROXY", "Загружено " + ChatFormatting.GREEN + ProxyLoader.globalProxies.size() + " прокси", 3);
                    }
                }
                NotificationRenderer.queue("Success", "Подключение " + count + " ботов.", 2);
                new Thread(() -> {
                    for (int i = 0; i < count && client.isConnected() && client.isConnectingBots(); i++) {
                        try {
                            String nick = nickLoader.nextNick();
                            if (nick == null) {
                                client.setConnectingBots(false);
                                if (!client.getModuleManager().getModule("Notification").isToggled()) {
                                    ChatUtil.sendChatMessage("Добавьте никнеймы в nicks.txt!", true);
                                }
                                return;
                            }
                            if (BotMain.connect.isToggled()) ChatUtil.sendChatMessage("&f[&b" + nick + "&f] Подключение...", true);
                            ProxyBasic proxy = client.getProxyLoader().getProxy();
                            System.out.println(proxy.getUsername() + " | " + proxy.getPassword() + " | " + proxy.getHost() + ":" + proxy.getPort());
                            new BotConnection(new Bot(nick)).connect(GuiConnecting.host, GuiConnecting.port, proxy);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        ThreadUtils.sleep(delay);
                    }
                    NotificationRenderer.queue("Success", "Подключено " + count + " ботов.", 2);

                    client.setConnectingBots(false);
                }).start();
            }
        }
    }
}
