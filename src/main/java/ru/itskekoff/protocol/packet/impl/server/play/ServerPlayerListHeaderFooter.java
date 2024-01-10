package ru.itskekoff.protocol.packet.impl.server.play;

import lombok.AllArgsConstructor;
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

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServerPlayerListHeaderFooter extends Packet {
    private BaseComponent[] header, footer;

    public ServerPlayerListHeaderFooter(String header, String footer) {
        this(new ComponentBuilder(header).create(), new ComponentBuilder(footer).create());
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeString(ComponentSerializer.toString(this.header));
        out.writeString(ComponentSerializer.toString(this.footer));
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.header = ComponentSerializer.parse(in.readString());
        this.footer = ComponentSerializer.parse(in.readString());
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x4A);
    }
}