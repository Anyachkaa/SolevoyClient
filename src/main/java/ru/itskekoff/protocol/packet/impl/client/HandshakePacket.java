package ru.itskekoff.protocol.packet.impl.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.packet.Packet;

import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HandshakePacket extends Packet {
    private int protocolId;
    private String host;
    private int port;
    private int nextState;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeVarInt(this.protocolId);
        out.writeString(this.host);
        out.writeShort(this.port);
        out.writeVarInt(this.nextState);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.protocolId = in.readVarInt();
        this.host = in.readString(128);
        this.port = in.readShort();
        this.nextState = in.readVarInt();
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x00, 0);
    }
}