package cn.evolvefield.mods.morechickens.common.block;


import cn.evolvefield.mods.morechickens.common.tile.CollectorTileEntity;
import cn.evolvefield.mods.morechickens.init.ModBlocks;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CollectorBlock extends ContainerBlock {
    public CollectorBlock() {
        super(Properties.of(Material.WOOD)
                .sound(SoundType.WOOD)
                .strength(2.0f,5.0f)

        );
        setRegistryName("collector");
    }

    @Nonnull
    @Override
    public ActionResultType use(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity playerEntity, @Nonnull Hand hand, @Nonnull BlockRayTraceResult result) {
        final CollectorTileEntity tileEntity = (CollectorTileEntity) world.getBlockEntity(pos);
        if (tileEntity != null && !world.isClientSide) {
            NetworkHooks.openGui((ServerPlayerEntity) playerEntity, tileEntity
                    ,
                    (PacketBuffer packerBuffer) -> packerBuffer.writeBlockPos(tileEntity.getBlockPos())
            );
        }

        return ActionResultType.SUCCESS;
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderShape(@Nonnull BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onRemove(@Nonnull BlockState state1, World world, @Nonnull BlockPos pos, @Nonnull BlockState state2, boolean bool) {
        final TileEntity tileEntity = world.getBlockEntity(pos);

        if (tileEntity instanceof CollectorTileEntity) {
            InventoryHelper.dropContents(world, pos, ((CollectorTileEntity) tileEntity).getInventory());
            InventoryHelper.dropItemStack(world, pos.getX(),pos.getY(),pos.getZ(), ModBlocks.BLOCK_COLLECTOR.asItem().getDefaultInstance());
        }

        super.onRemove(state1, world, pos, state2, bool);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(@Nonnull IBlockReader p_196283_1_) {
        return new CollectorTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}
