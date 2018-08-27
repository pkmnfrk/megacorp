package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.uplink.ContainerUplink;
import com.mike_caron.megacorp.gui.control.GuiContainerBase;
import com.mike_caron.megacorp.gui.control.GuiList;
import net.minecraft.util.ResourceLocation;

public class GuiTest
    extends GuiContainerBase
{
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;

    private static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/uplink.png");

    private GuiList list = new GuiList(10, 20, WIDTH - 20, 60);

    private boolean goingLeft = false;
    private boolean goingUp = false;

    public GuiTest(ContainerUplink container)
    {
        super(container);

        initControls();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

    }

    @Override
    protected void addControls()
    {
        super.addControls();

        this.addControl(list);

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i1)
    {
        drawInsertCardBackground();
    }



}
