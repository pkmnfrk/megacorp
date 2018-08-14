package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.sbs.ContainerSBS;
import com.mike_caron.megacorp.gui.control.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiSBS
    extends GuiContainerBase
    implements GuiButton.ClickedListener
{
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;

    private static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/sbs.png");

    private final ContainerSBS container;

    private GuiGroup ownedGroup = new GuiGroup();

    private GuiFluid money = new GuiFluid(97, 22, 44, 53);


    public GuiSBS(ContainerSBS container)
    {
        super(container);

        xSize = WIDTH;
        ySize = HEIGHT;

        this.container = container;

        initControls();
    }

    @Override
    protected void onContainerRefresh()
    {
        //update fluid
    }

    @Override
    public void addControls()
    {
        super.addControls();

        this.addControl(ownedGroup);

        ownedGroup.addControl(money);
    }

    @Override
    protected String getTitleKey()
    {
        return "tile.megacorp:small_business_simulator.name";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i1)
    {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    public void clicked(GuiButton.ClickedEvent event)
    {
        switch (event.id)
        {
            case 1:

                break;
        }
    }
}
