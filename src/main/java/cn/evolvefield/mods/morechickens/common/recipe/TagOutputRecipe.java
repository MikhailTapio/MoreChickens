package cn.evolvefield.mods.morechickens.common.recipe;

import cn.evolvefield.mods.morechickens.init.ModConfig;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public abstract class TagOutputRecipe
{
    public final Map<Ingredient, IntArrayNBT> itemOutput;
    public final Map<ItemStack, IntArrayNBT> calculatedItemOutput = new LinkedHashMap<>();
    public static final Map<String, Integer> modPreference = new HashMap<>();

    public TagOutputRecipe(Ingredient itemOutput) {
        this.itemOutput = new LinkedHashMap<>();
        this.itemOutput.put(itemOutput, new IntArrayNBT(new int[]{1, 1, 100}));
    }

    public TagOutputRecipe(Map<Ingredient, IntArrayNBT> itemOutput) {
        this.itemOutput = itemOutput;
    }

    public Map<ItemStack, IntArrayNBT> getRecipeOutputs() {
        if (calculatedItemOutput.isEmpty() && !itemOutput.isEmpty()) {
            itemOutput.forEach((ingredient, intNBTS) -> {
                final ItemStack preferredItem = getPreferredItemByMod(ingredient);
                if (preferredItem != null && !preferredItem.getItem().equals(Items.BARRIER)) {
                    calculatedItemOutput.put(preferredItem, intNBTS);
                }
            });
        }

        return new LinkedHashMap<>(calculatedItemOutput);
    }

    private static ItemStack getPreferredItemByMod(Ingredient ingredient) {
        final List<ItemStack> stacks = Arrays.asList(ingredient.getItems());
        return getPreferredItemByMod(stacks);
    }

    private static ItemStack getPreferredItemByMod(List<ItemStack> list) {
        ItemStack preferredItem = null;
        int currBest = getModPreference().size();
        for (ItemStack item : list) {
            final ResourceLocation rl = item.getItem().getRegistryName();
            if (rl != null) {
                final String modId = rl.getNamespace();
                int priority = 100;
                if (getModPreference().containsKey(modId)) {
                    priority = getModPreference().get(modId);
                }
                if (preferredItem == null || (priority >= 0 && priority <= currBest)) {
                    preferredItem = item.copy();
                    currBest = priority;
                }
            }
        }
        return preferredItem;
    }

    public static Fluid getPreferredFluidByMod(String fluidName) {
        // Try loading from fluid registry
        Fluid preferredFluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));

        // Try loading fluid from fluid tag
        if (preferredFluid == null || preferredFluid.equals(Fluids.EMPTY)) {
            try {
                final ITag<Fluid> fluidTag = FluidTags.getAllTags().getTag(new ResourceLocation(fluidName));
                if (fluidTag != null && fluidTag.getValues().size() > 0) {
                    int currBest = getModPreference().size();
                    for (Fluid fluid: fluidTag.getValues()) {
                        if (fluid instanceof FlowingFluid) {
                            fluid = ((FlowingFluid) fluid).getSource();
                        }

                        ResourceLocation rl = fluid.getRegistryName();
                        if (rl != null) {
                            final String modId = rl.getNamespace();
                            int priority = 100;
                            if (getModPreference().containsKey(modId)) {
                                priority = getModPreference().get(modId);
                            }

                            if (preferredFluid == null || (priority >= 0 && priority <= currBest)) {
                                preferredFluid = fluid;
                                currBest = priority;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Who cares
            }
        }

        return preferredFluid;
    }

    private static Map<String, Integer> getModPreference() {
        if (modPreference.size() > 0) {
            return modPreference;
        }

        int priority = 0;
        for (String modId : ModConfig.COMMON.preferredTagSource.get()) {
            if (ModList.get().isLoaded(modId)) {
                modPreference.put(modId, ++priority);
            }
        }

        return modPreference;
    }
}
