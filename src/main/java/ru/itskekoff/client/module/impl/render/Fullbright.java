package ru.itskekoff.client.module.impl.render;

import ru.itskekoff.client.clickgui.settings.impl.*;
import ru.itskekoff.client.module.Category;
import ru.itskekoff.client.module.Module;
import ru.itskekoff.event.EventTarget;
import ru.itskekoff.event.impl.EventGameTick;

public class Fullbright extends Module {

    private SliderSetting gammaValue;

    public Fullbright(){
        super("Fullbright", "Освещает все окрестности", Category.Render);
        gammaValue = new SliderSetting("GammaValue", "Light level", 100, 1, 100, 1);
        addSettings(gammaValue);
    }
    @Override
    public void onEnable(){
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventGameTick event) {
        if (isToggled()) {
            mc.gameSettings.gammaSetting = gammaValue.getCurrent();
        }
    }

    @Override
    public void onDisable(){
        super.onDisable();
        mc.gameSettings.gammaSetting = 1.0f;
    }
}
