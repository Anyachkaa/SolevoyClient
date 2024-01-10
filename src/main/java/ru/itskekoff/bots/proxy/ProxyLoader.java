package ru.itskekoff.bots.proxy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.itskekoff.client.SolevoyClient;
import ru.itskekoff.client.clickgui.settings.impl.*;
import ru.itskekoff.client.module.impl.bots.BotMain;
import ru.itskekoff.utils.ChatUtil;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyLoader {

    public static final List<ProxyBasic> globalProxies = new CopyOnWriteArrayList<>();
    public static final List<ProxyBasic> privateProxies = new CopyOnWriteArrayList<>();
    private static final String IPADDRESS_PATTERN = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3}):(\\d{2,4})\\z";
    protected static final String[] proxyList4 = new String[]{
            "https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-socks4.txt",
            "https://raw.githubusercontent.com/saschazesiger/Free-Proxies/master/proxies/socks4.txt",
            "https://raw.githubusercontent.com/monosans/proxy-list/main/proxies/socks4.txt",
            "https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/socks4.txt",
            "https://api.proxyscrape.com/v2/?request=displayproxies&protocol=socks4",
            "https://raw.githubusercontent.com/mmpx12/proxy-list/master/socks4.txt",
            "https://www.proxy-list.download/api/v1/get?type=socks4",
            "https://openproxylist.xyz/socks4.txt",
            "https://proxyspace.pro/socks4.txt",
    };

    protected static final String[] proxyList5 = new String[]{
            "https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-socks5.txt",
            "https://raw.githubusercontent.com/saschazesiger/Free-Proxies/master/proxies/socks5.txt",
            "https://raw.githubusercontent.com/monosans/proxy-list/main/proxies/socks5.txt",
            "https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/socks5.txt",
            "https://api.proxyscrape.com/v2/?request=displayproxies&protocol=socks5",
            "https://raw.githubusercontent.com/mmpx12/proxy-list/master/socks5.txt",
            "https://www.proxy-list.download/api/v1/get?type=socks5",
            "https://openproxylist.xyz/socks5.txt",
            "https://proxyspace.pro/socks5.txt",
            "https://cdn.discordapp.com/attachments/1014241422997721249/1014370233663893504/socks5.txt",
            "https://api.best-proxies.ru/proxylist.txt?key=75749eb852ddaca288b68618105f9203&type=socks5&limit=0",
    };

    private final Random random = new Random(System.currentTimeMillis());
    private static final SolevoyClient client = SolevoyClient.getInstance();
    private static ListSetting proxyType = null;
    private int number = 0;


    public static void init() {
         if (!new File(client.getMinecraftFolder() + "/bots/proxy/").exists()) {
            new File(client.getMinecraftFolder() + "/bots/proxy/").mkdirs();
        }
    }

    private static void parseSocks4(boolean isPrivate) {
        File proxyFile = new File(client.getProxiesFile4());
        if (isPrivate) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(proxyFile))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] address = line.split("@")[1].split(":", 2);
                    String[] creditionals = line.split("@")[0].split(":", 2);
                    privateProxies.add(new ProxyBasic(ProxyType.SOCKS4, address[0], Integer.parseInt(address[1]), creditionals[0], creditionals[1]));
                }
            } catch (Exception e) {
            }
        } else {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(proxyFile))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
                    Matcher matcher = pattern.matcher(line);
                    while (matcher.find()) {
                        String[] address = line.split(":", 2);
                        globalProxies.add(new ProxyBasic(ProxyType.SOCKS4, address[0], Integer.parseInt(address[1])));
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    private static void parseSocks5(boolean isPrivate) {
        File proxyFile = new File(client.getProxiesFile5());
        if (isPrivate) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(proxyFile))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] address = line.split("@")[1].split(":", 2);
                    String[] creditionals = line.split("@")[0].split(":", 2);
                    privateProxies.add(new ProxyBasic(ProxyType.SOCKS5, address[0], Integer.parseInt(address[1]), creditionals[0], creditionals[1]));
                }
            } catch (Exception e) {
            }
        } else {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(proxyFile))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
                    Matcher matcher = pattern.matcher(line);
                    while (matcher.find()) {
                        String[] address = line.split(":", 2);
                        globalProxies.add(new ProxyBasic(ProxyType.SOCKS5, address[0], Integer.parseInt(address[1])));
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    private static void parseHttp(boolean isPrivate) {
        File proxyFile = new File(client.getProxiesFileH());
        if (isPrivate) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(proxyFile))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] address = line.split("@")[1].split(":", 2);
                    String[] creditionals = line.split("@")[0].split(":", 2);
                    privateProxies.add(new ProxyBasic(ProxyType.HTTP, address[0], Integer.parseInt(address[1]), creditionals[0], creditionals[1]));
                }
            } catch (Exception e) {
            }
        }
    }

    private static void parseSocks4Web() {
        try {
            Arrays.stream(proxyList4).forEach(url -> {
                try {
                    Document proxyList = Jsoup.connect(url).get();
                    globalProxies.addAll(Arrays.stream(proxyList.text().split(" ")).distinct().map((proxy) -> new ProxyBasic(
                            ProxyType.SOCKS4, proxy.split(":")[0], Integer.parseInt(proxy.split(":")[1]))).toList());
                } catch (Throwable ignored) {}
            });
        } catch (Exception e) {
        }
    }

    private static void parseSocks5Web() {
        try {
            Arrays.stream(proxyList5).forEach(url ->  {
                try {
                    Document proxyList = Jsoup.connect(url).get();
                    globalProxies.addAll(Arrays.stream(proxyList.text().split(" ")).distinct().map((proxy) -> new ProxyBasic(
                            ProxyType.SOCKS5, proxy.split(":")[0], Integer.parseInt(proxy.split(":")[1]))).toList());
                } catch (Throwable ignored) {}
            });
        } catch (Exception e) {
        }
    }

    public static String parseAll(boolean download, boolean fromFile) {
        proxyType = BotMain.proxy;
        if (!globalProxies.isEmpty()) globalProxies.clear();
        File proxy4 = new File(client.getProxiesFile4());
        File proxy5 = new File(client.getProxiesFile5());
        File http = new File(client.getProxiesFileH());
        if (proxy4.exists()) {
            parseSocks4(proxyType.getCurrentMode().equals("Private"));
        } if (proxy5.exists()) {
            parseSocks5(proxyType.getCurrentMode().equals("Private"));
        } if (http.exists()) {
            parseHttp(proxyType.getCurrentMode().equals("Private"));
        }
        if (proxy4.exists() || proxy5.exists() || http.exists()) {
            return ChatUtil.fixColor(String.format("&fЗагружено &b%d &fпрокси.", privateProxies.size()));
        }

        if (download) {
            if (globalProxies.isEmpty()) {
                if (!proxy4.exists() && !proxy5.exists() && !http.exists()) {
                    parseSocks4Web();
                    parseSocks5Web();
                }
            }
        }
        return ChatUtil.fixColor(String.format("&fЗагружено &b%d &fпрокси.", globalProxies.size()));
    }

    public ProxyBasic getProxy() {
        proxyType = BotMain.proxy;
        if (proxyType.getCurrentMode().equals("No proxy")) return new ProxyBasic(ProxyType.NO_PROXY);
        if (privateProxies.size() > 0 && proxyType.getCurrentMode().equals("Private")) {
            System.out.println("нассале абассале");
            ++number;

            if (number >= privateProxies.size()) number = 0;
            return privateProxies.get(number);
        }
        if (globalProxies.size() > 0 && proxyType.getCurrentMode().equals("Public")) {
            ++number;

            if (number >= globalProxies.size())
                number = 0;

            return globalProxies.get(number);
        }
        return new ProxyBasic(ProxyType.NO_PROXY);
    }

    public List<ProxyBasic> getProxies() {
        return globalProxies;
    }
}
