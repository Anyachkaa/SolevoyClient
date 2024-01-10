package ru.itskekoff.client.clickgui.component.components.sub;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;

import ru.itskekoff.client.clickgui.component.Component;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import ru.itskekoff.client.clickgui.component.components.ModuleComponent;
import ru.itskekoff.client.clickgui.settings.impl.*;

public class SliderComponent extends Component {

    private boolean hovered;

    private SliderSetting set;
    private ModuleComponent parent;
    private int offset;
    private int x;
    private int y;
    private boolean dragging = false;

    private double renderWidth;

    public SliderComponent(SliderSetting value, ModuleComponent button, int offset) {
        this.set = value;
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
    }

    @Override
    public void renderComponent() {
        Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + parent.parent.getWidth(), parent.parent.getY() + offset + 12, 0xFF111111);
        final int drag = (int) (this.set.getCurrent() / this.set.getMaximum() * this.parent.parent.getWidth());
        Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX(), parent.parent.getY() + offset + 12, 0xFF111111);
        Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset + 8, parent.parent.getX() + 88, parent.parent.getY() + offset + 12, Color.GRAY.darker().darker().darker().getRGB());
        Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset + 8, parent.parent.getX() + (int) renderWidth, parent.parent.getY() + offset + 12, hovered ? 0xFF555555 : 0xFF444444);
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        Minecraft.getMinecraft().getMain_2().drawStringWithShadow(this.set.getLabel() + ": " + this.set.getCurrent(), (parent.parent.getX() * 2 + 15), (parent.parent.getY() + offset - 1) * 2 + 5, -1);

        GL11.glPopMatrix();
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnButtonD(mouseX, mouseY) || isMouseOnButtonI(mouseX, mouseY);
        this.y = parent.parent.getY() + offset;
        this.x = parent.parent.getX();

        double min = set.getMinimum();
        double max = set.getMaximum();

        renderWidth = (88) * (set.getCurrent() - min) / (max - min);

        if (dragging) {
            set.setCurrent((float) roundToPlace((double) (mouseX - x) * (max - min) / (double) parent.parent.getWidth() + min, (int) set.getIncrement()));
            if (set.getCurrent() > max) {
                set.setCurrent((float) max);
            } else if (set.getCurrent() < min) {
                set.setCurrent((float) min);
            }
            client.getConfigManager().saveConfig("latest");
        }
    }

    private static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButtonD(mouseX, mouseY) && button == 0 && this.parent.open) {
            dragging = true;
        }
        if (isMouseOnButtonI(mouseX, mouseY) && button == 0 && this.parent.open) {
            dragging = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        dragging = false;
    }

    public boolean isMouseOnButtonD(int x, int y) {
        return x > this.x && x < this.x + (parent.parent.getWidth() / 2 + 1) && y > this.y && y < this.y + 12;
    }

    public boolean isMouseOnButtonI(int x, int y) {
        return x > this.x + parent.parent.getWidth() / 2 && x < this.x + parent.parent.getWidth() && y > this.y && y < this.y + 12;
    }
}