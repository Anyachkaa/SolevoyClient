package ru.itskekoff.protocol.packet.impl.server.play;

import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.packet.Packet;

import java.util.Arrays;
import java.util.List;

public class ServerPacketPlayerHealth extends Packet {
    private float health, foodSat;
    private int food;

    public ServerPacketPlayerHealth() {
    }

    @Override
    public void read(PacketBuffer in, int protocol) {
        health = in.readFloat();
        food = in.readVarInt();
        foodSat = in.readFloat();
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeFloat(health);
        out.writeVarInt(food);
        out.writeFloat(foodSat);
    }

    public float getHealth() {
        return health;
    }

    public float getFoodSat() {
        return foodSat;
    }

    public int getFood() {
        return food;
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x41);
    }
}
