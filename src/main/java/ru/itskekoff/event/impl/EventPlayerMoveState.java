package ru.itskekoff.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.itskekoff.event.Event;

@AllArgsConstructor
public class EventPlayerMoveState extends Event {
    private final @Getter float moveStrafe, moveForward;
}
