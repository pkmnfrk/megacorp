package com.mike_caron.megacorp.gui.control;

import net.minecraft.client.gui.FontRenderer;

import javax.annotation.Nullable;

public interface IGuiGroup
{
    void addControl(GuiControl control);
    void removeControl(GuiControl control);
    void clearControls();
    FontRenderer getFontRenderer();
    boolean notifyTakeFocus(GuiControl taker);
    @Nullable
    GuiControl hitTest(int x, int y);
    void sort();
    int translateToScreenX(int x);
    int translateToScreenY(int y);
    int translateFromScreenX(int x);
    int translateFromScreenY(int y);
}
