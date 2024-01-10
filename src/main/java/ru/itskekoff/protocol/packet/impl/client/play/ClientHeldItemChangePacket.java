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
public class ClientHeldItemChangePacket extends Packet {
    private int slotId;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeShort(this.slotId);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.slotId = in.readShort();
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x1A);
    }
}