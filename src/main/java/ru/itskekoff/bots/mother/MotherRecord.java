package ru.itskekoff.bots.mother;

import lombok.Data;
import ru.itskekoff.protocol.data.Position;
import ru.itskekoff.protocol.packet.Packet;

import java.util.List;

@Data
public class MotherRecord {
    private final Position recordPosition;
    private final List<Packet> recordPackets;
}
