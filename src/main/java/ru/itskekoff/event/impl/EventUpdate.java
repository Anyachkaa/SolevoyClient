package ru.itskekoff.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.itskekoff.event.Event;

@AllArgsConstructor
public class EventUpdate extends Event {
    private @Getter @Setter double x, y, z, motionX, motionY, motionZ;
    private @Getter @Setter float yaw, pitch;
}
