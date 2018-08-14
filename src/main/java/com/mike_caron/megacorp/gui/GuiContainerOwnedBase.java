package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.block.ContainerBase;
import com.mike_caron.megacorp.gui.control.GuiContainerBase;
import com.mike_caron.megacorp.gui.control.GuiMultilineLabel;

public abstract class GuiContainerOwnedBase
    extends GuiContainerBase
{

    protected GuiMultilineLabel insertCardLabel = GuiUtil.staticMultilineLabelFromTranslationKey(8, 16, 160, 53,"tile.megacorp:misc.insertcard");

    public GuiContainerOwnedBase(ContainerBase inventorySlotsIn)
    {
        super(inventorySlotsIn);
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
