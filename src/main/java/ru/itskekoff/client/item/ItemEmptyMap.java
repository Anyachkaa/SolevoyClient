package ru.itskekoff.client.item;

import com.github.steveice10.mc.protocol.data.game.world.map.MapData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import ru.itskekoff.protocol.packet.impl.server.play.ServerMapDataPacket;
import ru.itskekoff.utils.BasicColor;

import javax.annotation.Nullable;

public class ItemEmptyMap extends ItemMapBase {
    public MapData mapdata;

    public ItemEmptyMap(ServerMapDataPacket packet) {
        this.mapdata = packet.getData();
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = ItemMap.setupNewMap(worldIn, playerIn.posX, playerIn.posZ, (byte) 0, true, false);
        ItemStack itemstack1 = playerIn.getHeldItem(handIn);
        itemstack1.shrink(1);

        if (itemstack1.isEmpty()) {
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        } else {
            if (!playerIn.inventory.addItemStackToInventory(itemstack.copy())) {
                playerIn.dropItem(itemstack, false);
            }

            playerIn.addStat(StatList.getObjectUseStats(this));
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack1);
        }
    }
}
