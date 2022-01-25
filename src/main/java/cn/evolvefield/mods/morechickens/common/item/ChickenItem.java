package cn.evolvefield.mods.morechickens.common.item;


import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import cn.evolvefield.mods.morechickens.init.ModItemGroups;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class ChickenItem extends Item {
    public ChickenItem() {
        super(new Properties().craftRemainder(Items.BUCKET)
                .stacksTo(1)
                .tab(ModItemGroups.INSTANCE)
        );
        setRegistryName("chicken");
    }

    @Override
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public ITextComponent getName(@Nonnull ItemStack stack) {
        World world = Minecraft.getInstance().level;
        if (world == null) {
            return super.getName(stack);
        } else {
            final String name = stack.getOrCreateTag().getString("Name");
            return new TranslationTextComponent("text.chickens.name."+ name);
        }
    }


    @Override
    @Nonnull
    public ActionResultType useOn(ItemUseContext context) {
        final PlayerEntity player = context.getPlayer();
        if (player == null){
            return ActionResultType.PASS;
        }
        final World world = context.getLevel();
        final Hand hand = context.getHand();
        final BlockPos pos = context.getClickedPos().above();
        final ItemStack chickenItem = player.getItemInHand(hand);
        final String typeTag = chickenItem.getOrCreateTag().getString("Type");
        //final CompoundNBT chickenTag = chickenItem.getTagElement("ChickenData");
        final MinecraftServer mcs = world.getServer();
        if (!world.isClientSide && mcs != null) {
            if (typeTag.equals("modded")) {
                final CompoundNBT nbt = chickenItem.getTag();
                if (nbt == null)
                    return ActionResultType.PASS;
                final BaseChickenEntity chicken = new BaseChickenEntity(ModEntities.BASE_CHICKEN.get(), world);
                chicken.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                chicken.finalizeSpawn(mcs.overworld(), world.getCurrentDifficultyAt(pos), SpawnReason.SPAWN_EGG, null, null);
                chicken.readAdditionalSaveData(nbt);
                mcs.overworld().addFreshEntity(chicken);
            } else {
                final ChickenEntity chicken = new ChickenEntity(EntityType.CHICKEN, world);
                chicken.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                chicken.finalizeSpawn(mcs.overworld(), world.getCurrentDifficultyAt(pos), SpawnReason.SPAWN_EGG, null, null);
                mcs.overworld().addFreshEntity(chicken);
            }
            chickenItem.shrink(1);
        }

        return ActionResultType.SUCCESS;
    }

}
