package ru.itskekoff.client;

import baritone.api.BaritoneAPI;
import lombok.Data;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import org.lwjgl.opengl.Display;
import ru.itskekoff.bots.Bot;
import ru.itskekoff.bots.chunks.CachedChunk;
import ru.itskekoff.bots.macro.MacroManager;
import ru.itskekoff.bots.mother.MotherRecord;
import ru.itskekoff.bots.nicks.NickLoader;
import ru.itskekoff.bots.proxy.ProxyLoader;
import ru.itskekoff.bots.utils.Timer;
import ru.itskekoff.client.clickgui.ClickGUI;
import ru.itskekoff.client.commands.CommandManager;
import ru.itskekoff.client.configuration.ConfigManager;
import ru.itskekoff.client.discord.DiscordInstance;
import ru.itskekoff.client.fix.GhostBlockFix;
import ru.itskekoff.client.image.ImageObject;
import ru.itskekoff.client.module.ModuleManager;
import ru.itskekoff.client.module.impl.bots.BotCaptcha;
import ru.itskekoff.event.EventManager;
import ru.itskekoff.event.handler.SimpleHandler;
import ru.itskekoff.protocol.data.Position;
import ru.itskekoff.protocol.packet.PacketRegistry;
import ru.itskekoff.protocol.packet.impl.client.play.ClientChatPacket;
import ru.itskekoff.protocol.packet.impl.client.play.ClientPlayerPositionPacket;
import ru.itskekoff.protocol.packet.impl.client.play.ClientPlayerPositionRotationPacket;
import ru.itskekoff.utils.CaptchaUtils;
import ru.itskekoff.utils.ThreadUtils;
import ru.itskekoff.utils.TimerHelper;
import viamcp.ViaMCP;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static baritone.api.utils.Helper.mc;

@Data
public class SolevoyClient {
    private static SolevoyClient instance = new SolevoyClient();
    private static TimerHelper timerHelper;
    private final Set<Bot> bots = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final List<CachedChunk> cachedChunks = new CopyOnWriteArrayList<>();
    private final HashMap<Bot, ImageObject> maps = new HashMap<>();
    private final HashMap<Bot, String> mapAnswers = new HashMap<>();
    private final ExecutorService taskExecutor = Executors.newFixedThreadPool(8);
    private final ScheduledExecutorService taskScheduler = Executors.newSingleThreadScheduledExecutor();
    public CommandManager commandManager;
    public PacketRegistry packetRegistry;
    public Minecraft mc = Minecraft.getInstance();
    int cycle = 0;
    private String minecraftFolder = "./SolevoyClient";
    private String proxiesFile4 = minecraftFolder + "/bots/proxy/socks4.txt";
    private String proxiesFile5 = minecraftFolder + "/bots/proxy/socks5.txt";
    private String proxiesFileH = minecraftFolder + "/bots/proxy/http.txt";
    private String configFile = minecraftFolder + "/bots/proxy/config.yml";
    private Bot currentSolvingBot = null;
    private Position position;
    private List<MotherRecord> motherRecords = new CopyOnWriteArrayList<>();
    private MotherRecord currentMotherRecord;
    private boolean mother = false;
    private int motherDelay = 200;
    private boolean connectingBots = false;
    private MacroManager macroManager;
    private ModuleManager moduleManager;
    private ConfigManager configManager;
    private ClickGUI clickGUI;
    private CaptchaUtils captchaUtils;
    private DiscordInstance discordInstance;
    private NickLoader nickLoader;
    public ProxyLoader proxyLoader;
    private final Timer timer = new Timer(20.0F);
    private boolean active = false;

    public static void setDisplay(String name) {
        Display.setTitle(name);
    }

    public static SolevoyClient getInstance() {
        return instance;
    }

