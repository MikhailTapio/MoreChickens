package cn.evolvefield.mods.morechickens.integrations.jei.ingredients;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.data.ChickenData;
import cn.evolvefield.mods.morechickens.common.data.custom.ChickenReloadListener;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class ChickenIngredientHelper implements IIngredientHelper<EntityIngredient>
{


    @Nullable
    @Override
    public EntityIngredient getMatch(Iterable<EntityIngredient> iterable, @Nonnull EntityIngredient type) {
        for (EntityIngredient ingredient : iterable) {
            if (Objects.equals(ingredient.getChickenData().name, type.getChickenData().name)) {
                return ingredient;
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public String getDisplayName(EntityIngredient type) {
        final ChickenData chickenData = ChickenReloadListener.INSTANCE.getData(type.getChickenData().name);
        if (chickenData != null) {
            return new TranslationTextComponent("text.chickens.name."+ type.getChickenData().name).getString();
        }
        return "ChickenType:chicken:" + type.getChickenData().name;
    }

    @Nonnull
    @Override
    public String getUniqueId(EntityIngredient type) {
        return "ChickenType:" + type.getChickenData().name;
    }

    @Nonnull
    @Override
    public String getModId(@Nonnull EntityIngredient type) {
        return MoreChickens.MODID;
    }

    @Nonnull
    @Override
    public String getResourceId(@Nonnull EntityIngredient type) {
        final ResourceLocation rl = ModEntities.BASE_CHICKEN.get().getRegistryName();
        return (rl != null)?rl.getPath():"";
    }

    @Nonnull
    @Override
    public EntityIngredient copyIngredient(@Nonnull EntityIngredient type) {
            return type;
    }

    @Nonnull
    @Override
    public String getErrorInfo(@Nullable EntityIngredient type) {
        if (type == null) {
            return "ChickenType:null";
        }
        if (type.getChickenData().name == null) {
            return "ChickenType:chicken:null";
        }
        return "ChickenType:chicken:" + type.getChickenData().name;
    }
}
