package net.glease.tc4tweak;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.glease.tc4tweak.modules.FlushableCache;
import net.glease.tc4tweak.network.MessageSendConfiguration;
import net.glease.tc4tweak.network.NetworkedConfiguration;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.research.ResearchCategories;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent e) {
        ConfigurationHandler.INSTANCE.init(e.getSuggestedConfigurationFile());

        if (Loader.isModLoaded("MineTweaker3")) MTCompat.preInit();

        TC4Tweak.INSTANCE.CHANNEL.registerMessage(
                MessageSendConfiguration.class, MessageSendConfiguration.class, 0, Side.CLIENT);
        int debugadd = Integer.getInteger("glease.debug.addtc4tabs.pre", 0);
        addDummyCategories(debugadd, "DUMMYPRE");
    }

    public void serverStarted() {
        FlushableCache.enableAll(true);
        NetworkedConfiguration.resetCheckWorkbenchRecipes();
    }

    public void init(FMLInitializationEvent e) {}

    public void postInit(FMLPostInitializationEvent e) {
        int debugadd = Integer.getInteger("glease.debug.addtc4tabs.post", 0);
        for (int i = 0; i < debugadd; i++) {
            addDummyCategories(debugadd, "DUMMYPOST");
        }
    }

    private void addDummyCategories(int amount, String categoryPrefix) {
        for (int i = 0; i < amount; i++) {
            ResearchCategories.registerCategory(
                    categoryPrefix + i,
                    new ResourceLocation("thaumcraft", "textures/items/thaumonomiconcheat.png"),
                    new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
        }
    }
}
