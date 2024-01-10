package ru.itskekoff.client.clickgui;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.util.Random;

@AllArgsConstructor
public @Data class Effect {
    private int x;
    private int y;
    private int fallingSpeed;
    private int size;

    public void update(ScaledResolution res) {
        Gui.drawRect(getX(), getY(), getX() + size, getY() + size, 0x99C9C5C5);
        setY(getY() + fallingSpeed);

        if (getY() > res.getScaledHeight() + 10 || getY() < -10) {
            setY(-10);
            Random rand = new Random();
            fallingSpeed = rand.nextInt(10) + 1;
            size = rand.nextInt(4) + 1;
        }
    }
}
