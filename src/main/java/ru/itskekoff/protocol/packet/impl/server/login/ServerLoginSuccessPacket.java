package ru.itskekoff.protocol.packet.impl.server.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.packet.Packet;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServerLoginSuccessPacket extends Packet {
    private UUID uuid;
    private String username;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeString(this.uuid == null ? "" : this.uuid.toString());
        out.writeString(this.username);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.uuid = UUID.fromString(in.readString());
        this.username = in.readString();
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x02);
    }
}