package ru.itskekoff.client.clickgui.component;

import lombok.Data;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;
import ru.itskekoff.client.SolevoyClient;
import ru.itskekoff.client.clickgui.component.components.ModuleComponent;
import ru.itskekoff.client.font.MCFontRenderer;
import ru.itskekoff.client.module.Category;
import ru.itskekoff.client.module.Module;

import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;

public @Data class Frame {

    public static int color;
    public ArrayList<Component> components;
    public Category category;
    public int dragX;
    public int dragY;
    private boolean open;
    private int width;
    private int y;
    private int x;
    private int barHeight;
    private boolean isDragging;

    private Minecraft mc = Minecraft.getMinecraft();

    public Frame(Category cat) {
        this.components = new ArrayList<Component>();
        this.category = cat;
        this.width = 88;
        this.x = 0;
        this.y = 60;
        this.dragX = 0;
        this.barHeight = 12;
        this.open = true;
        this.isDragging = false;
        int tY = this.barHeight;
        for (Module mod : SolevoyClient.getInstance().getModuleManager().getModulesInCategory(category)) {
            ModuleComponent modButton = new ModuleComponent(mod, this, tY);
            this.components.add(modButton);
            tY += 12;
        }
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    public void setDrag(boolean drag) {
        this.isDragging = drag;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void renderFrame(MCFontRenderer fontRenderer) { //TODO TRANSPERMENT BUTTONS AND GRADIENT IN FRAME 0x9F000000
        //Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.barHeight, Visual.getIntFromColor(Visual.rainbow().getRed(),Visual.rainbow().getGreen(),Visual.rainbow().getBlue()));
        if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 31 && Calendar.getInstance().get(Calendar.MONTH) == Calendar.DECEMBER) {
            Gui.drawRect(this.x, this.y - 1, this.x + this.width, this.y, -1);
        } else {
            Gui.drawRect(this.x, this.y - 1, this.x + this.width, this.y, Color.cyan.getRGB());
        }
        Gui.drawRect(this.x, this.y, this.x + this.width, this.y + 12, 0xFF111111);
        fontRenderer.drawStringWithShadow(this.category.name(), this.x, this.y + 5, -1);
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.open ? "-" : "+", (this.x + this.width - 12.5f) * 2 + 5, (this.y + 1f) * 2 + 5, -1);
        GL11.glPopMatrix();
        if (this.open) {
            if (!this.components.isEmpty()) {
                for (Component component : components) {
                    component.renderComponent();
                }
            }
        }
    }

    public void refresh() {
        int off = this.barHeight;
        for (Component comp : components) {
            comp.setOff(off);
            off += comp.getHeight();
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int newX) {
        this.x = newX;
    }

    public int getY() {
        return y;
    }

    public void setY(int newY) {
        this.y = newY;
    }

    public int getWidth() {
        return width;
    }

    public void updatePosition(int mouseX, int mouseY) {
        if (this.isDragging) {
            this.setX(mouseX - dragX);
            this.setY(mouseY - dragY);
        }
    }

    public boolean isWithinHeader(int x, int y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.barHeight;
    }

}
