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
@AllArgsConstructor
@NoArgsConstructor
public class ClientSettingsPacket extends Packet {
    private String locale;
    private byte viewDistance;
    private byte chatMode;
    private boolean chatColors;
    private byte skinParts;
    private int mainHand;

    public ClientSettingsPacket(String locale, byte viewDistance, byte chatMode, boolean chatColors, byte skinParts) {
        this.locale = locale;
        this.viewDistance = viewDistance;
        this.chatMode = chatMode;
        this.chatColors = chatColors;
        this.skinParts = skinParts;
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeString(locale);
        out.writeByte(viewDistance);
        if (protocol == 338) {
            out.writeVarInt(chatMode);
        } else {
            out.writeByte(chatMode);
        }
        out.writeBoolean(chatColors);
        out.writeByte(skinParts);
        if (protocol >= 110) {
            out.writeVarInt(mainHand);
        }
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.locale = in.readString(128);
        this.viewDistance = in.readByte();
        if (protocol == 338) {
            this.chatMode = (byte) in.readVarInt();
        } else {
            this.chatMode = in.readByte();
        }
        this.chatColors = in.readBoolean();
        this.skinParts = in.readByte();
        if (protocol >= 110) {
            this.mainHand = in.readVarInt();
        }
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x04);
    }
}