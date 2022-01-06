package cn.evolvefield.mods.morechickens.common.tile.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class InventoryHandlerHelper {

    public static final int INPUT_SLOT = 1;
    public static final int[] OUTPUT_SLOTS = new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10};

    private static int getAvailableOutputSlot(IItemHandler handler, ItemStack insertStack) {
        return getAvailableOutputSlot(handler, insertStack, new ArrayList<>());
    }

    private static int getAvailableOutputSlot(IItemHandler handler, ItemStack insertStack, List<Integer> blacklistedSlots) {
        int emptySlot = 0;
        for (int slot : OUTPUT_SLOTS) {
            if (blacklistedSlots.contains(slot)) {
                continue;
            }
            final ItemStack stack = handler.getStackInSlot(slot);
            if (stack.isEmpty() && emptySlot == 0) {
                emptySlot = slot;
            } else if (stack.getItem().equals(insertStack.getItem()) && (stack.getCount() + insertStack.getCount()) <= stack.getMaxStackSize()) {
                // Check tags
//                if (WoodChip.getBlock(insertStack) != null) {
//                    Block block = WoodChip.getBlock(stack);
//                    if (block != null && block.equals(WoodChip.getBlock(insertStack))) {
//                        return slot;
//                    }
//                } else if (!Gene.getAttributeName(insertStack).isEmpty()) {
//                    if (
//                            Gene.getAttributeName(stack).equals(Gene.getAttributeName(insertStack)) &&
//                                    Gene.getValue(stack).equals(Gene.getValue(insertStack)) &&
//                                    Gene.getPurity(stack).equals(Gene.getPurity(insertStack))
//                    ) {
//                        return slot;
//                    }
//                } else
                if (stack.isEmpty() || areItemsAndTagsEqual(stack, insertStack)) {
                    return slot;
                }
            }
        }
        return emptySlot;
    }

    public static boolean areItemsAndTagsEqual(ItemStack stack1, ItemStack stack2) {
        return (
                stack1.isEmpty() && stack2.isEmpty()
        ) ||
                (
                        stack1.getItem() == stack2.getItem() && areItemStackTagsEqual(stack1, stack2)
                );
    }

    public static boolean areItemStackTagsEqual(ItemStack stackA, ItemStack stackB) {
        if (!stackA.isEmpty() && !stackB.isEmpty()) {
            final CompoundNBT tagA = stackA.getTag();
            final CompoundNBT tagB = stackB.getTag();
            if ((tagA == null || tagA.isEmpty()) && tagB != null && !tagB.isEmpty()) {
                return false;
            } else {
                return (tagA == null || tagA.isEmpty() || tagA.equals(stackB.getTag())) && stackA.areCapsCompatible(stackB);
            }
        } else {
            return false;
        }
    }

    public static class ItemHandler extends ItemStackHandler
    {
        protected TileEntity tileEntity;

        public ItemHandler(int size) {
            this(size, null);
        }

        public ItemHandler(int size, @Nullable TileEntity tileEntity) {
            super(size);
            this.tileEntity = tileEntity;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (tileEntity != null) {
                tileEntity.setChanged();
            }
        }

        public boolean isInputSlot(int slot) {
            return  slot == INPUT_SLOT;
        }

        public boolean isInsertableSlot(int slot) {
            return  slot != INPUT_SLOT ;
        }



        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return isItemValid(slot, stack, true);
        }

        public boolean isItemValid(int slot, @Nonnull ItemStack stack, boolean fromAutomation) {
            // Always allow an input item into an input slot
            return !fromAutomation && isInsertableSlot(slot);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return extractItem(slot, amount, simulate, true);
        }

        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate, boolean fromAutomation) {
            // Do not extract from input slots
            if (fromAutomation && isInputSlot(slot)) {
                return ItemStack.EMPTY;
            }
            return super.extractItem(slot, amount, simulate);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return insertItem(slot, stack, simulate, true);
        }

        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate, boolean fromAutomation) {
            return super.insertItem(slot, stack, simulate);
        }

        public boolean addOutput(@Nonnull ItemStack stack) {
            final int slot = getAvailableOutputSlot(this, stack);
            if (slot > 0) {
                ItemStack existingStack = this.getStackInSlot(slot);
                if (existingStack.isEmpty()) {
                    setStackInSlot(slot, stack.copy());
                } else {
                    existingStack.grow(stack.getCount());
                }
                onContentsChanged(slot);
                return true;
            }
            return false;
        }

        public boolean canFitStacks(List<ItemStack> stacks) {
            final List<Integer> usedSlots = new ArrayList<>();
            for (ItemStack stack : stacks) {
                final int slot = getAvailableOutputSlot(this, stack, usedSlots);
                if (slot == 0) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            final int size = nbt.contains("Size", Constants.NBT.TAG_INT) ? nbt.getInt("Size") : stacks.size();
            if (size < stacks.size()) {
                nbt.putInt("Size", stacks.size());
            }
            super.deserializeNBT(nbt);
        }
    }

    /////
    public static class UpgradeHandler extends ItemHandler
    {

        public UpgradeHandler(int size, TileEntity tileEntity) {
            super(size, tileEntity);
        }

        @Override
        public boolean isInsertableSlot(int slot) {
            return false;
        }


    }


}
