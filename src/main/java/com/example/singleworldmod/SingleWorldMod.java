package com.example.singleworldmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = SingleWorldMod.MODID, name = SingleWorldMod.NAME, version = SingleWorldMod.VERSION,
        clientSideOnly = true)
public class SingleWorldMod {

    public static final String MODID = "singleworldmod";
    public static final String NAME = "Single World Mod";
    public static final String VERSION = "1.0.0";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }
}
