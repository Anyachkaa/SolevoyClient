package ru.itskekoff.protocol.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.packet.Packet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class ClientCloseWindowPacket extends Packet {
    private int windowId;

    @Override
    public void write(PacketBuffer out, int protocol) throws IOException {
        out.writeByte(this.windowId);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws IOException {
        this.windowId = in.readByte();
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x08);
    }
}