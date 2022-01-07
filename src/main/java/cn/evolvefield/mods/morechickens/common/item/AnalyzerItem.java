package cn.evolvefield.mods.morechickens.common.item;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.data.Gene;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.init.ModItemGroups;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class AnalyzerItem extends Item {
    public AnalyzerItem() {
        super(new Properties()
                .durability(238)
                .tab(ModItemGroups.INSTANCE)
        );
        setRegistryName("analyzer");
    }


    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(new TranslationTextComponent("item.chickens.analyzer.tooltip1"));
        tooltip.add(new TranslationTextComponent("item.chickens.analyzer.tooltip2"));
    }


    @Nonnull
    @Override
    public ActionResultType interactLivingEntity(@Nonnull ItemStack stack, PlayerEntity playerIn, @Nonnull LivingEntity target, @Nonnull Hand hand) {
        if(playerIn.level.isClientSide)
            return ActionResultType.FAIL;
        if(!(target instanceof BaseChickenEntity))
            return ActionResultType.FAIL;
        final BaseChickenEntity chickenEntity = (BaseChickenEntity) target;
        final Gene gene = chickenEntity.getGene();
        playerIn.sendMessage(
                new TranslationTextComponent("text." + MoreChickens.MODID + ".name." + chickenEntity.getChickenName()).withStyle(TextFormatting.GOLD),
                Util.NIL_UUID
        );
        /*
        playerIn.sendMessage(
                new TranslationTextComponent("text." + MoreChickens.MODID + ".stat.amount", gene.layAmount),
                Util.NIL_UUID);
        playerIn.sendMessage(
                new TranslationTextComponent("text." + MoreChickens.MODID + ".stat.time", gene.layTime),
                Util.NIL_UUID);
        */
        playerIn.sendMessage(
                new TranslationTextComponent("text." + MoreChickens.MODID + ".stat.gain", gene.GAIN).withStyle(TextFormatting.DARK_AQUA),
                Util.NIL_UUID);
        playerIn.sendMessage(
                new TranslationTextComponent("text." + MoreChickens.MODID + ".stat.growth", gene.GROWTH).withStyle(TextFormatting.GREEN),
                Util.NIL_UUID);
        playerIn.sendMessage(
                new TranslationTextComponent("text." + MoreChickens.MODID + ".stat.strength", gene.STRENGTH).withStyle(TextFormatting.AQUA),
                Util.NIL_UUID);
        playerIn.sendMessage(
                new TranslationTextComponent("text." + MoreChickens.MODID + ".stat.eggTimer", String.format("%.2f",chickenEntity.getLayTimer() / 1200d)).withStyle(TextFormatting.LIGHT_PURPLE),
                Util.NIL_UUID);
        return ActionResultType.PASS;

    }


}
