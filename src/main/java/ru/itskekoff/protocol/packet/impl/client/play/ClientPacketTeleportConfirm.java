package ru.itskekoff.protocol.packet.impl.client.play;

import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.packet.Packet;

import java.util.Arrays;
import java.util.List;

public class ClientPacketTeleportConfirm extends Packet {
    private int teleportID;

    public ClientPacketTeleportConfirm() {

    }

    public ClientPacketTeleportConfirm(int teleportID) {
        this.teleportID = teleportID;
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        teleportID = in.readVarInt();
    }

    @Override
    public void write(PacketBuffer out, int protocol) {
        out.writeVarInt(teleportID);
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x00);
    }
}
