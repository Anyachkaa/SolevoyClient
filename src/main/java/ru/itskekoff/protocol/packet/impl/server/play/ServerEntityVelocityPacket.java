package ru.itskekoff.protocol.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.packet.Packet;

@AllArgsConstructor
@NoArgsConstructor
public @Data class ServerEntityVelocityPacket extends Packet {
    private int entityID;
    private int motionX;
    private int motionY;
    private int motionZ;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeVarInt(this.entityID);
        out.writeShort(this.motionX);
        out.writeShort(this.motionY);
        out.writeShort(this.motionZ);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.entityID = in.readVarInt();
        this.motionX = in.readShort();
        this.motionY = in.readShort();
        this.motionZ = in.readShort();
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x3E);
    }
}
