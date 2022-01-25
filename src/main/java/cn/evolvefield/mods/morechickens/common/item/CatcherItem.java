package cn.evolvefield.mods.morechickens.common.item;


import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.init.ModItemGroups;
import cn.evolvefield.mods.morechickens.init.ModItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class CatcherItem extends Item {

    public CatcherItem() {
        super(new Properties()
                .stacksTo(1)
                .durability(238)
                .craftRemainder(Items.BUCKET)
                .tab(ModItemGroups.INSTANCE)
        );
        setRegistryName("catcher");

    }
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(new TranslationTextComponent("item.chickens.catcher.tooltip1"));

    }

    @Nonnull
    @Override
    public ActionResultType interactLivingEntity(@Nonnull ItemStack itemStack, @Nonnull PlayerEntity player, @Nonnull LivingEntity entity, @Nonnull Hand hand) {
        final World world = entity.level;
        final Vector3d pos = new Vector3d(entity.getX(), entity.getY(), entity.getZ());
        if(entity instanceof ChickenEntity) {
            final AnimalEntity chickenEntity = (AnimalEntity) entity;
            if (entity.isBaby()) {
                if (world.isClientSide) {
                    spawnParticles(pos, world, ParticleTypes.SMOKE);
                }
                world.playSound(player, pos.x, pos.y, pos.z, SoundEvents.CHICKEN_HURT, entity.getSoundSource(), 1.0F, 1.0F);
                itemStack.hurtAndBreak(1, player, LivingEntity::animateHurt);
            } else {
                if (world.isClientSide) {
                    spawnParticles(pos, world, ParticleTypes.EXPLOSION);
                } else {
                    final ItemStack chickenItem = new ItemStack(ModItems.ITEM_CHICKEN);
                    final CompoundNBT tagCompound = chickenItem.getOrCreateTagElement("ChickenData");
                    chickenEntity.addAdditionalSaveData(tagCompound);
                    chickenEntity.remove(false);
                    tagCompound.putString("Type", "vanilla");
                    chickenItem.setTag(tagCompound);
                    final ItemEntity item = entity.spawnAtLocation(chickenItem, 1.0F);
                    if (item != null) {
                        item.setDeltaMovement(0, 0.2d, 0);
                    }
                }
                world.playSound(player, pos.x, pos.y, pos.z, SoundEvents.CHICKEN_EGG, entity.getSoundSource(), 1.0F, 1.0F);
            }
            /*
            if (world.isClientSide) {
                spawnParticles(pos, world, ParticleTypes.EXPLOSION);
            } else {
                final ItemStack chickenItem = new ItemStack(ModItems.ITEM_CHICKEN);
                final CompoundNBT tagCompound = chickenItem.getOrCreateTagElement("ChickenData");
                tagCompound.putString("Type", "vanilla");
                chickenItem.setTag(tagCompound);
                final ItemEntity item = entity.spawnAtLocation(chickenItem, 1.0F);
                if (item != null) {
                    item.setDeltaMovement(0, 0.2D, 0);
                    final MinecraftServer mcs = entity.getServer();
                    if(mcs != null) {
                        mcs.overworld().removeEntity(entity, false);
                    }
                }
            }
            world.playSound(player, pos.x, pos.y, pos.z, SoundEvents.CHICKEN_EGG, entity.getSoundSource(), 1.0F, 1.0F);
            */
            return ActionResultType.SUCCESS;
        }
        else if (entity instanceof BaseChickenEntity) {

            final BaseChickenEntity chickenEntity = (BaseChickenEntity)entity;
            if (entity.isBaby()) {
                if (world.isClientSide) {
                    spawnParticles(pos, world, ParticleTypes.SMOKE);
                }
                world.playSound(player, pos.x, pos.y, pos.z, SoundEvents.CHICKEN_HURT, entity.getSoundSource(), 1.0F, 1.0F);
                itemStack.hurtAndBreak(1, player, LivingEntity::animateHurt);
            } else {
                if (world.isClientSide) {
                    spawnParticles(pos, world, ParticleTypes.EXPLOSION);
                } else {
                    final ItemStack chickenItem = new ItemStack(ModItems.ITEM_CHICKEN);
                    final CompoundNBT tagCompound = chickenItem.getOrCreateTagElement("ChickenData");
                    chickenEntity.addAdditionalSaveData(tagCompound);
                    chickenEntity.remove(false);
                    tagCompound.putString("Type", "modded");
                    chickenItem.setTag(tagCompound);
                    final ItemEntity item = entity.spawnAtLocation(chickenItem, 1.0F);
                    if (item != null) {
                        item.setDeltaMovement(0,0.2d,0);
                    }
                }
                world.playSound(player, pos.x, pos.y, pos.z, SoundEvents.CHICKEN_EGG, entity.getSoundSource(), 1.0F, 1.0F);
            }
            return ActionResultType.SUCCESS;
        }

        player.sendMessage(new TranslationTextComponent("item.chickens.catcher.fail"), Util.NIL_UUID);
        return ActionResultType.FAIL;
    }


    private void spawnParticles(Vector3d pos, World world, IParticleData data) {
        final Random rand = world.random;
        for (int k = 0; k < 20; ++k) {
            final double xCoord = pos.x + (rand.nextDouble() * 0.6D) - 0.3D;
            final double yCoord = pos.y + (rand.nextDouble() * 0.6D);
            final double zCoord = pos.z + (rand.nextDouble() * 0.6D) - 0.3D;
            final double xSpeed = rand.nextGaussian() * 0.02D;
            final double ySpeed = rand.nextGaussian() * 0.2D;
            final double zSpeed = rand.nextGaussian() * 0.02D;
            world.addParticle(data, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
        }
    }


}
