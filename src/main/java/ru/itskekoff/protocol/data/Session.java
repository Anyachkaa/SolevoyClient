package ru.itskekoff.protocol.data;

import io.netty.channel.Channel;
import lombok.Data;
import ru.itskekoff.protocol.codec.CompressionCodec;
import ru.itskekoff.protocol.codec.PacketCodec;
import ru.itskekoff.protocol.ProtocolType;
import ru.itskekoff.protocol.packet.Packet;

@Data
public class Session {
    private final Channel channel;
    private String username;
    private boolean disconnectedManually;

    public void sendPackets(Packet... packets) {
        if (this.isChannelOpen()) {
            for (Packet p : packets) {
                this.channel.write(p);
            }

            this.channel.flush();
        }
    }

    public void sendPacket(Packet p) {
        if (this.isChannelOpen()) {
            this.channel.writeAndFlush(p);
        }
    }

    public boolean isChannelOpen() {
        return this.channel != null && this.channel.isOpen();
    }

    public int getProtocolID() {
        if (getPacketCodec() == null) {
            return ProtocolType.PROTOCOL_UNKNOWN.getProtocol();
        }

        return getPacketCodec().getProtocol();
    }

    public void setProtocolID(int protocol) {
        getPacketCodec().setProtocol(protocol);
    }

    public ConnectionState getConnectionState() {
        return getPacketCodec().getConnectionState();
    }

    public void setConnectionState(ConnectionState state) {
        getPacketCodec().setConnectionState(state);
    }

    public PacketCodec getPacketCodec() {
        return ((PacketCodec) channel.pipeline().get("packetCodec"));
    }

    public void setCompressionThreshold(final int threshold) {
        if (getConnectionState() == ConnectionState.LOGIN) {
            if (channel.pipeline().get("compression") == null) {
                channel.pipeline().addBefore("packetCodec", "compression", new CompressionCodec(threshold));
            } else {
                ((CompressionCodec) channel.pipeline().get("compression")).setCompressionThreshold(threshold);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }
}