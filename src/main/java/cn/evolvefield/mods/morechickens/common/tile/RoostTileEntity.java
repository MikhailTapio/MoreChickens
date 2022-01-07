package cn.evolvefield.mods.morechickens.common.tile;

import cn.evolvefield.mods.morechickens.common.container.RoostContainer;
import cn.evolvefield.mods.morechickens.common.container.base.ItemListInventory;
import cn.evolvefield.mods.morechickens.common.data.ChickenData;
import cn.evolvefield.mods.morechickens.common.data.ChickenUtils;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.tile.base.FakeWorldTileEntity;
import cn.evolvefield.mods.morechickens.init.ModBlocks;
import cn.evolvefield.mods.morechickens.init.ModContainers;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import cn.evolvefield.mods.morechickens.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoostTileEntity extends FakeWorldTileEntity implements ITickableTileEntity, INamedContainerProvider {


    private ItemStack chickenItem;
    public final NonNullList<ItemStack> outputInventory;
    private AnimalEntity chickenEntity;
    final Random rand = new Random();
    private int progress;
    private int timeElapsed = 0;
    private int timeUntilNextLay = 0;

    public final IIntArray dataAccess = new IIntArray() {
        public int get(int id) {
            return (id == 0)?progress:0;
        }

        public void set(int id, int value) {
            if (id == 0) {
                progress = value;
            }
        }

        public int getCount() {
            return 2;
        }
    };

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
        return new RoostContainer(ModContainers.ROOST_CONTAINER,id, playerInventory, getOutputInventory(),dataAccess,this);
    }

    public RoostTileEntity() {
        super(ModTileEntities.ROOST, ModBlocks.BLOCK_ROOST.defaultBlockState());
        outputInventory = NonNullList.withSize(4, ItemStack.EMPTY);
        chickenItem = ItemStack.EMPTY;
    }

    public AnimalEntity getChicken(World world, ItemStack stack) {
        final CompoundNBT compound = stack.getOrCreateTag();
        final String type = compound.getString("Type");
        AnimalEntity chicken;
        if(type.equals("vanilla")){
            chicken = new ChickenEntity(EntityType.CHICKEN,world);
            return chicken;
        }
        chicken = new BaseChickenEntity(ModEntities.BASE_CHICKEN.get(), world);
        chicken.readAdditionalSaveData(compound);
        return chicken;
    }

    public void setChicken(ItemStack stack, AnimalEntity chickenEntity) {
        final CompoundNBT compound = stack.getOrCreateTag();
        if (chickenEntity instanceof BaseChickenEntity){
            chickenEntity.addAdditionalSaveData(compound);
            compound.putString("Type","modded");
            stack.setTag(compound);
        }
        else if (chickenEntity instanceof ChickenEntity){
            compound.putString("Type","vanilla");
            stack.setTag(compound);
        }

    }

    public void setChickenItem(ItemStack chicken1) {
        this.chickenItem = chicken1;
        if (chicken1.isEmpty()) {
            chickenEntity = null;
        } else {
            chickenEntity = getChicken(level, chicken1);
        }
        setChanged();
        sync();
    }

    @Nullable
    public ItemStack getChickenItem() {
        return chickenItem;
    }

    @Nullable
    public String getChickenItemName(){
        return getChickenItem() != null ? getChickenItem().getOrCreateTag().getString("Name") : null;
    }

    public boolean hasChickenItem() {
        return chickenItem != null &&!chickenItem.isEmpty();
    }

    @Nullable
    public AnimalEntity getChickenEntity() {
        if (chickenEntity == null && !chickenItem.isEmpty()) {
            chickenEntity = getChicken(level, chickenItem);
        }
        return chickenEntity;
    }

    public ItemStack removeChickenItem() {
        final ItemStack v = chickenItem;
        setChickenItem(ItemStack.EMPTY);
        return v;
    }



    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT compound) {
        if (hasChickenItem()) {
            final CompoundNBT comp = new CompoundNBT();
            if (chickenEntity != null) {
                setChicken(chickenItem, chickenEntity);
            }
            chickenItem.save(comp);
            compound.put("ChickenItem", comp);
        }
        compound.putInt("TimeElapsed",this.timeElapsed);
        compound.putInt("TimeUntilNextLay", timeUntilNextLay);
        compound.put("OutputInventory", ItemStackHelper.saveAllItems(new CompoundNBT(), outputInventory, true));
        return super.save(compound);
    }

    @Override
    public void load(@Nonnull BlockState state, CompoundNBT compound) {
        if (compound.contains("ChickenItem")) {
            final CompoundNBT comp = compound.getCompound("ChickenItem");
            chickenItem = ItemStack.of(comp);
            chickenEntity = null;
        } else {
            removeChickenItem();
        }
        this.timeUntilNextLay = compound.getInt("TimeUntilNextLay");
        this.timeElapsed = compound.getInt("TimeElapsed");
        ItemStackHelper.loadAllItems(compound.getCompound("OutputInventory"), outputInventory);
        super.load(state, compound);
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) {
            return;
        }
        updateProgress();
        updateTimerIfNeeded();
        spawnChickenDropIfNeeded();
    }
    private void updateTimerIfNeeded() {
        if (  canLay() && !outputIsFull()) {
            timeElapsed += 5;
            setChanged();
        }
    }
    public boolean canLay() {
        if (getChickenEntity() == null){
            return false;
        }
        if (!hasChickenItem() ) {
            return false;
        }
        return !getChickenEntity().isBaby() ;
    }


    private void updateProgress() {
        if (hasChickenItem()){
            this.progress = timeUntilNextLay == 0 ? 0 : (timeElapsed * 1000 / timeUntilNextLay);
        }
        else {
            this.progress = 0;
        }
    }

    public double getProgress() {
        return progress / 1000.0;
    }


    private void spawnChickenDropIfNeeded() {
        if ((timeElapsed >= timeUntilNextLay)) {
            if (timeUntilNextLay > 0) {
                if(getLevel() != null && addLoot()){
                    getLevel().playSound(null, getBlockPos(), SoundEvents.CHICKEN_EGG, SoundCategory.NEUTRAL, 0.5F, 0.8F);
                    //spawnParticles();
                }
            }
            resetTimer();
        }
    }

    private void resetTimer() {
        if(getChickenItem() == null){
            return;
        }
        final String type = getChickenItem().getOrCreateTag().getString("Type");
        final String name = getChickenItem().getOrCreateTag().getString("Name");
        final ChickenData data = ChickenUtils.getChickenDataByName(name);
        final int growth = getChickenItem().getOrCreateTag().getInt("ChickenGrowth");
        timeElapsed = 0;
        if (type.equals("vanilla")){
            timeUntilNextLay = rand.nextInt(6000) + 6000;
        }
        else if(type.equals("modded"))
        {
            //final BaseChickenEntity chicken = (BaseChickenEntity) getChickenEntity();
            timeUntilNextLay = ChickenUtils.calcNewEggLayTime(rand, data, growth);
            timeUntilNextLay = Math.max(600, timeUntilNextLay) + 6000;
        }
        setChanged();
    }

    private boolean addLoot() {
        if(getChickenItem() == null){
            return false;
        }
        for (int i = 0; i < outputInventory.size(); i++) {
            if (outputInventory.get(i).isEmpty()) {
                final String type = getChickenItem().getOrCreateTag().getString("Type");
                final String name = getChickenItem().getOrCreateTag().getString("Name");
                final ChickenData data = ChickenUtils.getChickenDataByName(name);
                final int gain = getChickenItem().getOrCreateTag().getInt("ChickenGain");
                if(type.equals("modded")) {
                    //final BaseChickenEntity chicken = (BaseChickenEntity) getChickenEntity();
                    final ItemStack layItem = getRandItemStack(ChickenUtils.calcDrops(gain, data, 0, rand),rand);
                    outputInventory.set(i, layItem);

                }
                else if(type.equals("vanilla"))
                {
                    final List<ItemStack> vanillaLay = new ArrayList<>();
                    vanillaLay.add(new ItemStack(Items.EGG));
                    vanillaLay.add(new ItemStack(Items.FEATHER));
                    outputInventory.set(i, getRandItemStack(vanillaLay,rand));
                }
                return true;
            }
        }
        return false;
    }


    private ItemStack getRandItemStack(List<ItemStack> list, Random random){
       return list.get(random.nextInt(list.size()));
    }


    private boolean outputIsFull() {
        for (ItemStack stack : outputInventory) {
            if (stack.getCount() < stack.getMaxStackSize()) return false;
        }
        return true;
    }



    public IInventory getOutputInventory() {
        return new ItemListInventory(outputInventory, this::setChanged);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (!remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side != null && side.equals(Direction.DOWN)) {
                return LazyOptional.of(this::getOutputInventoryItemHandler).cast();
            }
        }
        return super.getCapability(cap, side);
    }

    private IItemHandlerModifiable outputInventoryHandler;

    @Nonnull
    public IItemHandlerModifiable getOutputInventoryItemHandler() {
        return (outputInventoryHandler == null)?new ItemStackHandler(outputInventory):outputInventoryHandler;
    }







}
