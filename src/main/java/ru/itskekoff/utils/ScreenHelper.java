package ru.itskekoff.utils;


import lombok.Data;
import net.minecraft.util.math.MathHelper;

public final @Data class ScreenHelper {
    private double x;
    private double y;

    public ScreenHelper(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public final void interpolate(double targetX, double targetY, double smoothing) {
        this.x = animate(targetX, this.x, smoothing);
        this.y = animate(targetY, this.y, smoothing);
    }

    public void animate(double newX, double newY) {
        this.x = animate(this.x, newX, 1.0);
        this.y = animate(this.y, newY, 1.0);
    }
    public static double animate(final double target, double current, double speed) {
        return MathHelper.lerp(current, target, speed);
    }
}