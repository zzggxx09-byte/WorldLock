package com.example.singleworldmod;

import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEventHandler {

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        GuiScreen gui = event.getGui();
        if (gui instanceof GuiCreateWorld) {
            GuiScreen parent = getParentScreen((GuiCreateWorld) gui);
            event.setGui(new GuiCreateWorldCustom(parent));
        }
    }

    private GuiScreen getParentScreen(GuiCreateWorld gui) {
        try {
            java.lang.reflect.Field f = GuiCreateWorld.class.getDeclaredField("parentScreen");
            f.setAccessible(true);
            return (GuiScreen) f.get(gui);
        } catch (Exception e) {
            return null;
        }
    }
}
