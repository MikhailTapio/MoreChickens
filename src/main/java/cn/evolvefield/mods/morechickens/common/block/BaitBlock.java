package cn.evolvefield.mods.morechickens.common.block;

import cn.evolvefield.mods.morechickens.common.block.utils.BaitType;
import cn.evolvefield.mods.morechickens.common.block.utils.EnvironmentalCondition;
import cn.evolvefield.mods.morechickens.common.tile.BaitTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BaitBlock extends ContainerBlock {
    public static final String nameSuffix = "_bait";

    private static final VoxelShape BOUNDING_BOX = VoxelShapes.box(0.1, 0, 0.1, 0.9, 0.1, 0.9);

    private final BaitType baitType;

    public BaitBlock(BaitType baitType) {
        super(Properties.of(Material.CAKE).strength(0.1f));
        this.baitType = baitType;
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return BOUNDING_BOX;
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull IBlockReader reader, @Nonnull BlockPos pos, @Nonnull ISelectionContext p_220071_4_) {
        return VoxelShapes.empty();
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(@Nonnull IBlockReader p_196283_1_) {
        return new BaitTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public ActionResultType use(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
        final BaitTileEntity tileEntity = (BaitTileEntity) world.getBlockEntity(pos);
        if (tileEntity != null) {
            final EnvironmentalCondition environmentStatus = tileEntity.checkSpawnConditions(true);
            if (!world.isClientSide) {
                final TextComponent chatComponent = new TranslationTextComponent(environmentStatus.langKey);
                chatComponent.getStyle().withColor(environmentStatus != EnvironmentalCondition.CanSpawn ? TextFormatting.RED : TextFormatting.GREEN);
                player.sendMessage(chatComponent, Util.NIL_UUID);
            }
        }

        return ActionResultType.SUCCESS;
    }


    @Override
    public void setPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        if (placer instanceof PlayerEntity) {
            final BaitTileEntity tileEntity = (BaitTileEntity) world.getBlockEntity(pos);
            if (tileEntity != null) {
                final EnvironmentalCondition environmentStatus = tileEntity.checkSpawnConditions(true);
                if (!world.isClientSide) {
                    final TextComponent chatComponent = new TranslationTextComponent(environmentStatus.langKey);
                    chatComponent.getStyle().withColor(environmentStatus != EnvironmentalCondition.CanSpawn ? TextFormatting.RED : TextFormatting.GREEN);
                    placer.sendMessage(chatComponent, Util.NIL_UUID);
                }
            }
        }
    }

    @Override
    public void animateTick(@Nonnull BlockState stateIn, World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
        //if (!ExCompressumConfig.CLIENT.disableParticles.get()) {
        final BaitTileEntity tileEntity = (BaitTileEntity) world.getBlockEntity(pos);
        if (tileEntity != null && tileEntity.checkSpawnConditions(false) == EnvironmentalCondition.CanSpawn) {
            if (rand.nextFloat() <= 0.2f) {
                world.addParticle(ParticleTypes.SMOKE, pos.getX() + rand.nextFloat(), pos.getY() + rand.nextFloat() * 0.5f, pos.getZ() + rand.nextFloat(), 0.0, 0.0, 0.0);
            }
        }
        //}
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable IBlockReader world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {

    }



    public BaitType getBaitType() {
        return baitType;
    }


}
