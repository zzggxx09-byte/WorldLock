package com.example.singleworldmod;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.IOException;

public class GuiCreateWorldCustom extends GuiScreen {

    private final GuiScreen parentScreen;
    private GuiTextField worldNameField;
    private GuiButton createButton;
    private String statusMessage = "";
    private boolean working = false;

    public GuiCreateWorldCustom(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        int centerX = this.width / 2;

        this.worldNameField = new GuiTextField(2, this.fontRenderer, centerX - 100, 60, 200, 20);
        this.worldNameField.setMaxStringLength(64);
        this.worldNameField.setText("Новий світ");
        this.worldNameField.setFocused(true);

        this.createButton = new GuiButton(0, centerX - 100, 100, 200, 20, "Створити світ");
        this.addButton(this.createButton);

        this.addButton(new GuiButton(1, centerX - 100, 130, 200, 20, "Скасувати"));
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.worldNameField.isFocused()) {
            this.worldNameField.textboxKeyTyped(typedChar, keyCode);
        }
        if (keyCode == Keyboard.KEY_RETURN) {
            this.actionPerformed(this.createButton);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.worldNameField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (!button.enabled || this.working) {
            return;
        }

        if (button.id == 1) {
            this.mc.displayGuiScreen(this.parentScreen);
            return;
        }

        if (button.id == 0) {
            createWorldFromTemplate();
        }
    }

    private void createWorldFromTemplate() {
        String rawName = this.worldNameField.getText().trim();
        if (rawName.isEmpty()) {
            rawName = "Новий світ";
        }

        String folderName = WorldTemplateUtils.sanitizeFolderName(rawName);
        File savesDir = new File(this.mc.mcDataDir, "saves");
        File targetDir = WorldTemplateUtils.getUniqueSaveFolder(savesDir, folderName);

        this.working = true;
        this.createButton.enabled = false;
        this.statusMessage = "Створення світу...";

        try {
            WorldTemplateUtils.extractTemplateWorld(targetDir);
            WorldTemplateUtils.setLevelName(targetDir, rawName);

            this.mc.launchIntegratedServer(targetDir.getName(), rawName, null);
        } catch (Exception e) {
            e.printStackTrace();
            this.working = false;
            this.createButton.enabled = true;
            this.statusMessage = "Помилка створення світу: " + e.getMessage();
        }
    }

    @Override
    public void updateScreen() {
        this.worldNameField.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "Створення світу", this.width / 2, 20, 0xFFFFFF);
        this.drawString(this.fontRenderer, "Назва світу:", this.width / 2 - 100, 47, 0xA0A0A0);
        this.worldNameField.drawTextBox();

        if (!this.statusMessage.isEmpty()) {
            this.drawCenteredString(this.fontRenderer, this.statusMessage, this.width / 2, 160, 0xFF5555);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
