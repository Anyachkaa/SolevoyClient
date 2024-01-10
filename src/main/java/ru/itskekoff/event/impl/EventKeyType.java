package ru.itskekoff.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.itskekoff.event.Event;

@AllArgsConstructor
public class EventKeyType extends Event {
    private @Getter @Setter int key;
}
