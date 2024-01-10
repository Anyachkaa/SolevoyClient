package ru.itskekoff.client.fix;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import ru.itskekoff.client.SolevoyClient;
import ru.itskekoff.event.EventTarget;
import ru.itskekoff.event.impl.EventGameTick;
import ru.itskekoff.event.types.StateType;
import ru.itskekoff.protocol.packet.impl.client.play.ClientPlayerActionPacket;

public class GhostBlockFix {
    private static int tick = 0;

    public GhostBlockFix() {
        SolevoyClient.getInstance().getTaskExecutor().submit(() -> {
            while (true) {
                execute();
            }
        });
    }

    public void execute() {
        tick++;
        if (tick >= 20) {
            /*
            Minecraft mc = Minecraft.getMinecraft();
            NetHandlerPlayClient conn = mc.getConnection();
            if (conn == null)
                return;
            BlockPos pos = mc.player.getPosition();
            for (int dx = -4; dx <= 4; dx++)
                for (int dy = -4; dy <= 4; dy++)
                    for (int dz = -4; dz <= 4; dz++) {
                        CPacketPlayerDigging packet = new CPacketPlayerDigging(
                                CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz),
                                EnumFacing.UP);
                        conn.sendPacket(packet);
                    }

             */
            if (tick >= 20) tick = 0;
        }
    }
}
