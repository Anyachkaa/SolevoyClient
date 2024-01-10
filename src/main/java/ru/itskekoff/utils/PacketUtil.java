package ru.itskekoff.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.data.Position;
import ru.itskekoff.protocol.data.Session;
import ru.itskekoff.protocol.ProtocolType;
import ru.itskekoff.protocol.packet.impl.server.play.ServerPlayerAbilitiesPacket;
import ru.itskekoff.protocol.packet.Packet;
import ru.itskekoff.protocol.packet.impl.CustomPacket;

import java.util.UUID;

import static ru.itskekoff.protocol.packet.Packet.Builder.DataType.*;

public class PacketUtil {
    private static final UUID bossBarUUID = UUID.randomUUID();
    private static final int bossBarID = 999;

    private static int spawnEntityID = 1;

    public static PacketBuffer createEmptyPacketBuffer() {
        return new PacketBuffer(Unpooled.buffer());
    }

    public static CustomPacket createCustomPacket(int id, Packet.Builder.CustomData... data) {
        return new Packet.Builder().init(data).build(id);
    }

    public static byte[] copyBuff(ByteBuf in) {
        ByteBuf incopy = in.duplicate();

        byte[] customData = new byte[incopy.readableBytes()];
        incopy.readBytes(customData);
        incopy.clear();

        return customData;
    }

    public static void fly(Session session, boolean fly) {
        session.sendPacket(new ServerPlayerAbilitiesPacket(false, fly, fly, fly, 0.1f, 0.1f));
    }

    public static void speed(Session session, float speed) {
        session.sendPacket(new ServerPlayerAbilitiesPacket(false, false, false, false, 1.0f, speed));
    }
    public static void spawnParticle(Session session, int particleID, boolean longDistance, Position pos, float offsetX, float offsetY, float offsetZ, float particleData, int particleCount) {
        Packet.Builder particlePacket = new Packet.Builder()
                .add(INT, particleID)
                .add(BOOLEAN, longDistance)
                .add(FLOAT, (float) pos.getX())
                .add(FLOAT, (float) pos.getY())
                .add(FLOAT, (float) pos.getZ())
                .add(FLOAT, offsetX)
                .add(FLOAT, offsetY)
                .add(FLOAT, offsetZ)
                .add(FLOAT, particleData)
                .add(INT, particleCount);

        session.sendPacket(particlePacket.build(0x22));
    }
}