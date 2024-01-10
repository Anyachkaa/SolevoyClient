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
@NoArgsConstructor
@AllArgsConstructor
public class ServerPlayerAbilitiesPacket extends Packet {
    private boolean damage, flying, allowFlying, creative;
    private float flySpeed, walkSpeed;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        byte flags = 0;
        if (this.damage) {
            flags = (byte) (flags | 0x1);
        }
        if (this.flying) {
            flags = (byte) (flags | 0x2);
        }
        if (this.allowFlying) {
            flags = (byte) (flags | 0x4);
        }
        if (this.creative) {
            flags = (byte) (flags | 0x8);
        }
        out.writeByte(flags);
        out.writeFloat(this.flySpeed);
        out.writeFloat(this.walkSpeed);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        final byte flags = in.readByte();
        this.damage = ((flags & 0x1) > 0);
        this.flying = ((flags & 0x2) > 0);
        this.allowFlying = ((flags & 0x4) > 0);
        this.creative = ((flags & 0x8) > 0);
        this.flySpeed = in.readFloat();
        this.walkSpeed = in.readFloat();
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x2C);
    }
}