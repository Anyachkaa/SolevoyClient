package ru.itskekoff.client.clickgui.settings.impl;

import lombok.Data;
import ru.itskekoff.client.clickgui.settings.Setting;

public @Data class SliderSetting extends Setting {
    private float current, minimum, maximum;
    private float increment;

    public SliderSetting(String name, String label, float current, float minimum, float maximum, float increment) {
        this.name = name;
        this.label = label;
        this.current = current;
        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = increment;
    }

}
