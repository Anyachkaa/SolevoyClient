package ru.itskekoff.protocol.packet.impl.server.login;

import com.github.steveice10.mc.protocol.data.message.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.util.text.ITextComponent;
import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.packet.Packet;

import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
public class ServerLoginDisconnectPacket extends Packet {
    private ITextComponent reason;
    private Message message;

    public ServerLoginDisconnectPacket(ITextComponent reason) {
        this.reason = reason;
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeString(ITextComponent.Serializer.componentToJson(this.reason));
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.reason = ITextComponent.Serializer.fromJsonLenient(in.readString(32767));
        this.message = Message.fromString(ITextComponent.Serializer.componentToJson(this.reason));
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x00);
    }
}