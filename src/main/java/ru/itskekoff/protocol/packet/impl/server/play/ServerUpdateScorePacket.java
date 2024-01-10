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
public class ServerUpdateScorePacket extends Packet {
    private String scoreName;
    private int action;
    private String objectiveName;
    private int value;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeString(this.scoreName);
        out.writeByte(this.action);
        out.writeString(this.objectiveName);
        if (action != 1) {
            out.writeVarInt(this.value);
        }
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.scoreName = in.readString(128);
        this.action = in.readByte();
        this.objectiveName = in.readString(32767);
        if (action != 1) {
            this.value = in.readVarInt();
        }
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x45);
    }
}