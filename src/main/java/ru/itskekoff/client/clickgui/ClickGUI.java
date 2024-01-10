package ru.itskekoff.client.clickgui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import ru.itskekoff.client.SolevoyClient;
import ru.itskekoff.client.clickgui.component.Component;
import ru.itskekoff.client.clickgui.component.Frame;
import ru.itskekoff.client.clickgui.settings.impl.*;
import ru.itskekoff.client.module.Category;
import ru.itskekoff.client.module.impl.render.ClickGui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class ClickGUI extends GuiScreen {
    public static ArrayList<Frame> frames;
    public static int color;
    private final ArrayList<Effect> effectList = new ArrayList<Effect>();

    private SolevoyClient client = SolevoyClient.getInstance();

    public ClickGUI() {
        frames = new ArrayList<Frame>();
        int frameX = 2;
        for (Category category : Category.values()) {
            Frame frame = new Frame(category);
            frame.setX(frameX);
            frames.add(frame);
            frameX += frame.getWidth();
        }
        Random random = new Random();
        for (int i = 0; i < 100; ++i) {
            for (int y = 0; y < 3; ++y) {
                Effect snow = new Effect(25 * i, y * -50, random.nextInt(3) + 1, random.nextInt(2) + 1);
                effectList.add(snow);
            }
        }
    }

    @Override
    public void initGui() {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (ClickGui.background.isToggled())
            drawDefaultBackground();
        final ScaledResolution res = new ScaledResolution(mc);
        if (ClickGui.effect.getCurrentMode().equals("Snow")) {
            if (!effectList.isEmpty()) {
                effectList.forEach(snow -> snow.update(res));
            }
        }
        GL11.glPushMatrix();
        ScaledResolution sr = new ScaledResolution(mc);
        GL11.glPopMatrix();
        for (Frame frame : frames) {
            frame.renderFrame(Minecraft.getMinecraft().getText());
            frame.updatePosition(mouseX, mouseY);
            for (Component comp : frame.getComponents()) {
                comp.updateComponent(mouseX, mouseY);
            }
        }
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        for (Frame frame : frames) {
            if (frame.isWithinHeader(mouseX, mouseY) && mouseButton == 0) {
                frame.setDrag(true);
                frame.dragX = mouseX - frame.getX();
                frame.dragY = mouseY - frame.getY();
            }
            if (frame.isWithinHeader(mouseX, mouseY) && mouseButton == 1) {
                frame.setOpen(!frame.isOpen());
            }
            if (frame.isOpen()) {
                if (!frame.getComponents().isEmpty()) {
                    for (Component component : frame.getComponents()) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        for (Frame frame : frames) {
            if (frame.isOpen() && keyCode != 1) {
                if (!frame.getComponents().isEmpty()) {
                    for (Component component : frame.getComponents()) {
                        component.keyTyped(typedChar, keyCode);
                    }
                }
            }
        }
        if (keyCode == 19) {
            frames = new ArrayList<>();
            int frameX = 2;
            for (Category category : Category.values()) {
                Frame frame = new Frame(category);
                ScaledResolution sr = new ScaledResolution(mc);
                if (!(sr.getScaledWidth() < frame.getWidth() * 7)) {
                    frame.setX(frameX);
                    frames.add(frame);
                    frameX += frame.getWidth();
                }
            }
        }
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
        }
    }


    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (Frame frame : frames) {
            frame.setDrag(false);
        }
        for (Frame frame : frames) {
            if (frame.isOpen()) {
                if (!frame.getComponents().isEmpty()) {
                    for (Component component : frame.getComponents()) {
                        component.mouseReleased(mouseX, mouseY, state);
                    }
                }
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }


    public static class TextBoxGUI extends GuiScreen {
        private final StringSetting setting;
        private GuiTextField text;
        private SolevoyClient client = SolevoyClient.getInstance();

        public TextBoxGUI(StringSetting setting) {
            this.setting = setting;
        }

        @Override
        public void initGui() {
            this.text = new GuiTextField(0, this.fontRenderer, this.width / 2 - 68, this.height / 2 - 46, 137, 20);
            text.setMaxStringLength(setting.getKeyLen());
            text.setText(setting.getCurrentText());
            this.text.setFocused(true);
        }

        @Override
        public void keyTyped(char typedChar, int keyCode) throws IOException {
            this.text.textboxKeyTyped(typedChar, keyCode);
            if (keyCode == 28 || keyCode == 1) {
                setting.setCurrentText(this.text.getText());
                Minecraft.getMinecraft().displayGuiScreen(new ClickGUI());
            }
            setting.setCurrentText(this.text.getText());
            if (!(keyCode == Keyboard.KEY_E && this.text.isFocused()))
                super.keyTyped(typedChar, keyCode);
            client.getConfigManager().saveConfig("latest");
        }

        public void updateScreen() {
            super.updateScreen();
            this.text.updateCursorCounter();
        }

        public void drawScreen(int par1, int par2, float par3) {
            drawDefaultBackground();
            this.text.drawTextBox();
        }

        protected void mouseClicked(int x, int y, int btn) throws IOException {
            super.mouseClicked(x, y, btn);
            this.text.mouseClicked(x, y, btn);
        }

        @Override
        public void onGuiClosed() {
            super.onGuiClosed();
        }
    }

}
