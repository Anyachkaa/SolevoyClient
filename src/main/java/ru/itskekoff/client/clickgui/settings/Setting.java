package ru.itskekoff.client.clickgui.settings;

import lombok.Data;
import lombok.Getter;
import ru.itskekoff.client.clickgui.Configurable;
import ru.itskekoff.client.module.Module;

import java.util.List;
import java.util.function.Supplier;

public @Data class Setting extends Configurable {
    protected String name;
    protected String label;
}