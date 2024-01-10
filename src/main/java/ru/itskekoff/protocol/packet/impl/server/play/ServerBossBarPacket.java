package ru.itskekoff.protocol.packet.impl.server.play;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;
import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.packet.Packet;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ServerBossBarPacket extends Packet {
    private UUID uuid;
    private int action;
    private BaseComponent[] title;
    private float health;
    private int color;
    private int division;
    private byte flags;

    public ServerBossBarPacket(UUID uuid, int action) {
        this.uuid = uuid;
        this.action = action;
    }

    public ServerBossBarPacket(UUID uuid, int action, String title, float health, int color, int division, byte flags) {
        this.uuid = uuid;
        this.action = action;
        this.title = new ComponentBuilder(title).create();
        this.health = health;
        this.color = color;
        this.division = division;
        this.flags = flags;
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeUuid(uuid);
        out.writeVarInt(action);

        switch (action) {
            case 0 -> {
                out.writeString(ComponentSerializer.toString(title));
                out.writeFloat(health);
                out.writeVarInt(color);
                out.writeVarInt(division);
                out.writeByte(flags);
            }
            case 2 -> out.writeFloat(health);
            case 3 -> out.writeString(ComponentSerializer.toString(title));
            case 4 -> {
                out.writeVarInt(color);
                out.writeVarInt(division);
            }
            case 5 -> out.writeByte(flags);
        }
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        uuid = in.readUuid();
        action = in.readVarInt();

        switch (action) {
            case 0 -> {
                title = ComponentSerializer.parse(in.readString());
                health = in.readFloat();
                color = in.readVarInt();
                division = in.readVarInt();
                flags = in.readByte();
            }
            case 2 -> health = in.readFloat();
            case 3 -> title = ComponentSerializer.parse(in.readString());
            case 4 -> {
                color = in.readVarInt();
                division = in.readVarInt();
            }
            case 5 -> flags = in.readByte();
        }
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x0C);
    }
}