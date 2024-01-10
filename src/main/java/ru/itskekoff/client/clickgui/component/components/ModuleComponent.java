package ru.itskekoff.client.clickgui.component.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import ru.itskekoff.client.clickgui.component.Component;
import ru.itskekoff.client.clickgui.component.Frame;
import ru.itskekoff.client.clickgui.component.components.sub.*;
import ru.itskekoff.client.clickgui.component.components.sub.TextComponent;
import ru.itskekoff.client.clickgui.settings.Setting;
import ru.itskekoff.client.clickgui.settings.impl.*;
import ru.itskekoff.client.module.Module;

import java.awt.*;
import java.util.ArrayList;

public class ModuleComponent extends Component {

    public Module mod;
    public Frame parent;
    public int offset;
    public boolean open;
    public int height;
    public FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    private boolean isHovered;
    private ArrayList<Component> subcomponents;

    public ModuleComponent(Module mod, Frame parent, int offset) {
        this.mod = mod;
        this.parent = parent;
        this.offset = offset;
        this.height = 12;
        this.subcomponents = new ArrayList<Component>();
        this.open = false;
        int opY = offset + 12;
        if (mod.getSettings() != null) {
            for (Setting s : mod.getSettings()) {
                if (s instanceof ListSetting) {
                    this.subcomponents.add(new ListComponent((ListSetting) s, this, mod, opY));
                    opY += 12;
                }
                if (s instanceof SliderSetting) {
                    this.subcomponents.add(new SliderComponent((SliderSetting) s, this, opY));
                    opY += 12;
                }
                if (s instanceof BooleanSetting) {
                    this.subcomponents.add(new BooleanComponent((BooleanSetting) s, this, opY));
                    opY += 12;
                }
                if (s instanceof StringSetting) {
                    this.subcomponents.add(new TextComponent((StringSetting) s, this, opY));
                    opY += 12;
                }
            }
        }
        this.subcomponents.add(new BindComponent(this, opY));
        this.subcomponents.add(new VisibleComponent(this, mod, opY));
        //this.subcomponents.add(new InfoButton(this, mod, opY));
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
        int opY = offset + 12;
        for (Component comp : this.subcomponents) {
            comp.setOff(opY);
            opY += 12;
        }
    }

    @Override
    public void renderComponent() {
        if (isHovered) {
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            Gui.drawRect(0, 57, Minecraft.getMinecraft().fontRenderer.getStringWidth(mod.getDescription()) + 32, 40, 0x4F000000);
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.mod.getDescription() + ".", 3, 45, -1);
        }
        Gui.drawRect(parent.getX(), this.parent.getY() + this.offset, parent.getX() + parent.getWidth(), this.parent.getY() + 12 + this.offset, this.isHovered ? 0xFF222222 : 0xFF111111);
        Minecraft.getMinecraft().getMain().drawStringWithShadow(this.mod.getName(), (parent.getX() + 5), (parent.getY() + offset + 3), this.mod.isToggled() ? Color.cyan.getRGB() : Color.white.getRGB()); //0x999999
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.open ? "§7▼" : "§7▶", (parent.getX() + parent.getWidth() - 10), (parent.getY() + offset + 2), -1);
        if (this.open) {
            if (!this.subcomponents.isEmpty()) {
                for (Component comp : this.subcomponents) {
                    comp.renderComponent();
                }
                //Gui.drawRect(parent.getX() + 0, parent.getY() + this.offset + 12, parent.getX() + 1, parent.getY() + this.offset + ((this.subcomponents.size() + 1) * 12), ClickGuiManager.color);
            }
        }
    }

    @Override
    public int getHeight() {
        if (this.open) {
            return (12 * (this.subcomponents.size() + 1));
        }
        return 12;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.isHovered = isMouseOnButton(mouseX, mouseY);
        if (!this.subcomponents.isEmpty()) {
            for (Component comp : this.subcomponents) {
                comp.updateComponent(mouseX, mouseY);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && button == 0) {
            this.mod.toggle();
            client.getConfigManager().saveConfig("latest");
        }
        if (isMouseOnButton(mouseX, mouseY) && button == 1) {
            this.open = !this.open;
            this.parent.refresh();
        }
        for (Component comp : this.subcomponents) {
            comp.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (Component comp : this.subcomponents) {
            comp.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        for (Component comp : this.subcomponents) {
            comp.keyTyped(typedChar, key);
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > parent.getX() && x < parent.getX() + parent.getWidth() && y > this.parent.getY() + this.offset && y < this.parent.getY() + 12 + this.offset;
    }
}
