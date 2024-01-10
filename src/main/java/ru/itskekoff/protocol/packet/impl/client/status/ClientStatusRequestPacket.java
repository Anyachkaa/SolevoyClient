package ru.itskekoff.protocol.packet.impl.client.status;

import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.packet.Packet;

import java.util.Collections;
import java.util.List;

public class ClientStatusRequestPacket extends Packet {
    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x00);
    }
}