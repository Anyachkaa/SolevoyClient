package ru.itskekoff.protocol.data;

import ru.itskekoff.protocol.packet.Packet;
import ru.itskekoff.protocol.packet.PacketDirection;

import java.util.ArrayList;
import java.util.List;

public enum ConnectionState {
    HANDSHAKE, LOGIN, PLAY, STATUS;

    private final List<Packet> clientPackets, serverPackets;

    ConnectionState() {
        this.clientPackets = new ArrayList<>();
        this.serverPackets = new ArrayList<>();
    }

    public List<Packet> getPacketsByDirection(PacketDirection direction) {
        switch (direction) {
            case SERVERBOUND:
                return clientPackets;
            case CLIENTBOUND:
                return serverPackets;
        }
        return null;
    }
}