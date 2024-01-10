package ru.itskekoff.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.ScaledResolution;
import ru.itskekoff.event.Event;

@AllArgsConstructor
public class EventRender2D extends Event {
    private @Getter @Setter ScaledResolution sr;
    private @Getter @Setter float partialTicks;
}
