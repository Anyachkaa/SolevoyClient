package ru.itskekoff.bots.nicks;

import ru.itskekoff.client.SolevoyClient;
import ru.itskekoff.client.clickgui.settings.impl.*;
import ru.itskekoff.client.module.impl.bots.BotMain;
import ru.itskekoff.utils.notification.Notification;
import ru.itskekoff.utils.notification.NotificationRenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NickLoader {
    private static final SolevoyClient client = SolevoyClient.getInstance();
    private final List<String> nicknames = new CopyOnWriteArrayList<>();
    private final List<String> il = new CopyOnWriteArrayList<>();
    private final List<String> clientNicks = new CopyOnWriteArrayList<>();
    private int number = 0;
    private final ListSetting nickType = BotMain.nickType;
    private final StringSetting nickFormat = BotMain.nickFormat;

    private final Random rnd = new Random();

    public NickLoader() throws IOException {
        nicknames.clear();
        clientNicks.clear();
        il.clear();
        if (!new File(client.getMinecraftFolder() + "/bots/nicks/").exists()) {
            new File(client.getMinecraftFolder() + "/bots/nicks/").mkdirs();
        }
    }

    public void reload() throws IOException {
        try
                (Stream<String> stream = Files.lines(Paths.get(client.getMinecraftFolder() + "/bots/nicks.txt"))) {
            stream.forEach(nicknames::add);
        } catch (Exception ignored) {
        }
    }

    public String nextNick() {
        String nickname;
        switch (nickType.getCurrentMode()) {
            case "File" -> {
                try {
                    number++;
                    if (number >= nicknames.size()) number = 0;
                    return nicknames.get(number);
                } catch (ArrayIndexOutOfBoundsException exception) {
                    NotificationRenderer.queue("ERROR", "Добавьте никнеймы в nicks.txt", 3);
                    return null;
                }
            }
            case "IllIlIll" -> {
                do {
                    nickname = IntStream.range(0, 12)
                            .mapToObj(i -> (ThreadLocalRandom.current().nextBoolean()) ? "I" : "l")
                            .collect(Collectors.joining());
                } while (il.contains(nickname));
                il.add(nickname);
                return nickname;
            }
            case "Custom" -> {
                do {
                    nickname = nickFormat.getCurrentText().replaceAll("%d", String.valueOf(rnd.nextInt(100090, 999999)));
                } while (clientNicks.contains(nickname));
                clientNicks.add(nickname);
                return nickname;
            }
        }
        return "SOLEVOY_" + rnd.nextInt(100000, 999999);
    }
}
