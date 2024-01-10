package ru.itskekoff.client.clickgui.component.components.sub;

import lombok.Data;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;
import ru.itskekoff.client.clickgui.component.Component;
import ru.itskekoff.client.clickgui.component.components.ModuleComponent;
import ru.itskekoff.utils.ChatUtil;
import ru.itskekoff.client.module.Module;
public @Data class InfoComponent extends Component {

    private boolean hovered;
    private ModuleComponent parent;
    private int offset;
    private int x;
    private int y;
    private Module mod;

    public InfoComponent(ModuleComponent button, Module mod, int offset) {
        this.parent = button;
        this.mod = mod;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void renderComponent() {
        Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth()), parent.parent.getY() + offset + 12, this.hovered ? 0xFF222222 : 0xFF111111);
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        Minecraft.getMinecraft().getMain_2().drawStringWithShadow("Info", (parent.parent.getX() + 7) * 2, (parent.parent.getY() + offset + 2) * 2 + 5, -1);
        GL11.glPopMatrix();
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
            ChatUtil.sendChatMessage("&o" + this.mod.getDescription() + ".", true);
            client.getConfigManager().saveConfig("latest");
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12;
    }
}
