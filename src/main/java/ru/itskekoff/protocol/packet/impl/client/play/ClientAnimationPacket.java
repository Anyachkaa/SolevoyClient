package ru.itskekoff.protocol.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.packet.Packet;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientAnimationPacket extends Packet {
    private EnumHand hand;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeEnumValue(this.hand);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.hand = (EnumHand)in.readEnumValue(EnumHand.class);
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x1D);
    }

    public enum EnumHand {
        MAIN_HAND,
        OFF_HAND;
    }
}
