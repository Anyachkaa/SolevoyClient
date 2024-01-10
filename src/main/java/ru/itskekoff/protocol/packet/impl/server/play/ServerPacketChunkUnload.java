package ru.itskekoff.protocol.packet.impl.server.play;

import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.packet.Packet;

import java.util.Arrays;
import java.util.List;

public class ServerPacketChunkUnload extends Packet {
    private int x, z;

    public ServerPacketChunkUnload() {
    }

    @Override
    public void read(PacketBuffer in, int protocol) {
        x = in.readInt();
        z = in.readInt();
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeInt(x);
        out.writeInt(z);
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x1D);
    }
}
