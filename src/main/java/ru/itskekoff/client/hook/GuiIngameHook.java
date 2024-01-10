package ru.itskekoff.client.hook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.opengl.GL11;
import ru.itskekoff.bots.proxy.ProxyLoader;
import ru.itskekoff.client.SolevoyClient;
import ru.itskekoff.client.render.RenderUtils;
import ru.itskekoff.utils.ChatUtil;
import ru.itskekoff.utils.PacketUtils;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class GuiIngameHook extends GuiIngame {
    public static Minecraft mc = Minecraft.getMinecraft();
    public DecimalFormat df = new DecimalFormat("#.##");
    public GuiIngameHook(Minecraft mcIn) {
        super(mcIn);
    }

    @Override
    public void renderGameOverlay(float partialTicks) {
        super.renderGameOverlay(partialTicks);
        if (SolevoyClient.getInstance().getModuleManager().getModule("HUD").isToggled()) {
            if (!GameSettings.showDebugInfo) {
                renderInfo();
                List<String> elements = new CopyOnWriteArrayList<>();
                elements.add("X: " + (int) mc.player.posX + " Y: " + (int) mc.player.posY + " Z: " + (int) mc.player.posZ);
                elements.add((new SimpleDateFormat("HH:mm")).format(Calendar.getInstance().getTime()));
                elements.add((new SimpleDateFormat("dd/MM/yyyy")).format(Calendar.getInstance().getTime()));
                renderHud(elements);
            }
        }
    }

    private void renderInfo() {
        List<String> elements = new CopyOnWriteArrayList<>();
        elements.add(ChatUtil.fixColor("&fBots online: &b" + SolevoyClient.getInstance().getBots().size()));
        elements.add(ChatUtil.fixColor("&fMother enabled: &b" + (SolevoyClient.getInstance().isMother() ? "&bEnabled" : "&bDisabled")));
        elements.add(ChatUtil.fixColor("&fMacro playing: &b" + (SolevoyClient.getInstance().getMacroManager().isMacro() ? String.format("&bEnabled &f(&b%s, %d records&f)", SolevoyClient.getInstance().getMacroManager().getCurrentMacro().getName(), SolevoyClient.getInstance().getMacroManager().getCurrentMacro().getRecords().size()) : "&bDisabled")));
        elements.add(ChatUtil.fixColor("&fProxies size: &b" + ProxyLoader.globalProxies.size() + " &f(&b" + ProxyLoader.privateProxies.size() + "&f)&r"));
        elements.add(ChatUtil.fixColor("&fCurrent session: &b" + (mc.isSingleplayer() ? "&bSingleplayer" : String.format("&b%s:%d", GuiConnecting.host, GuiConnecting.port))));
        if (!mc.isSingleplayer()) {
            String core = mc.player.getServerBrand();
            try {
                if (core.contains("git:")) {
                    core = mc.player.getServerBrand().split("\\(")[0].replace(" ", "") + mc.player.getServerBrand().split("\\)")[1];
                }
            } catch (Exception ignored) {
            }
            elements.add(ChatUtil.fixColor("&fServer brand: &b" + core));
        }
        elements.add(ChatUtil.fixColor("&fTPS: &b" + df.format(PacketUtils.tps) + " TPS"));
        GL11.glScalef(1.0F, 1.0F, 1.0F);
        AtomicInteger x = new AtomicInteger(10);
        AtomicInteger y = new AtomicInteger(2);
        String largest = elements.stream().max(Comparator.comparing(String::length)).get();
        String builder = String.valueOf(elements.size()) + 0;
        RenderUtils.drawFixedBorderedRect(x.get() - 2, 10, mc.fontRenderer.getStringWidth(largest) + 32, Integer.parseInt(builder), 6, new Color(0xAD000000, true), new Color(35, 210, 248, 255));
        elements.forEach(element -> {
            y.addAndGet(8);
            mc.fontRenderer.drawStringWithShadow(element, x.get(), y.get(), -1);
        });
    }

    private void renderHud(List<String> chatElements) {
        ScaledResolution sr = new ScaledResolution(mc);
        if (!(mc.currentScreen instanceof GuiChat)) {
            mc.fontRenderer.drawStringWithShadow(chatElements.get(0), 3, sr.getScaledHeight() - 10, -1);
            mc.fontRenderer.drawStringWithShadow(chatElements.get(1), (float) (sr.getScaledWidth() - mc.fontRenderer.getStringWidth(chatElements.get(1)) - 18), (float) (sr.getScaledHeight() - 19.5), -1);
            mc.fontRenderer.drawStringWithShadow(chatElements.get(2), (float) (sr.getScaledWidth() - mc.fontRenderer.getStringWidth(chatElements.get(2)) - 7), (float) (sr.getScaledHeight() - 10.5), -1);
        }
    }
}
