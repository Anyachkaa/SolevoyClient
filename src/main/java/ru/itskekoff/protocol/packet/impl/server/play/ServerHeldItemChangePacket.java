package ru.itskekoff.protocol.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.packet.Packet;

import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerHeldItemChangePacket extends Packet {
    private int slot;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeByte(this.slot);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.slot = in.readByte();
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x3A);
    }
}