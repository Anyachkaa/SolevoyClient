package ru.itskekoff.client.discord;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import ru.itskekoff.utils.ThreadUtils;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiscordInstance {
    private static long created = 0;
    private boolean running = true;

    private final ExecutorService taskExecutor = Executors.newFixedThreadPool(1);
    private final ScheduledExecutorService taskScheduler = Executors.newSingleThreadScheduledExecutor();

    private final String[] firstLines = {"я рыгнул так,", "Раньше было лучше,", "BabyTrep, говоришь что ты рем, ты долбаеб?",
            "github.com/itskekoff", "Я кот,", "Пиздуйте нахуй отсюда,", "Надеюсь последнее что я услышу,",
            "Большие дяди отправят на бойню,", "Кто прочитал...", "Депортировали в кровать,", "Зачем ты это читаешь?", "СПб-шные соли. Не дорого,"};
    private final String[] secondLines = {"что жестко пернул.", "Я согласен - это факт.", "все мы давно знаем то, что ты BabyTrep",
            "пастеры, вам туда.", "А чего добились вы?", "Ебанные пидорасы.", "это будет выстрел.",
            "дядям всегда было похуй.", "... тот пидор.", "жизнь боль.", "Занимайся делами, долбаеб.", "naebalovo.ru"};

    public DiscordInstance() {
        start();
    }

    public static void update(String firstLine, String secondLine) {
        DiscordRichPresence.Builder b = new DiscordRichPresence.Builder(secondLine);
        b.setBigImage("https://media.tenor.com/BcVfaL-D0XEAAAAd/komaru-cat-komaru.gif", "dsc.gg/bebranetwork | Build 4255822");
        b.setDetails(firstLine);
        b.setStartTimestamps(created);

        DiscordRPC.discordUpdatePresence(b.build());
    }

    private int currentLine = 0;

    public void start() {
        created = System.currentTimeMillis();
        taskScheduler.scheduleAtFixedRate(DiscordRPC::discordRunCallbacks, 0L, 5L, TimeUnit.SECONDS);
        taskScheduler.scheduleAtFixedRate(() -> {
            String firstLine = firstLines[currentLine];
            String secondLine = secondLines[currentLine];
            currentLine++;
            if (currentLine == firstLines.length) currentLine = 0;
            StringBuilder builder = new StringBuilder();
            for (char textChar : firstLine.toCharArray()) {
                builder.append(textChar);
                update(builder.toString(), "");
                ThreadUtils.sleep(50L);
            }
            builder = new StringBuilder();
            for (char textChar : secondLine.toCharArray()) {
                builder.append(textChar);
                update(firstLine, builder.toString());
                ThreadUtils.sleep(50L);
            }
        }, 0L, 8L, TimeUnit.SECONDS);
        DiscordRPC.discordInitialize("1041653786114019391", null, true);
    }

    public void shutdown() {
        running = false;
        DiscordRPC.discordShutdown();
    }

    private String animate(String text) {
        char[] textChars = text.toCharArray();
        StringBuilder resulut = new StringBuilder();
        for (char textChar : textChars) {
            resulut.append(textChar);
        }
        return resulut.toString();
    }
}
