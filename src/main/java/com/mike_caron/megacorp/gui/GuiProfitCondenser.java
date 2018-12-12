package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.profit_condenser.ContainerProfitCondenser;
import com.mike_caron.mikesmodslib.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;

public class GuiProfitCondenser
    extends GuiContainerBase
{
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;

    private static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/profit_condenser.png");

    private final ContainerProfitCondenser container;

    private GuiGroup ownedGroup = new GuiGroup();

    private GuiFluid input = new GuiFluid(18, 22, 43, 52);
    private GuiFluid output = new GuiFluid(114, 22, 43, 52);

    private GuiProgressGlyph progress = new GuiProgressGlyph(69, 25, 36, 46, 177, 1, background);


    public GuiProfitCondenser(ContainerProfitCondenser container)
    {
        super(container, WIDTH, HEIGHT);

        this.container = container;

        initControls();
    }

    @Override
    protected void onContainerRefresh()
    {
        //update fluid
        input.setAmount(container.inputFluidAmount);
        input.setCapacity(container.inputFluidCapacity);
        input.setFluid(FluidRegistry.getFluid(container.inputFluid));

        output.setAmount(container.outputFluidAmount);
        output.setCapacity(container.outputFluidCapacity);
        output.setFluid(FluidRegistry.getFluid(container.outputFluid));

        progress.setProgress(container.progress);
    }

    @Override
    public void addControls()
    {
        super.addControls();

        this.addControl(ownedGroup);

        input.setGradEnabled(true);

        ownedGroup.addControl(input);
        ownedGroup.addControl(output);
        ownedGroup.addControl(progress);
    }

    @Override
    protected String getTitleKey()
    {
        return "tile.megacorp:profit_condenser.name";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i1)
    {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

}
