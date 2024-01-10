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
public class ServerKeepAlivePacket extends Packet {
    private long keepaliveId;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        if (protocol >= 340) {
            out.writeLong(this.keepaliveId);
        } else {
            out.writeVarInt((int) this.keepaliveId);
        }
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        if (protocol >= 340) {
            this.keepaliveId = in.readLong();
        } else {
            this.keepaliveId = in.readVarInt();
        }
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x1F);
    }
}