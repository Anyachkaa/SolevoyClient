package ru.itskekoff.client.module.impl.render;

import ru.itskekoff.client.clickgui.settings.impl.*;
import ru.itskekoff.client.module.Category;
import ru.itskekoff.client.module.Module;

import org.lwjgl.input.Keyboard;

import java.util.List;

public class ClickGui extends Module {


    public static BooleanSetting background;
    public static ListSetting effect;

    public ClickGui(){
        super("ClickGui", "Клик гуи", Category.Render);
        background = new BooleanSetting("Background", "Background", true);
        effect = new ListSetting("Effect", "ClickGui effect", "Snow", "Snow");
        this.addSettings(background, effect);
        this.setKey(Keyboard.KEY_RSHIFT);
    }

    @Override
    public void onEnable(){
        super.onEnable();
        mc.displayGuiScreen(client.getClickGUI());
        this.setToggled(false);
    }
}

