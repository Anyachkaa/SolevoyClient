package ru.itskekoff.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

import java.util.Arrays;
import java.util.stream.IntStream;

public class ChatUtil {
    public static String fixColor(String text) {
        return text.replace('&', '\u00A7')
                .replace(">>", "»")
                .replace("<<", "«")
                .replace("(o)", "●")
                .replace("(*)", "•");
    }

    public static void sendChatMessage(String message, boolean prefix) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(fixColor((prefix ? "&bSolevoy&rClient &7(o) " : "") + "&f" + message)));
    }


    public static void clearChat(int x) {
        IntStream.range(0, x).forEach(i -> sendChatMessage(" ",false));
    }
}