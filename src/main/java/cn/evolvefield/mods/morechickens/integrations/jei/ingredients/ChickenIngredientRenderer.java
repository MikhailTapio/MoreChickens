package cn.evolvefield.mods.morechickens.integrations.jei.ingredients;

import cn.evolvefield.mods.morechickens.client.render.ingredient.ChickenRenderer;
import cn.evolvefield.mods.morechickens.common.data.ChickenData;
import cn.evolvefield.mods.morechickens.common.data.custom.ChickenReloadListener;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ChickenIngredientRenderer implements IIngredientRenderer<EntityIngredient>
{

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int xPosition, int yPosition, @Nullable EntityIngredient type) {
        if (type == null) {
            return;
        }

        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            ChickenRenderer.render(matrixStack, xPosition, yPosition, type.getChickenData(), minecraft);
        }
    }

    @Nonnull
    @Override
    public List<ITextComponent> getTooltip(EntityIngredient type, @Nonnull ITooltipFlag iTooltipFlag) {
        final List<ITextComponent> list = new ArrayList<>();
        final ChickenData chickenData = ChickenReloadListener.INSTANCE.getData(type.getChickenData().name);
        if (chickenData != null) {
            list.add(new TranslationTextComponent("text.chickens.name." + type.getChickenData().name));
        }
        list.add(new TranslationTextComponent("text.chickens.name." + type.getChickenData().name).withStyle(TextFormatting.BLUE));
        return list;
    }
}
