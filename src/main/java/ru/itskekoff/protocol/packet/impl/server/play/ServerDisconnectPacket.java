package ru.itskekoff.protocol.packet.impl.server.play;

import com.github.steveice10.mc.protocol.data.message.Message;
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
@NoArgsConstructor
public class ServerDisconnectPacket extends Packet {
    private BaseComponent[] reason;
    private Message message;

    public ServerDisconnectPacket(String message) {
        this(new ComponentBuilder(message).create());
    }

    public ServerDisconnectPacket(BaseComponent[] reason) {
        this.reason = reason;
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeString(ComponentSerializer.toString(reason));
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        String str;
        this.reason = ComponentSerializer.parse(str = in.readString());
        this.message = Message.fromString(str);
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x1A);
    }
}