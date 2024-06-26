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
@AllArgsConstructor
@NoArgsConstructor
public class ServerPlayerPosLookPacket extends Packet {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private int flags;
    private int teleport;

    public ServerPlayerPosLookPacket(double x, double y, double z, float yaw, float pitch, int flags) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.flags = flags;
    }

    public ServerPlayerPosLookPacket(double x, double y, double z, float yaw, float pitch) {
        this(x, y, z, yaw, pitch, 0);
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeDouble(this.x);
        out.writeDouble(this.y);
        out.writeDouble(this.z);
        out.writeFloat(this.yaw);
        out.writeFloat(this.pitch);
        out.writeByte(this.flags);
        out.writeVarInt(this.teleport);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.z = in.readDouble();
        this.yaw = in.readFloat();
        this.pitch = in.readFloat();
        this.flags = in.readUnsignedByte();
        this.teleport = in.readVarInt();
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x2F);
    }
}