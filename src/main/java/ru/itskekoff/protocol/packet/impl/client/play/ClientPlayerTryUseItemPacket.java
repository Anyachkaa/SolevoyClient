package ru.itskekoff.protocol.packet.impl.client.play;

import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.packet.Packet;

import java.util.Collections;
import java.util.List;

public class ClientPlayerTryUseItemPacket extends Packet {
    private int hand;

    public ClientPlayerTryUseItemPacket() {

    }

    public ClientPlayerTryUseItemPacket(int hand) {
        this.hand = hand;
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeVarInt(hand);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        hand = in.readVarInt();
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x20);
    }
}
