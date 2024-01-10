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
public class ServerTimeUpdatePacket extends Packet {
    private long worldAge;
    private long dayTime;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeLong(this.worldAge);
        out.writeLong(this.dayTime);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.worldAge = in.readLong();
        this.dayTime = in.readLong();
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x47);
    }
}