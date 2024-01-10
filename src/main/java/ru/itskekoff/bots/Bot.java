package ru.itskekoff.bots;

import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import lombok.Getter;
import lombok.Setter;
import ru.itskekoff.bots.chunks.CachedChunk;
import ru.itskekoff.bots.inventory.InventoryContainer;
import ru.itskekoff.bots.macro.MacroRecord;
import ru.itskekoff.bots.mother.MotherRecord;
import ru.itskekoff.client.SolevoyClient;
import ru.itskekoff.client.commands.impl.bot.bypass.BotBypassController;
import ru.itskekoff.protocol.data.Position;
import ru.itskekoff.protocol.data.ServerData;
import ru.itskekoff.protocol.data.Session;
import ru.itskekoff.protocol.packet.Packet;
import ru.itskekoff.protocol.packet.impl.client.play.ClientChatPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class Bot {
    public final List<CachedChunk> ownChunks = new ArrayList<>();
    private final String name;
    public int captchaTries = 3;
    private Session session;
    private double x, y, z, lastX, lastY, lastZ;
    private float yaw = 0, pitch = 0, lastYaw, lastPitch;
    private double motionX = 0, motionY = 0, motionZ = 0;
    private long lastKeepAlive;
    private boolean onGround = false;
    private int motherIndex = 0;
    private boolean mother = false;
    private int macroIndex = 0;
    private int entityID;
    private boolean macro = false;
    private boolean macroComplete = false;
    private boolean registered = false;
    private boolean rejoined = false;
    private InventoryContainer openContainer;
    private InventoryContainer inventory = new InventoryContainer(0, new ArrayList<>(Collections.nCopies(46, null)), "inventory");
    private ServerData serverData;
    private BotBypassController bypassController;

    private SolevoyClient client = SolevoyClient.getInstance();

    public Bot(String name) {
        this.name = name;
    }

    public void onUpdate() {
        try {
            if (!mother && !macro) {
                motherIndex = 0;
                macroIndex = 0;
                macroComplete = false;
            }
            if (!mother && !macro && isAreaLoaded(getFloorX(), getFloorY(), getFloorZ())) {
                x += motionX;
                y += motionY;
                z += motionZ;
                this.botFall();
                motionY *= 0.98D;
            } else if (mother && SolevoyClient.getInstance().getMotherRecords().size() > motherIndex) {
                MotherRecord record = SolevoyClient.getInstance().getMotherRecords().get(motherIndex);
                Position pos = record.getRecordPosition();

                x = pos.getX();
                y = pos.getY();
                z = pos.getZ();
                yaw = pos.getYaw();
                pitch = pos.getPitch();

                for (Packet packet : record.getRecordPackets()) {
                    this.getSession().sendPacket(packet);
                }

                motherIndex++;
            } else if (macro && client.getMacroManager().getCurrentMacro().getRecords().size() > macroIndex) {
                MacroRecord record = SolevoyClient.getInstance().getMacroManager().getCurrentMacro().getRecords().get(macroIndex);

                x += record.getPosChange().getXChange();
                y += record.getPosChange().getYChange();
                z += record.getPosChange().getZChange();

                yaw = record.getPosChange().getYaw();
                pitch = record.getPosChange().getPitch();

                for (Packet packet : record.getPackets()) {
                    this.getSession().sendPacket(packet);
                }

                macroIndex++;
            } else if (macro && client.getMacroManager().getCurrentMacro().getRecords().size() <= macroIndex) {
                this.macroComplete = true;
            }
        } catch (Throwable ignored) {
        }
    }

    private void botFall() {
        if (y < 256 || y > 0) {
            BlockState state = this.getBlockAtPos(getFloorX(), (int) Math.floor(getY() - 0.0001D), getFloorZ());
            if (state != null && state.getId() != 0 && !isFlower(state)) {
                if (getY() - getFloorY() > 0.5D) {
                    y = getFloorY() + 1;
                }
                motionY = 0D;
                onGround = true;
                return;
            }
        }
        motionY -= 0.08D;
        onGround = false;
    }

    public void onDisconnect() {
        SolevoyClient.getInstance().getBots().remove(this);
    }

    public void jump() {
        if (onGround) motionY += 0.42F;
    }

    public void sendMessage(String message) {
        session.sendPacket(new ClientChatPacket(message));
    }
    public boolean isFlower(BlockState state) {
        return state.getId() == 31 || state.getId() == 38 || state.getId() == 37 || state.getId() == 175 || state.getId() == 6;
    }

    public boolean isAreaLoaded(int x, int y, int z) {
        if (y > 256 || y < 0) {
            return true;
        } else {
            Column chunk;
            if ((chunk = getChunkAtPos(x >> 4, z >> 4)) != null) {
                return chunk.getChunks().length >= (y >> 4);
            }
            return false;
        }
    }

    public BlockState getBlockAtPos(int x, int y, int z) {
        Column current = this.getChunkAtPos(x >> 4, z >> 4);
        Chunk[] chunks = current.getChunks();
        if (current.getChunks() != null && chunks.length > y >> 4 && chunks[y >> 4] != null && chunks[y >> 4].getBlocks() != null) {
            return chunks[y >> 4].getBlocks().get(x & 15, y & 15, z & 15);
        }
        return null;
    }

    public void setBlockAtPos(int x, int y, int z, BlockState state) {
        Chunk current = this.getChunkAtPos(x >> 4, z >> 4).getChunks()[y >> 4];
        current.getBlocks().set(x & 15, y & 15, z & 15, state);
    }

    public Column getChunkAtPos(int x, int z) {
        for (CachedChunk column : ownChunks) {
            if (column.getChunk().getX() == x && column.getChunk().getZ() == z) {
                return column.getChunk();
            }
        }

        return null;
    }

    public int getFloorX() {
        return (int) Math.floor(x);
    }

    public int getFloorY() {
        return (int) Math.floor(y);
    }

    public int getFloorZ() {
        return (int) Math.floor(z);
    }

}