    public void load() throws Exception {
        instance = this;
        if (!new File(minecraftFolder).exists()) {
            new File(minecraftFolder).mkdir();
        }
        discordInstance = new DiscordInstance();
        packetRegistry = new PacketRegistry();
        packetRegistry.init();
        macroManager = new MacroManager();
        macroManager.init();
        commandManager = new CommandManager();
        commandManager.init();
        moduleManager = new ModuleManager();
        configManager = new ConfigManager();
        configManager.load();
        proxyLoader = new ProxyLoader();
        captchaUtils = new CaptchaUtils();
        clickGUI = new ClickGUI();
        nickLoader = new NickLoader();
        EventManager.register(new SimpleHandler());
        EventManager.register(new GhostBlockFix());
        EventManager.register(this);
        ProxyLoader.init();
        try {
            ViaMCP.getInstance().start();
            ViaMCP.getInstance().initAsyncSlider();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BaritoneAPI.getProvider().getPrimaryBaritone();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> configManager.saveConfig("latest")));
        startThreads();
    }

    public void startThreads() {
        taskExecutor.execute(() -> {
            while (true) {
                timer.updateTimer();
                for (int i = 0; i < Math.min(10, timer.getElapsedTicks()); ++i) {
                    updateBots();
                }

                ThreadUtils.sleep(10L);
            }
        });
        taskScheduler.scheduleWithFixedDelay(() -> {
            if (mc.currentScreen instanceof GuiChat) {
                BaritoneAPI.getProvider().getPrimaryBaritone().getInputOverrideHandler().clearAllKeys();
            }
            try {
                try {
                    for (CachedChunk chunk : cachedChunks) {
                        chunk.getUsages().removeIf(bot -> !bot.getSession().isChannelOpen() || !bots.contains(bot));

                        if (chunk.getUsages().size() == 0) {
                            cachedChunks.remove(chunk);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                bots.forEach(bot -> {
                    try {
                        int diff = Integer.parseInt(String.valueOf(bot.getLastKeepAlive() - System.currentTimeMillis()).split("\\.")[0]);
                        // проверка на таймаут (5 секунд), удаление бота из списка и отключение возможности перезайти боту.
                        if (diff >= 5000) {
                            bot.onDisconnect();
                            bot.getSession().setDisconnectedManually(true);
                        }
                    } catch (NumberFormatException ignored) {}
                });
            } catch (Throwable e) {
                e.printStackTrace();
            }
            try {
                macroManager.init();
            } catch (IOException ignored) {
            }
            HashMap<BufferedImage, Bot> copy = BotCaptcha.getCaptchas();
            copy.forEach((image, bot) -> {
                if (captchaUtils.processCaptcha(image, bot)) BotCaptcha.getCaptchas().remove(image);
            });
        }, 0L, 1L, TimeUnit.SECONDS);

    }

    public void handleChat(String message) {
        try {
            SolevoyClient.getInstance().getMacroManager().getCurrentMacroRecord().getPackets().add(new ClientChatPacket(message));
            if (currentSolvingBot != null) {
                mapAnswers.put(currentSolvingBot, message);
            }
        } catch (Exception ignored) {
        }
    }

    public void updateBots() {
        cycle++;
        bots.forEach(bot -> {
            if (cycle == 20 || bot.getLastX() != bot.getX() || bot.getLastY() != bot.getY() || bot.getLastZ() != bot.getZ() || bot.getLastPitch() != bot.getPitch() || bot.getLastYaw() != bot.getYaw()) {
                if (bot.getLastPitch() != bot.getPitch() || bot.getLastYaw() != bot.getYaw()) {
                    bot.getSession().sendPacket(new ClientPlayerPositionRotationPacket(bot.getX(), bot.getY(), bot.getZ(), bot.getYaw(), bot.getPitch(), bot.isOnGround()));
                } else {
                    bot.getSession().sendPacket(new ClientPlayerPositionPacket(bot.getX(), bot.getY(), bot.getZ(), bot.isOnGround()));
                }
            }
            bot.setLastX(bot.getX());
            bot.setLastY(bot.getY());
            bot.setLastZ(bot.getZ());
            bot.setLastYaw(bot.getYaw());
            bot.setLastPitch(bot.getPitch());
            bot.onUpdate();

            if (cycle == 20) cycle = 0;
        });

        if (cycle == 20) cycle = 0;
    }

    public boolean isConnected() {
        try {
            return Minecraft.getMinecraft().player.connection.getGameProfile().getName() != null;
        } catch (NullPointerException exception) {
            return false;
        }
    }

}
