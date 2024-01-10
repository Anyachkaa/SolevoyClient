package ru.itskekoff.client.commands.impl.bot.proxy;

import ru.itskekoff.bots.proxy.ProxyLoader;
import ru.itskekoff.bots.proxy.ProxyType;
import ru.itskekoff.client.commands.Command;
import ru.itskekoff.client.commands.CommandInfo;
import ru.itskekoff.utils.ChatUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

@CommandInfo(
        prefix = "proxy",
        description = "Парсер прокси",
        usage = "dump | load"
)
public class CommandParseProxy extends Command {

    @Override
    public void onCommand(String[] args) throws Exception {
        if (args[0].equals("dump")) {
            File socks4 = new File(client.getProxiesFile4());
            File socks5 = new File(client.getProxiesFile5());
            BufferedWriter socks4Write = new BufferedWriter(new FileWriter(socks4));
            BufferedWriter socks5Write = new BufferedWriter(new FileWriter(socks5));
            client.getProxyLoader().getProxies().forEach(proxyBasic -> {
                if (proxyBasic.getType() == ProxyType.SOCKS5) {
                    try {
                        socks5Write.write(proxyBasic.getHost() + ":" + proxyBasic.getPort() + "\n");
                    } catch (Exception ignored) {
                    }
                } else if (proxyBasic.getType() == ProxyType.SOCKS4) {
                    try {
                        socks4Write.write(proxyBasic.getHost() + ":" + proxyBasic.getPort() + "\n");
                    } catch (Exception ignored) {
                    }
                }
            });
            socks4Write.close();
            socks5Write.close();
            ChatUtil.sendChatMessage("Успешно записано в файлы!", true);
        } else if (args[0].equals("load")) {
            ProxyLoader.globalProxies.clear();
            ProxyLoader.privateProxies.clear();
            ProxyLoader.parseAll(false, true);
        } else if (args[0].equals("clear")) {
            ProxyLoader.globalProxies.clear();
            ProxyLoader.privateProxies.clear();
            ChatUtil.sendChatMessage("Очищено.", true);
        }
    }
}
