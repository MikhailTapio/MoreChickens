package cn.evolvefield.mods.morechickens.common.block;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.block.base.HorizontalRotatableBlock;
import cn.evolvefield.mods.morechickens.common.item.ChickenItem;
import cn.evolvefield.mods.morechickens.common.tile.RoostTileEntity;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RoostBlock extends HorizontalRotatableBlock {

    public RoostBlock() {
        super(Properties.of(Material.WOOD)
                .sound(SoundType.WOOD)
                .strength(2.0f,5.0f)
                .noCollission()

        );
        setRegistryName(new ResourceLocation(MoreChickens.MODID, "roost"));

    }

    @Nonnull
    @Override
    public ActionResultType use(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos, PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
        final ItemStack heldItem = player.getItemInHand(handIn);
        final TileEntity tileEntity = worldIn.getBlockEntity(pos);
        if (!(tileEntity instanceof RoostTileEntity)) {
            return super.use(state, worldIn, pos, player, handIn, hit);
        }
        final RoostTileEntity breeder = (RoostTileEntity) tileEntity;

        if (!breeder.hasChickenItem() && heldItem.getItem() instanceof ChickenItem) {
            breeder.setChickenItem(heldItem.copy());
            ItemUtils.decrItemStack(heldItem, player);
            worldIn.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return ActionResultType.SUCCESS;

        } else if (player.isShiftKeyDown() && breeder.hasChickenItem()) {
            final ItemStack stack = breeder.removeChickenItem();
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
            if (!worldIn.isClientSide()) {
                openGui((ServerPlayerEntity) player, (RoostTileEntity) tileEntity);

            }
            return ActionResultType.SUCCESS;
        }
    }

    public void openGui(ServerPlayerEntity player, RoostTileEntity tileEntity) {
        NetworkHooks.openGui(player, tileEntity, packetBuffer -> packetBuffer.writeBlockPos(tileEntity.getBlockPos()));
    }

    @Override
    public void destroy(IWorld world, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        final TileEntity tileEntity = world.getBlockEntity(pos);

        if (tileEntity instanceof RoostTileEntity) {
            InventoryHelper.dropItemStack((World) world, pos.getX(),pos.getY(),pos.getZ(), ModBlocks.BLOCK_ROOST.asItem().getDefaultInstance());
        }

        super.destroy(world, pos, state);
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderShape(@Nonnull BlockState p_149645_1_) {
        return BlockRenderType.INVISIBLE;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new RoostTileEntity();
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
