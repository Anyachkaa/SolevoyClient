package ru.itskekoff.protocol.packet.impl.server.play;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.data.TitleAction;
import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.packet.Packet;

import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
public class ServerTitlePacket extends Packet {
    private TitleAction titleAction;
    private BaseComponent[] title;
    private BaseComponent[] subTitle;
    private BaseComponent[] actionBar;

    private int fadeIn;
    private int fadeOut;
    private int stay;

    public ServerTitlePacket(TitleAction action, String message) {
        this.titleAction = action;
        if (action == TitleAction.TITLE) {
            this.title = new ComponentBuilder(message).create();
        } else if (action == TitleAction.SUBTITLE) {
            this.subTitle = new ComponentBuilder(message).create();
        } else if (action == TitleAction.ACTIONBAR) {
            this.actionBar = new ComponentBuilder(message).create();
        } else {
            throw new IllegalArgumentException("Illegal use of ServerTitlePacket!");
        }
    }

    public ServerTitlePacket(TitleAction action, BaseComponent... components) {
        this.titleAction = action;
        if (action == TitleAction.TITLE) {
            this.title = components;
        } else if (action == TitleAction.SUBTITLE) {
            this.subTitle = components;
        } else if (action == TitleAction.ACTIONBAR) {
            this.actionBar = components;
        } else {
            throw new IllegalArgumentException("Illegal use of ServerTitlePacket!");
        }
    }


    public ServerTitlePacket(TitleAction action, int fadeIn, int stay, int fadeOut) {
        this.titleAction = action;
        if (titleAction == TitleAction.TIMES) {
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
        } else {
            throw new IllegalArgumentException("Illegal use of ServerTitlePacket");
        }
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeVarInt(titleAction.getIdByProtocol(protocol));
        if (titleAction == TitleAction.TITLE) {
            out.writeString(ComponentSerializer.toString(this.title));
        } else if (titleAction == TitleAction.SUBTITLE) {
            out.writeString(ComponentSerializer.toString(this.subTitle));
        } else if (titleAction == TitleAction.ACTIONBAR) {
            out.writeString(ComponentSerializer.toString(this.actionBar));
        }
        if (titleAction == TitleAction.TIMES) {
            out.writeInt(this.fadeIn);
            out.writeInt(this.stay);
            out.writeInt(this.fadeOut);
        }
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.titleAction = TitleAction.getById(in.readVarInt(), protocol);

        if (titleAction == TitleAction.TITLE) {
            this.title = ComponentSerializer.parse(in.readString());
        } else if (titleAction == TitleAction.SUBTITLE) {
            this.subTitle = ComponentSerializer.parse(in.readString());
        } else if (titleAction == TitleAction.ACTIONBAR) {
            this.actionBar = ComponentSerializer.parse(in.readString());
        } else if (titleAction == TitleAction.TIMES) {
            this.fadeIn = in.readInt();
            this.stay = in.readInt();
            this.fadeOut = in.readInt();
        }
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x48);
    }
}