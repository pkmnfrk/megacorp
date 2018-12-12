package com.mike_caron.megacorp.gui;

import com.mike_caron.mikesmodslib.block.ContainerBase;
import com.mike_caron.mikesmodslib.gui.GuiContainerBase;
import com.mike_caron.mikesmodslib.gui.GuiMultilineLabel;
import com.mike_caron.mikesmodslib.gui.GuiUtil;

import java.awt.Color;

public abstract class GuiContainerOwnedBase
    extends GuiContainerBase
{

    protected GuiMultilineLabel insertCardLabel;

    public GuiContainerOwnedBase(ContainerBase inventorySlotsIn, int width, int height)
    {
        super(inventorySlotsIn, width, height);

        insertCardLabel = GuiUtil.staticMultilineLabelFromTranslationKey(8, 16, width - 16, 70,"tile.megacorp:misc.insertcard");
        insertCardLabel.setVeticalAlignment(GuiMultilineLabel.VerticalAlignment.CENTER);

    }

    protected void drawInsertCardBackground()
    {
        GuiUtil.setGLColor(Color.WHITE);
        GuiUtil.bindTexture(Resources.MISC_RESOURCES);
        GuiUtil.draw3x3Stretched(guiLeft, guiTop, xSize, ySize, 48, 16);
        GuiUtil.drawTexturePart(guiLeft + xSize / 2 - 81, guiTop + ySize - 83, 162, 76, 0, 180, 256, 256);
    }

    @Override
    protected void addControls()
    {
        super.addControls();

        insertCardLabel.setAlignment(GuiMultilineLabel.Alignment.CENTER);
        insertCardLabel.setVisible(false);
        this.addControl(insertCardLabel);
    }
}
