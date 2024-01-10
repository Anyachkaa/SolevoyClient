package ru.itskekoff.protocol.packet.impl.client.play;

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
public class ClientStatusPacket extends Packet {
    private int actionId;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeVarInt(this.actionId);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.actionId = in.readVarInt();
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x03);
    }
}