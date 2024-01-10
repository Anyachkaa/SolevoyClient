package ru.itskekoff.client.commands.impl.bot.bypass;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import ru.itskekoff.bots.Bot;
import ru.itskekoff.client.commands.impl.bot.bypass.diff.DiffItem;
import ru.itskekoff.protocol.data.ItemStack;
import ru.itskekoff.protocol.data.WindowAction;
import ru.itskekoff.protocol.packet.impl.client.play.ClientChatPacket;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public @Data class BotBypassController {
    private final Bot bot;
    private boolean solved;

    public BotBypassController(Bot bot) {
        this.bot = bot;
    }

    public void solveCaptcha(String message) {
        try {
            String line = findStringByRegex(message, Pattern.compile("\\..* /verify .*")).replace("\"", " ");

            if (StringUtils.containsIgnoreCase(message, "clickEvent")) {
                bot.getSession().sendPacket(new ClientChatPacket(line.split(" ")[0] + " /verify " + line.split(" ")[2]));
                solved = true;
            }
        } catch (Exception ignored) {
        }
        try {
            String line = findStringByRegex(message, Pattern.compile("/captcha .*")).replace("»", " ");

            if (line.contains(":"))
                bot.getSession().sendPacket(new ClientChatPacket("/captcha " + line.split(":")[1].trim()));

            bot.getSession().sendPacket(new ClientChatPacket("/captcha " + line.split(" ")[1]));
            solved = true;
        } catch (Exception ignored) {
        }
        try {
            String line = findStringByRegex(message, Pattern.compile("Type .*"));

            if (StringUtils.containsIgnoreCase(message, "prove")) {
                bot.getSession().sendPacket(new ClientChatPacket(line.split(" ")[1]));
            }
            solved = true;
        } catch (Exception ignored) {
        }
        try {
            String line = findStringByRegex(message, Pattern.compile("following code: .*"));
            bot.getSession().sendPacket(new ClientChatPacket(line.split(" ")[2]));
            solved = true;
        } catch (Exception ignored) {
        }

        try {
            String line = findStringByRegex(message, Pattern.compile("clique na cor .*"));

            bot.getSession().sendPacket(new ClientChatPacket("/color " + line.split(" ")[3]));
            solved = true;
        } catch (Exception ignored) {
        }
    }

    public void solveItemName(String name) {
        int slot = 0;
        for (ItemStack item : bot.getOpenContainer().getItems()) {
            if (!item.getName().contains(name)) slot++;
            else {
                bot.getOpenContainer().slotClick(bot, (short) slot, 0, WindowAction.CLICK_ITEM);
                solved = true;
            }
        }
    }

    public void solveDifferent() {
        if (bot.getOpenContainer() == null) return;
        List<DiffItem> diffTypes = new CopyOnWriteArrayList<>();
        bot.getOpenContainer().getItems().forEach(stack -> {
            DiffItem diffItem = DiffItem.fromSlot(bot, stack);
            if (!diffTypes.contains(diffItem)) diffTypes.add(diffItem);
            else diffTypes.get(diffTypes.indexOf(diffItem)).addCount();
        });
        AtomicReference<DiffItem> item = new AtomicReference<>(diffTypes.stream()
                .sorted(Comparator.comparing(DiffItem::getSlotNum))
                .min(Comparator.comparing(DiffItem::getCount)).orElse(null));
        bot.getOpenContainer().getItems().forEach(stack -> {
            DiffItem diffItem = DiffItem.fromSlot(bot, stack);

            if (diffTypes.contains(diffItem)) {
                diffItem = diffTypes.get(diffTypes.indexOf(diffItem));
                if (item.get().getCount() == diffItem.getCount() && (item.get().getSlotNum() > diffItem.getSlotNum() || item.get().getSlotNum() > 35)) {
                    item.set(diffItem);
                }
            }
        });

        DiffItem finalDiffItem = item.get();
        bot.getOpenContainer().getItems().stream()
                .filter(slot -> finalDiffItem.equals(DiffItem.fromSlot(bot, slot)))
                .findAny().ifPresent(slot -> {
                    bot.getOpenContainer().slotClick(bot, item.get().getSlotNum(), 0, WindowAction.CLICK_ITEM);
                    solved = true;
                });
    }

    public void solveMath(String message) {
        String[] numbers1 = {"①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧", "⑨", "⑩", "⑪", "⑫", "⑬", "⑭", "⑮", "⑯", "⑰", "⑱", "⑲", "⑳"};
        String[] numbers2 = {"⑴", "⑵", "⑶", "⑷", "⑸", "⑹", "⑺", "⑻", "⑼", "⑽", "⑾", "⑿", "⒀", "⒁", "⒂", "⒃", "⒄", "⒅", "⒆", "⒇"};
        String[] numbers3 = {"⒈", "⒉", "⒊", "⒋", "⒌", "⒍", "⒎", "⒏", "⒐", "⒑", "⒒", "⒓", "⒔", "⒕", "⒖", "⒗", "⒘", "⒙", "⒚", "⒛"};
        String[] numbers4 = {"ноль", "один", "два", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять", "десять", "одиннадцать", "двенадцать", "тринадцать", "четырнадцать", "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать"};
        String[] numbers5 = {"⓫", "⓬", "⓭", "⓮", "⓯", "⓰", "⓱", "⓲", "⓳", "⓴"};
        String[] numbers6 = {"０", "９", "７", "６", "５", "４", "３", "２", "１"};
        String[] operators = {"＊", "＋", "－", "／"};
        String[] converted = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "+", "-", "/", "*"};

        if (message.contains("реши")) {
            Pattern pattern = Pattern.compile("(.*)Решите пример: (.*)");
            Matcher matcher = pattern.matcher(message);
            String math;
            while (matcher.find()) {
                math = matcher.group(2);
                for (int i = 0; i < converted.length; i++) {
                    if (!(i >= numbers1.length)) math = math.replaceAll(numbers1[i], converted[i]);
                    if (!(i >= numbers2.length)) math = math.replaceAll(numbers2[i], converted[i]);
                    if (!(i >= numbers3.length)) math = math.replaceAll(numbers3[i], converted[i]);
                    if (!(i >= numbers4.length)) math = math.replaceAll(numbers4[i], converted[i]);
                    if (!(i >= numbers5.length)) math = math.replaceAll(numbers5[i], converted[i]);
                    if (!(i >= numbers6.length)) math = math.replaceAll(numbers6[i], converted[i]);
                    if (i >= 21) math = math.replaceAll(operators[i], converted[i]);
                }
                double result = 0.0D;
                String[] numbers;
                if (math.contains("+")) {
                    numbers = math.split("[+]");
                    result = Double.parseDouble(numbers[0]) + Double.parseDouble(numbers[1]);
                } else if (math.contains("-")) {
                    numbers = math.split("-");
                    result = Double.parseDouble(numbers[0]) - Double.parseDouble(numbers[1]);
                } else if (math.contains("/")) {
                    numbers = math.split("/");
                    result = Double.parseDouble(numbers[0]) / Double.parseDouble(numbers[1]);
                } else if (math.contains("*")) {
                    numbers = math.split("[*]");
                    result = Double.parseDouble(numbers[0]) * Double.parseDouble(numbers[1]);
                }
                bot.getSession().sendPacket(new ClientChatPacket(String.valueOf(result)));
                solved = true;
            }
        }
    }

    public static List<String> findStringsByRegex(String text, Pattern regex) {
        List<String> strings = new ArrayList<>();
        Matcher match = regex.matcher(text);

        while (match.find())
            strings.add(text.substring(match.start(), match.end()));

        return strings;
    }

    public static String findStringByRegex(String text, Pattern regex) {
        Matcher match = regex.matcher(text);

        if (match.find())
            return text.substring(match.start(), match.end());

        return null;
    }
}
