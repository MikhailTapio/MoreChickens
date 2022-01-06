package cn.evolvefield.mods.morechickens.init;


import cn.evolvefield.mods.morechickens.MoreChickens;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ModItemGroups extends ItemGroup {
    public ModItemGroups() {
        super(MoreChickens.MODID);
    }


    public static final ModItemGroups INSTANCE = new ModItemGroups();

    @Nonnull
    @Override
    public ItemStack makeIcon() {
        return new ItemStack(ModItems.ANALYZER);
    }
}
