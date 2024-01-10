package ru.itskekoff.protocol.packet.impl.server.play;

import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.util.NetUtil;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetInput;
import ru.itskekoff.protocol.Protocol;
import ru.itskekoff.protocol.codec.PacketBuffer;
import ru.itskekoff.protocol.packet.Packet;

import java.util.Arrays;
import java.util.List;

public class ServerPacketBlockChange extends Packet {
    private BlockChangeRecord record;
    private byte[] customData;

    public ServerPacketBlockChange() {
    }

    @Override
    public void read(PacketBuffer in, int protocol) {
        NetInput input = new ByteBufNetInput(in);

        try {
            this.record = new BlockChangeRecord(NetUtil.readPosition(input), NetUtil.readBlockState(input));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeBytes(customData);
    }

    public BlockChangeRecord getRecord() {
        return this.record;
    }

    @Override
    public Protocol getProtocol() {
        return new Protocol(0x0B);
    }
}
