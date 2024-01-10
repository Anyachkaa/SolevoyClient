package ru.itskekoff.protocol.packet.impl.server.play;

import com.github.steveice10.mc.protocol.data.MagicValues;
import com.github.steveice10.mc.protocol.data.game.MessageType;
import com.github.steveice10.mc.protocol.data.message.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;
import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.data.MessagePosition;
import ru.itskekoff.protocol.packet.Packet;

@Getter
@NoArgsConstructor
public class ServerChatPacket extends Packet {
    private BaseComponent[] message;
    private MessagePosition position;

    private Message msg;
    private MessageType type;

    public ServerChatPacket(BaseComponent[] message, MessagePosition position) {
        this.message = message;
        this.position = position;
    }

    public ServerChatPacket(String message) {
        this(new ComponentBuilder(message).create(), MessagePosition.CHATBOX);
    }

    public ServerChatPacket(String message, MessagePosition position) {
        this(new ComponentBuilder(message).create(), position);
    }

    public ServerChatPacket(BaseComponent... text) {
        this(text, MessagePosition.CHATBOX);
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeString(ComponentSerializer.toString(message));
        out.writeByte(position.getId());
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        String str = in.readString();
        byte byt = in.readByte();

        this.message = ComponentSerializer.parse(str);
        this.position = MessagePosition.getById(byt);

        this.msg = Message.fromString(str);
        this.type = (MessageType) MagicValues.key(MessageType.class, byt);
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x0F);
    }
}