package ru.itskekoff.event;

import lombok.Getter;
import lombok.Setter;

public abstract class Event {
    private @Getter @Setter boolean cancelled;
}
