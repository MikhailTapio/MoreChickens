package cn.evolvefield.mods.morechickens.common.container.base;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public class ItemListInventory implements IInventory, ISidedInventory {
    protected NonNullList<ItemStack> items;
    private final Runnable onMarkDirty;
    private final Function<PlayerEntity, Boolean> onIsUsableByPlayer;

    public ItemListInventory(NonNullList<ItemStack> items, Runnable onMarkDirty, Function<PlayerEntity, Boolean> onIsUsableByPlayer) {
        this.items = items;
        this.onMarkDirty = onMarkDirty;
        this.onIsUsableByPlayer = onIsUsableByPlayer;
    }

    public ItemListInventory(NonNullList<ItemStack> items, Runnable onMarkDirty) {
        this(items, onMarkDirty, null);
    }

    public ItemListInventory(NonNullList<ItemStack> items) {
        this(items, (Runnable)null);
    }

    public int getContainerSize() {
        return this.items.size();
    }

    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    @Nonnull
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Nonnull
    public ItemStack removeItem(int index, int count) {
        final ItemStack itemstack = ItemStackHelper.removeItem(this.items, index, count);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }

        return itemstack;
    }

    @Nonnull
    public ItemStack removeItemNoUpdate(int index) {
        return ItemStackHelper.takeItem(this.items, index);
    }

    public void setItem(int index, @Nonnull ItemStack stack) {
        this.items.set(index, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    public void setChanged() {
        if (this.onMarkDirty != null) {
            this.onMarkDirty.run();
        }

    }

    public boolean stillValid(@Nonnull PlayerEntity player) {
        return this.onIsUsableByPlayer != null ? (Boolean)this.onIsUsableByPlayer.apply(player) : true;
    }

    public void clearContent() {
        this.items.clear();
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull Direction direction) {
        final int count = getContainerSize();
        int[] itemSlots = new int[count];
        for (int i = 0; i < count; i++) {
            itemSlots[i] = i;
        }
        return itemSlots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @Nonnull ItemStack itemStack, @Nullable Direction direction) {
        return canPlaceItem(index, itemStack);    }

    @Override
    public boolean canTakeItemThroughFace(int index, @Nonnull ItemStack itemStack, @Nonnull Direction direction) {
        return index >= 0;
    }
}
