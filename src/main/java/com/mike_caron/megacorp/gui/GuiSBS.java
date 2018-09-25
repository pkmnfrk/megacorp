package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.sbs.ContainerSBS;
import com.mike_caron.megacorp.gui.control.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;

public class GuiSBS
    extends GuiContainerBase
    implements GuiButton.ClickedListener
{
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;

    private static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/sbs.png");

    private final ContainerSBS container;

    private GuiGroup ownedGroup = new GuiGroup();

    private GuiFluid money = new GuiFluid(97, 22, 43, 52);

    private GuiProgressGlyph progress = new GuiProgressGlyph(53, 35, 38, 26, 177, 1, background);


    public GuiSBS(ContainerSBS container)
    {
        super(container, WIDTH, HEIGHT);

        this.container = container;

        initControls();
    }

    @Override
    protected void onContainerRefresh()
    {
        //update fluid
        money.setAmount(container.fluidAmount);
        money.setCapacity(container.fluidCapacity);
        money.setFluid(FluidRegistry.getFluid(container.fluid));
        progress.setProgress(container.progress);
    }

    @Override
    public void addControls()
    {
        super.addControls();

        this.addControl(ownedGroup);

        money.setGradEnabled(true);

        ownedGroup.addControl(money);
        ownedGroup.addControl(progress);
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
