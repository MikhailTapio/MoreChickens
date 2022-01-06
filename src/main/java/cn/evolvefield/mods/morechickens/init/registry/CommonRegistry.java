package cn.evolvefield.mods.morechickens.init.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class CommonRegistry {
    public CommonRegistry() {
    }

    public static SimpleChannel registerChannel(String modId, String name, int protocolVersion) {
        final String protocolVersionString = String.valueOf(protocolVersion);
        return NetworkRegistry.newSimpleChannel(new ResourceLocation(modId, name), () -> protocolVersionString, (s) -> s.equals(protocolVersionString), (s) -> s.equals(protocolVersionString));
    }
    public static SimpleChannel registerChannel(String modId, String name) {
        return NetworkRegistry.newSimpleChannel(new ResourceLocation(modId, name), () -> "1.0.0", (s) -> true, (s) -> true);
    }


}
