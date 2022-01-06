package cn.evolvefield.mods.morechickens.common.container.base;


import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import javax.annotation.Nonnull;

public class SeedsSlot extends Slot {
    public SeedsSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }


    @Override //isItemValid
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return isSeed(stack);
    }

    public static boolean isSeed(ItemStack stack) {
        final Item item = stack.getItem();
        return (item == Items.WHEAT_SEEDS || item == Items.MELON_SEEDS || item == Items.PUMPKIN_SEEDS || item == Items.BEETROOT_SEEDS);
    }
}
