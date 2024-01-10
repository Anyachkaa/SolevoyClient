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
public class ClientChatPacket extends Packet {
    private String message;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        if (protocol >= 110 && message.length() > 100) {
            message = message.substring(0, 100);
        }
        out.writeString(message);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.message = in.readString(32767);
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x02);
    }
}