package cn.evolvefield.mods.morechickens.common.tile;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.container.CollectorContainer;
import cn.evolvefield.mods.morechickens.common.util.InventoryUtil;
import cn.evolvefield.mods.morechickens.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CollectorTileEntity extends TileEntity implements ISidedInventory, ITickableTileEntity, INamedContainerProvider, IIntArray {

    private final NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(getContainerSize(), ItemStack.EMPTY);
    private int searchOffset = 0;

    public CollectorTileEntity() {
        super(ModTileEntities.TILE_COLLECTOR);
    }


    public String getName() {
        return "container." + MoreChickens.MODID + ".collector";
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(getName());
    }

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
        return new CollectorContainer(id,playerInventory,this);
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Nonnull
    @Override
    public ItemStack getItem(int index) {
        return inventory.get(index);
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : inventory) {
            if (!itemStack.isEmpty()) return false;
        }
        return true;
    }

    @Nonnull
    @Override
    public ItemStack removeItem(int index, int count) {
        return ItemStackHelper.removeItem(inventory, index, count);
    }

    @Nonnull
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ItemStackHelper.takeItem(inventory, index);
    }

    @Override
    public void setItem(int index, @Nonnull ItemStack stack) {
        inventory.set(index, stack);

        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
    }


    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        if (getLevel() == null){
            return false;
        }
        if (getLevel().getBlockEntity(getBlockPos()) != this) {
            return false;
        } else {
            return player.distanceToSqr(getBlockPos().getX() + 0.5D, getBlockPos().getY() + 0.5D, getBlockPos().getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void startOpen(@Nonnull PlayerEntity p_174889_1_) {
    }

    @Override
    public void stopOpen(@Nonnull PlayerEntity p_174886_1_) {
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @Nonnull ItemStack itemStackIn, @Nullable Direction direction) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @Nonnull ItemStack stack, @Nonnull Direction direction) {
        return true;
    }

    @Override//isItemValidForSlot
    public boolean canPlaceItem(int index, @Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public void clearContent() {
        inventory.clear();
    }

    @Override
    public int get(int p_221476_1_) {
        return 0;
    }

    @Override
    public void set(int p_221477_1_, int p_221477_2_) {
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull Direction direction) {
        final int[] itemSlots = new int[27];
        for (int i = 0; i < 27; i++) {
            itemSlots[i] = i;
        }
        return itemSlots;
    }

    @Override
    public void tick() {
        if (getLevel() == null){
            return;
        }
        if (!getLevel().isClientSide) {
            updateSearchOffset();
            gatherItems();
        }
    }


    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.load(state, nbt);
        ItemStackHelper.loadAllItems(nbt, inventory);
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT nbt) {
        super.save(nbt);
        ItemStackHelper.saveAllItems(nbt, inventory);
        return nbt;
    }

    private IItemHandler itemHandler;

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (itemHandler == null) itemHandler = new InvWrapper(this);
            return (LazyOptional<T>) itemHandler;
        }
        return super.getCapability(cap, side);
    }

    private void updateSearchOffset() {
        searchOffset = (searchOffset + 1) % 27;
    }

    private void gatherItems() {
        for (int x = -4; x < 5; x++) {
            final int y = searchOffset / 9;
            final int z = (searchOffset % 9) - 4;
            gatherItemAtPos(getBlockPos().offset(x, y, z));
        }
    }

    private void gatherItemAtPos(BlockPos pos) {
        if(getLevel() == null){
            return;
        }
        final TileEntity tileEntity = getLevel().getBlockEntity(pos);
        if (!(tileEntity instanceof RoostTileEntity)) return;

        final RoostTileEntity roostTileEntity = (RoostTileEntity) getLevel().getBlockEntity(pos);

        int[] slots = new int[4];

        for (int i : slots) {
            if (pullItemFromSlot(roostTileEntity, i)) return;
        }
    }

    private boolean pullItemFromSlot(RoostTileEntity tileRoost, int index) {
        if (tileRoost == null){
            return false;
        }
        final ItemStack itemStack = tileRoost.outputInventory.get(index);
        if (!itemStack.isEmpty()) {
            final ItemStack itemStack1 = itemStack.copy();
            final ItemStack itemStack2 = InventoryUtil.putStackInInventoryAllSlots(tileRoost, this,
                    tileRoost.getOutputInventory().removeItem(index, 1),null);

            if (itemStack2.isEmpty()) {
                tileRoost.setChanged();
                setChanged();
                return true;
            }

            tileRoost.getOutputInventory().setItem(index, itemStack1);
        }

        return false;
    }



}
