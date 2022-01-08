package cn.evolvefield.mods.morechickens.common.block;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.block.base.HorizontalRotatableBlock;
import cn.evolvefield.mods.morechickens.common.item.ChickenItem;
import cn.evolvefield.mods.morechickens.common.tile.BreederTileEntity;
import cn.evolvefield.mods.morechickens.common.util.ItemUtils;
import cn.evolvefield.mods.morechickens.init.ModBlocks;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BreederBlock extends HorizontalRotatableBlock  {


    public BreederBlock() {
        super(Properties.of(Material.WOOD)
                .sound(SoundType.WOOD)
                .strength(2.0f,5.0f)
                .noCollission()
        );
        setRegistryName(new ResourceLocation(MoreChickens.MODID, "breeder"));

    }

    @Nonnull
    @Override
    public ActionResultType use(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos, PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
        final ItemStack heldItem = player.getItemInHand(handIn);
        final TileEntity tileEntity = worldIn.getBlockEntity(pos);
        if (!(tileEntity instanceof BreederTileEntity)) {
            return super.use(state, worldIn, pos, player, handIn, hit);
        }
        final BreederTileEntity breeder = (BreederTileEntity) tileEntity;

        if (!breeder.hasChicken1() && heldItem.getItem() instanceof ChickenItem) {
            breeder.setChicken1(heldItem.copy());
            ItemUtils.decrItemStack(heldItem, player);
            worldIn.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return ActionResultType.SUCCESS;
        } else if (!breeder.hasChicken2() && heldItem.getItem() instanceof ChickenItem) {
            breeder.setChicken2(heldItem.copy());
            ItemUtils.decrItemStack(heldItem, player);
            worldIn.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return ActionResultType.SUCCESS;
        } else if (player.isShiftKeyDown() && breeder.hasChicken2()) {
            final ItemStack stack = breeder.removeChicken2();
            if (heldItem.isEmpty()) {
                player.setItemInHand(handIn, stack);
            } else {
                if (!player.inventory.add(stack)) {
                    final Direction direction = state.getValue(FACING);
                    InventoryHelper.dropItemStack(worldIn, direction.getStepX() + pos.getX() + 0.5D, pos.getY() + 0.5D, direction.getStepZ() + pos.getZ() + 0.5D, stack);
                }
            }
            worldIn.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return ActionResultType.SUCCESS;
        } else if (player.isShiftKeyDown() && breeder.hasChicken1()) {
            final ItemStack stack = breeder.removeChicken1();
            if (heldItem.isEmpty()) {
                player.setItemInHand(handIn, stack);
            } else {
                if (!player.inventory.add(stack)) {
                    final Direction direction = state.getValue(FACING);
                    InventoryHelper.dropItemStack(worldIn, direction.getStepX() + pos.getX() + 0.5D, pos.getY() + 0.5D, direction.getStepZ() + pos.getZ() + 0.5D, stack);
                }
            }
            worldIn.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return ActionResultType.SUCCESS;
        } else {
            if (!worldIn.isClientSide())
            {
                openGui((ServerPlayerEntity) player, (BreederTileEntity) tileEntity);

            }
            return ActionResultType.SUCCESS;
        }
    }
    public void openGui(ServerPlayerEntity player, BreederTileEntity tileEntity) {
        NetworkHooks.openGui(player, tileEntity, packetBuffer -> packetBuffer.writeBlockPos(tileEntity.getBlockPos()));
    }


    @Override
    public void onRemove(@Nonnull BlockState state1, World world, @Nonnull BlockPos pos, @Nonnull BlockState state2, boolean bool) {
        final TileEntity tileEntity = world.getBlockEntity(pos);

        if (tileEntity instanceof BreederTileEntity) {
            InventoryHelper.dropContents(world, pos, ((BreederTileEntity) tileEntity).getFoodInventory());
            InventoryHelper.dropContents(world, pos, ((BreederTileEntity) tileEntity).getOutputInventory());
            InventoryHelper.dropItemStack(world, pos.getX(),pos.getY(),pos.getZ(), ModBlocks.BLOCK_BREEDER.asItem().getDefaultInstance());
        }

        super.onRemove(state1, world, pos, state2, bool);
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderShape(@Nonnull BlockState p_149645_1_) {
        return BlockRenderType.INVISIBLE;
    }


    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new BreederTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getShadeBrightness(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
        return 1F;
    }


}
