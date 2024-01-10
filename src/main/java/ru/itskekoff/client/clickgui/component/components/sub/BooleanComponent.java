package ru.itskekoff.client.clickgui.component.components.sub;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;
import ru.itskekoff.client.clickgui.component.Component;
import ru.itskekoff.client.clickgui.component.components.ModuleComponent;
import ru.itskekoff.client.clickgui.settings.impl.*;

import java.awt.*;

public class BooleanComponent extends Component {

    private boolean hovered;
    private BooleanSetting op;
    private ModuleComponent parent;
    private int offset;
    private int x;
    private int y;

    public BooleanComponent(BooleanSetting option, ModuleComponent button, int offset) {
        this.op = option;
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
    }

    @Override
    public void renderComponent() {
        Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth() * 1), parent.parent.getY() + offset + 12, this.hovered ? 0xFF222222 : 0xFF111111);
        //Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, 0xFF111111);
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        Minecraft.getMinecraft().getMain_2().drawStringWithShadow(this.op.getLabel(), (parent.parent.getX() + 10 + 4) * 2 + 5, (parent.parent.getY() + offset + 2) * 2 + 4, -1);
        GL11.glPopMatrix();
        //Gui.drawRect(parent.parent.getX(), parent.parent.getY(), parent.parent.getX() + parent.parent.getBarHeight(), parent.parent.getY() - parent.parent.getWidth(), Color.gray.getRGB());
        if (this.op.isToggled()) {
            Gui.drawRect(parent.parent.getX() + 4 + 4, parent.parent.getY() + offset + 4, parent.parent.getX() + 8 + 4, parent.parent.getY() + offset + 8, Color.cyan.getRGB());
        } else {
            Gui.drawRect(parent.parent.getX() + 4 + 4, parent.parent.getY() + offset + 4, parent.parent.getX() + 8 + 4, parent.parent.getY() + offset + 8, Color.white.getRGB());
        }
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnButton(mouseX, mouseY);
        this.y = parent.parent.getY() + offset;
        this.x = parent.parent.getX();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.open) {
            this.op.setToggled(!this.op.isToggled());
            client.getConfigManager().saveConfig("latest");
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12;
    }
}
