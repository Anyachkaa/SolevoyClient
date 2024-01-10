package ru.itskekoff.client.clickgui.settings.impl;

import lombok.Data;
import ru.itskekoff.client.clickgui.settings.Setting;

import java.util.Arrays;
import java.util.List;

public @Data class ListSetting extends Setting {
    private String name;
    private String currentMode;
    private int index;
    public final List<String> modes;

    public ListSetting(String name, String label, String currentMode, String... options) {
        this.name = name;
        this.label = label;
        this.modes = Arrays.asList(options);
        this.index = modes.indexOf(currentMode);
        this.currentMode = modes.get(index);
        addSettings(this);
    }

    public void setIndex(int index) {
        this.index = index;
        this.currentMode = modes.get(index);
    }

    public List<String> getModes() {
        return modes;
    }

    public String getOptions() {
        return modes.get(this.index);
    }
}
