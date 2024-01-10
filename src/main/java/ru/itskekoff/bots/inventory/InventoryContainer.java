package ru.itskekoff.bots.inventory;

import net.minecraft.inventory.Slot;
import ru.itskekoff.bots.Bot;
import ru.itskekoff.protocol.data.ItemStack;
import ru.itskekoff.protocol.data.WindowAction;
import ru.itskekoff.protocol.packet.impl.client.play.ClientPlayerWindowActionPacket;

import java.util.List;

public class InventoryContainer {
    private final int windowID;
    private final List<ItemStack> items;

    private String name;
    private int transaction = 0;

    public InventoryContainer(int windowID, List<ItemStack> items, String name) {
        this.windowID = windowID;
        this.items = items;
        this.name = name;
    }


    public void slotClick(Bot bot, short slot, int button, WindowAction action) {
        bot.getSession().sendPacket(new ClientPlayerWindowActionPacket(windowID, slot, button, action, transaction++, ItemStack.AIR));
    }

    public List<ItemStack> getItems() {
        return items;
    }
    public int getWindowID() {
        return windowID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
