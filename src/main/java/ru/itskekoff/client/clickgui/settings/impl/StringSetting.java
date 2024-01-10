package ru.itskekoff.client.clickgui.settings.impl;

import lombok.Data;
import ru.itskekoff.client.clickgui.settings.Setting;

public @Data class StringSetting extends Setting {
    private String currentText;
    private int keyLen;
    public StringSetting(String name, String label, String currentText, int keyLen) {
        this.name = name;
        this.label = label;
        this.currentText = currentText;
        this.keyLen = keyLen;
    }
}
