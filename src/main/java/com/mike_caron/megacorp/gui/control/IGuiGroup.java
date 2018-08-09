package com.mike_caron.megacorp.gui.control;

import net.minecraft.client.gui.FontRenderer;

public interface IGuiGroup
{
    void addControl(GuiControl control);
    void removeControl(GuiControl control);
    FontRenderer getFontRenderer();
}
