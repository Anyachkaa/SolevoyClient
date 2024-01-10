package ru.itskekoff.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.itskekoff.event.Event;
import ru.itskekoff.event.types.StateType;

@AllArgsConstructor
public class EventGameTick extends Event {
    private @Getter StateType type;
}
