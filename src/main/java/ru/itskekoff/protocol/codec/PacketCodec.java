package ru.itskekoff.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.Getter;
import lombok.Setter;
import ru.itskekoff.client.SolevoyClient;
import ru.itskekoff.protocol.data.ConnectionState;
import ru.itskekoff.protocol.packet.Packet;
import ru.itskekoff.protocol.packet.PacketDirection;

import java.util.List;

@Getter
@Setter
public class PacketCodec extends ByteToMessageCodec<Packet> {
    private final PacketDirection packetDirection;
    private ConnectionState connectionState;
    private int protocol;
    private boolean via;

    public PacketCodec(ConnectionState connectionState, PacketDirection packetDirection, boolean viaMcp) {
        this.connectionState = connectionState;
        this.packetDirection = packetDirection;
        this.via = viaMcp;

    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) {
        if (!byteBuf.isWritable()) return;

        PacketBuffer packetbuffer = new PacketBuffer(byteBuf);

        packetbuffer.writeVarInt(getPacketIDByProtocol(packet, protocol));
        try {
            packet.write(packetbuffer, protocol);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if (byteBuf.readableBytes() != 0) {
            PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
            int packetID = packetBuffer.readVarInt();
            Packet packet = SolevoyClient.getInstance().getPacketRegistry().createPacket(connectionState, packetDirection, packetID, protocol);


            try {
                packet.read(packetBuffer, protocol);
            } catch (Throwable ignored) {
            }

            list.add(packet);
            packetBuffer.clear();
        }
    }

    private int getPacketIDByProtocol(Packet packet, int protocol) {
        for (int protocol2 : packet.getProtocol().getProtocols()) {
            if (protocol2 == protocol) {
                return packet.getProtocol().getId();
            }
        }
        return packet.getProtocol().getProtocols()[0];
    }
}