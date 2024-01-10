package ru.itskekoff.client.module.impl.render;

import lombok.Getter;
import ru.itskekoff.client.module.Category;
import ru.itskekoff.client.module.Module;

public class HUD extends Module {
    private static @Getter boolean enabled = false;

    public HUD() {
        super("HUD", "Информация на экране.", Category.Render);
    }

    @Override
    public void onEnable() {
        enabled = true;
    }

    @Override
    public void onDisable() {
        enabled = false;
    }
}
