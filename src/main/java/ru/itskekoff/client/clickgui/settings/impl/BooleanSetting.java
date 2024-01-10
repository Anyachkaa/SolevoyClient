package ru.itskekoff.client.clickgui.settings.impl;

import lombok.Data;
import ru.itskekoff.client.clickgui.settings.Setting;

public @Data class BooleanSetting extends Setting {
    private boolean toggled;

    public BooleanSetting(String name, String label, boolean value) {
        this.name = name;
        this.label = label;
        this.toggled = value;
        addSettings(this);
    }
}
