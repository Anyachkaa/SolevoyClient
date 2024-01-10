package ru.itskekoff.client.module.impl.bots;

import lombok.Getter;
import lombok.Setter;
import ru.itskekoff.bots.Bot;
import ru.itskekoff.captcha.wrapper.ApiCaptcha;
import ru.itskekoff.captcha.wrapper.modes.ApiType;
import ru.itskekoff.captcha.wrapper.modes.impl.Normal;
import ru.itskekoff.client.clickgui.settings.impl.*;
import ru.itskekoff.client.module.Category;
import ru.itskekoff.client.module.Module;
import ru.itskekoff.event.EventTarget;
import ru.itskekoff.event.impl.EventCaptchaReceived;
import ru.itskekoff.event.impl.EventUpdate;
import ru.itskekoff.event.types.Priority;
import ru.itskekoff.protocol.data.ItemStack;
import ru.itskekoff.protocol.packet.impl.client.play.ClientChatPacket;
import ru.itskekoff.protocol.packet.impl.server.play.ServerMapDataPacket;
import ru.itskekoff.utils.BasicColor;
import ru.itskekoff.utils.notification.NotificationRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class BotCaptcha extends Module {
    private static @Getter
    @Setter HashMap<BufferedImage, Bot> captchas = new HashMap<>();

    private boolean windowOpened;

    public ListSetting mode;
    public BooleanSetting numeric;
    public BooleanSetting math;
    public StringSetting apiKey;
    public StringSetting instructions;

    public BotCaptcha() {
        super("BotCaptcha", "Настройки ботов", Category.Bots);
        mode = new ListSetting("Mode", "CaptchaMode", "JustNanixAI", "CaptchaGuru", "RuCaptcha", "TwoCaptcha", "JustNanixAI", "Window");
        apiKey = new StringSetting("ApiKey", "Captcha key", "none", 32);
        instructions = new StringSetting("Instructions", "Instructions", "Введите всё, что написано на картинке.", 256);
        numeric = new BooleanSetting("Numeric", "Numeric captcha", true);
        math = new BooleanSetting("Math", "Math captcha", false);
        addSettings(mode, apiKey, instructions, numeric, math);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mode.getCurrentMode().equals("Window")) {
            if (!windowOpened) {
                client.getCaptchaUtils().setVisible(true);
                windowOpened = true;
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mode.getCurrentMode().equals("Window")) {
            if (windowOpened) {
                client.getCaptchaUtils().setVisible(false);
                windowOpened = false;
            }
        }
    }

    @EventTarget
    public void onReceiveCaptcha(EventCaptchaReceived event) {
        if (!mode.getCurrentMode().equals("Window")) {
            ServerMapDataPacket packet = event.getPacket();
            Bot bot = event.getBot();
            if (bot.getInventory().getItems().contains(new ItemStack(358)) && bot.captchaTries != 0) {
                client.getTaskExecutor().execute(() -> {
                    try {
                        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_4BYTE_ABGR);
                        for (int x = 0; x < 128; x++) {
                            for (int y = 0; y < 128; y++) {
                                byte input = packet.getData().getData()[x + y * 128];
                                int colId = (input >>> 2) & 0b11111;
                                byte shader = (byte) (input & 0b11);

                                BasicColor col = BasicColor.colors.get(colId);
                                if (col == null) {
                                    col = BasicColor.TRANSPARENT;
                                }

                                image.setRGB(x, y, col.shaded(shader));
                            }
                        }

                        File captchaFile = new File("./SolevoyClient" + File.separator + "bots" + File.separator + "cache" + File.separator + "captcha" + File.separator + new Random(System.currentTimeMillis()).nextInt(999999999) + ".png");
                        captchaFile.getParentFile().mkdirs();
                        ImageIO.write(image, "PNG", captchaFile);
                        try {
                            ApiType apiType = ApiType.valueOf(mode.getCurrentMode());
                            ApiCaptcha apiCaptcha = new ApiCaptcha(apiKey.getCurrentText(), apiType);
                            Normal normal = new Normal();
                            normal.setFile(captchaFile);
                            if (numeric.isToggled()) {
                                normal.setNumeric(1);
                            } else {
                                normal.setNumeric(3);
                            }
                            normal.setCalc(math.isToggled());
                            normal.setHintText(instructions.getCurrentText());
                            if (apiType == ApiType.RuCaptcha) {
                                normal.setCaseSensitive(true);
                            } else if (apiType == ApiType.CaptchaGuru) {
                                normal.setReturnId(14);
                            } else if (apiType == ApiType.JustNanixAI) {
                                apiCaptcha.setApiKey("itscumoff228");
                            }
                            apiCaptcha.solve(normal);
                            if (bot.getSession().isChannelOpen()) {
                                NotificationRenderer.queue("Капча решена", "Ответ: " + normal.getCode(), 1);
                                bot.getSession().sendPacket(new ClientChatPacket(normal.getCode()));
                            }
                        } catch (Throwable e) {
                            if (e.getMessage().contains("UNSOLVABLE")) {
                                if (bot.getSession().isChannelOpen()) {
                                    bot.getSession().sendPacket(new ClientChatPacket("null"));
                                }
                            }
                        }
                        bot.captchaTries--;
                    } catch (Throwable ignored) {
                    }
                });
            }
        }

        if (mode.getCurrentMode().equals("Window")) {
            ServerMapDataPacket packet = event.getPacket();
            Bot bot = event.getBot();
            if (bot.getInventory().getItems().contains(new ItemStack(358)) && bot.captchaTries != 0) {
                client.getTaskExecutor().execute(() -> {
                    if (!windowOpened) {
                        client.getCaptchaUtils().setVisible(true);
                        windowOpened = true;
                    }
                    try {
                        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_4BYTE_ABGR);
                        for (int x = 0; x < 128; x++) {
                            for (int y = 0; y < 128; y++) {
                                byte input = packet.getData().getData()[x + y * 128];
                                int colId = (input >>> 2) & 0b11111;
                                byte shader = (byte) (input & 0b11);

                                BasicColor col = BasicColor.colors.get(colId);
                                if (col == null) {
                                    col = BasicColor.TRANSPARENT;
                                }

                                image.setRGB(x, y, col.shaded(shader));
                            }
                        }

                        File captchaFile = new File("./SolevoyClient" + File.separator + "cache" + File.separator + "captcha" + File.separator + new Random(System.currentTimeMillis()).nextInt(999999999) + ".png");
                        captchaFile.getParentFile().mkdirs();
                        ImageIO.write(image, "PNG", captchaFile);
                        captchas.put(image, bot);
                    } catch (Exception ignored) {
                    }
                });
            }
        }
    }
}
