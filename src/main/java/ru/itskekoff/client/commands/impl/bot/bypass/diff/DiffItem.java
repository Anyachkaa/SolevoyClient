package ru.itskekoff.client.commands.impl.bot.bypass.diff;

import lombok.Data;
import net.minecraft.inventory.Slot;
import ru.itskekoff.bots.Bot;
import ru.itskekoff.bots.inventory.InventoryContainer;
import ru.itskekoff.protocol.data.ItemStack;

public @Data class DiffItem {
    private ItemStack stack;
    private short slotNum;
    private int count;

    public DiffItem(ItemStack stack, short slotNum) {
        this.stack = stack;
        this.slotNum = slotNum;
    }

    public static DiffItem fromSlot(Bot bot, ItemStack slot) {
        for (short i = 0; i < bot.getInventory().getItems().size(); i++) {
            if (bot.getInventory().getItems().get(i) == slot) {
                return new DiffItem(slot, i);
            }
        }
        return null;
    }

    public void addCount() {
        count++;
    }
}
