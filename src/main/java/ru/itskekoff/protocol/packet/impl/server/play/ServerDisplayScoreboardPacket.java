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

public class ServerDisplayScoreboardPacket extends Packet {
    private int position;
    private String scoreName;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeByte(this.position);
        out.writeString(this.scoreName);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.position = in.readByte();
        this.scoreName = in.readString(32767);
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x3B);
    }
}