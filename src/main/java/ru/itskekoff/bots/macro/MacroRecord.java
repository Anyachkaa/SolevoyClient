package ru.itskekoff.bots.macro;

import lombok.Data;
import ru.itskekoff.protocol.packet.Packet;

import java.io.Serializable;
import java.util.List;

@Data
public class MacroRecord implements Serializable {
    private final List<Packet> packets;
    private PosChange posChange;

    @Data
    public static class PosChange implements Serializable {
        private final double xChange, yChange, zChange;
        private final float yaw, pitch;
    }
}
