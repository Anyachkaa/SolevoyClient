package ru.itskekoff.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.itskekoff.bots.Bot;
import ru.itskekoff.event.Event;
import ru.itskekoff.protocol.packet.impl.server.play.ServerMapDataPacket;

@AllArgsConstructor
public class EventCaptchaReceived extends Event {
    private @Getter @Setter ServerMapDataPacket packet;
    private @Getter @Setter Bot bot;
}
