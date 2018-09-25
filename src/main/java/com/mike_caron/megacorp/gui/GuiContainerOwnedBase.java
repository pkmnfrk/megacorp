package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.block.ContainerBase;
import com.mike_caron.megacorp.gui.control.GuiContainerBase;
import com.mike_caron.megacorp.gui.control.GuiMultilineLabel;

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

    @Override
    protected void addControls()
    {
        super.addControls();

        insertCardLabel.setAlignment(GuiMultilineLabel.Alignment.CENTER);
        insertCardLabel.setVisible(false);
        this.addControl(insertCardLabel);
    }
}
