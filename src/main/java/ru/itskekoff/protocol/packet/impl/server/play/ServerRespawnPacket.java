package ru.itskekoff.protocol.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.data.Difficulty;
import ru.itskekoff.protocol.data.Dimension;
import ru.itskekoff.protocol.data.Gamemode;
import ru.itskekoff.protocol.packet.Packet;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ServerRespawnPacket extends Packet {
    private Dimension dimension;
    private Difficulty difficulty;
    private Gamemode gamemode;
    private String level_type;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeInt(this.dimension.getId());
        out.writeByte(this.difficulty.getId());
        out.writeByte(this.gamemode.getId());
        out.writeString(this.level_type);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.dimension = Dimension.getById(in.readInt());
        this.difficulty = Difficulty.getById(in.readUnsignedByte());
        this.gamemode = Gamemode.getById(in.readUnsignedByte());
        this.level_type = in.readString(128);
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x35);
    }
}