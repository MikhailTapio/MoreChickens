package cn.evolvefield.mods.morechickens.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BallItem extends Item {

    private final Fluid fluid;

    public BallItem( Fluid fluid) {
        super(new Properties());
        this.fluid = fluid;
    }


    /**
     * Place the fluid
     */
    @Nonnull
    @Override
    public ActionResult<ItemStack> use(@Nonnull World worldIn, PlayerEntity playerIn, @Nonnull Hand handIn) {
        final ItemStack itemstack = playerIn.getItemInHand(handIn);
        final BlockRayTraceResult rayTraceResult = getPlayerPOVHitResult(worldIn, playerIn, RayTraceContext.FluidMode.NONE);
        final ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(playerIn, worldIn, itemstack, rayTraceResult);
        if (ret != null) return ret;
        if (rayTraceResult.getType() == RayTraceResult.Type.MISS) {
            return ActionResult.pass(itemstack);
        } else if (rayTraceResult.getType() != RayTraceResult.Type.BLOCK) {
            return ActionResult.pass(itemstack);
        } else {
            final BlockPos blockpos = rayTraceResult.getBlockPos();
            final Direction direction = rayTraceResult.getDirection();
            final BlockPos blockpos1 = blockpos.relative(direction);
            if (worldIn.mayInteract(playerIn, blockpos) && playerIn.mayUseItemAt(blockpos1, direction, itemstack)) {
                final BlockState blockstate = worldIn.getBlockState(blockpos);
                final BlockPos blockpos2 = canBlockContainFluid(worldIn, blockpos, blockstate) ? blockpos : blockpos1;
                if (this.tryPlaceContainedLiquid(playerIn, worldIn, blockpos2, rayTraceResult)) {
                    if (playerIn instanceof ServerPlayerEntity) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)playerIn, blockpos2, itemstack);
                    }

                    playerIn.awardStat(Stats.ITEM_USED.get(this));
                    if(!playerIn.isCreative())
                        itemstack.shrink(1);
                    return ActionResult.sidedSuccess(itemstack, worldIn.isClientSide);
                } else {
                    return ActionResult.fail(itemstack);
                }
            } else {
                return ActionResult.fail(itemstack);
            }
        }
    }
    public int getBurnTime(ItemStack stack){
        return fluid == Fluids.LAVA ? 20_000 : 0;
    }

    public boolean tryPlaceContainedLiquid(@Nullable PlayerEntity player, World worldIn, BlockPos posIn, @Nullable BlockRayTraceResult rayTrace) {
        if (!(this.fluid instanceof FlowingFluid)) {
            return false;
        } else {
            final BlockState blockstate = worldIn.getBlockState(posIn);
            final Block block = blockstate.getBlock();
            final Material material = blockstate.getMaterial();
            final boolean flag = blockstate.canBeReplaced(this.fluid);
            final boolean flag1 = blockstate.isAir() || flag || block instanceof ILiquidContainer && ((ILiquidContainer)block).canPlaceLiquid(worldIn, posIn, blockstate, this.fluid);
            if (!flag1) {
                return rayTrace != null && this.tryPlaceContainedLiquid(player, worldIn, rayTrace.getBlockPos().relative(rayTrace.getDirection()), null);
            } else if (worldIn.dimensionType().ultraWarm() && this.fluid.is(FluidTags.WATER)) {
                final int i = posIn.getX();
                final int j = posIn.getY();
                final int k = posIn.getZ();
                worldIn.playSound(player, posIn, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.random.nextFloat() - worldIn.random.nextFloat()) * 0.8F);

                for(int l = 0; l < 8; ++l) {
                    worldIn.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D);
                }

                return true;
            } else if (block instanceof ILiquidContainer && ((ILiquidContainer)block).canPlaceLiquid(worldIn,posIn,blockstate,fluid)) {
                ((ILiquidContainer)block).placeLiquid(worldIn, posIn, blockstate, ((FlowingFluid)this.fluid).getSource(false));
                this.playEmptySound(player, worldIn, posIn);
                return true;
            } else {
                if (!worldIn.isClientSide && flag && !material.isLiquid()) {
                    worldIn.destroyBlock(posIn, true);
                }

                if (!worldIn.setBlock(posIn, ((FlowingFluid) this.fluid).defaultFluidState().createLegacyBlock(), 11) && !blockstate.getFluidState().isSource()) {
                    return false;
                } else {
                    this.playEmptySound(player, worldIn, posIn);
                    return true;
                }
            }
        }
    }

    protected void playEmptySound(@Nullable PlayerEntity player, IWorld worldIn, BlockPos pos) {
        final SoundEvent soundevent = this.fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        worldIn.playSound(player, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    private boolean canBlockContainFluid(World worldIn, BlockPos posIn, BlockState blockstate)
    {
        return blockstate.getBlock() instanceof ILiquidContainer && ((ILiquidContainer)blockstate.getBlock()).canPlaceLiquid(worldIn, posIn, blockstate, this.fluid);
    }
}
