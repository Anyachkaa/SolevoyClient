package ru.itskekoff.protocol.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.data.Effect;
import ru.itskekoff.protocol.packet.Packet;

import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerChangeGameStatePacket extends Packet {
    private Effect effect;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeByte(effect.getEffectReason());
        out.writeFloat(effect.getValue());
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.effect = new Effect(in.readByte(), in.readFloat());
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x1E);
    }
}