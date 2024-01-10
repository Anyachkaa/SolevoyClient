package ru.itskekoff.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.entity.EntityPlayerSP;
import ru.itskekoff.event.Event;

@AllArgsConstructor
public class EventSendMessage extends Event {
    private @Getter String message;
    private @Getter EntityPlayerSP player;
}
