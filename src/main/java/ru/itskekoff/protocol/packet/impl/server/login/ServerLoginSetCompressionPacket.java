package ru.itskekoff.protocol.packet.impl.server.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.packet.Packet;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServerLoginSetCompressionPacket extends Packet {
    private int threshold;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeVarInt(this.threshold);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.threshold = in.readVarInt();
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x03);
    }
}