package cn.evolvefield.mods.morechickens.common.container.base;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class LockedSlot extends Slot {
    protected final boolean inputLocked;
    protected final boolean outputLocked;

    public LockedSlot(IInventory inventoryIn, int index, int xPosition, int yPosition, boolean inputLocked, boolean outputLocked) {
        super(inventoryIn, index, xPosition, yPosition);
        this.inputLocked = inputLocked;
        this.outputLocked = outputLocked;
    }

    public LockedSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        this(inventoryIn, index, xPosition, yPosition, false, true);
    }

    public boolean mayPickup(@Nonnull PlayerEntity playerIn) {
        return !this.outputLocked && super.mayPickup(playerIn);
    }

    public boolean mayPlace(@Nonnull ItemStack stack) {
        return !this.inputLocked && super.mayPlace(stack);
    }
}
