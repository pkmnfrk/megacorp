package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.profit_materializer.ContainerProfitMaterializer;
import com.mike_caron.megacorp.gui.control.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;

import java.text.NumberFormat;

public class GuiProfitMaterializer
    extends GuiContainerOwnedBase
{
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;

    public static final int FONT_COLOUR = 0x404040;

    private static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/profit_materializer.png");

    private final ContainerProfitMaterializer container;

    private GuiGroup activeGroup;
    private GuiFluid fluid;
    private GuiLabel profitRemainingTitleLabel;
    private GuiTranslatedLabel profitRemainingLabel;

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if(container.owner == null && fluid.isVisible())
        {
            activeGroup.setVisible(false);
            insertCardLabel.setVisible(true);
        }
        else if(container.owner != null && !fluid.isVisible())
        {
            activeGroup.setVisible(true);
            insertCardLabel.setVisible(false);

        }

        if(activeGroup.isVisible())
        {
            fluid.setAmount(container.fluidAmount);
            fluid.setCapacity(container.fluidCapacity);
            fluid.setFluid(FluidRegistry.getFluid(container.fluid));

            profitRemainingLabel.setPlaceholder(0, NumberFormat.getIntegerInstance().format(container.profitRemaining));
        }
    }

    @Override
    protected String getTitleKey()
    {
        return "tile.megacorp:profit_materializer.name";
    }

    public GuiProfitMaterializer(ContainerProfitMaterializer container)
    {
        super(container);

        xSize = WIDTH;
        ySize = HEIGHT;

        this.container = container;

        initControls();
    }

    @Override
    protected void addControls()
    {
        super.addControls();

        activeGroup = new GuiGroup();

        fluid = new GuiFluid(26, 21, 43, 52);
        fluid.setGradEnabled(true);

        profitRemainingTitleLabel = GuiUtil.staticLabelFromTranslationKey(74, 22, "tile.megacorp:profit_materializer.remaining");
        profitRemainingLabel = new GuiTranslatedLabel(74, 31 , "tile.megacorp:profit_materializer.remainingval", "");

        activeGroup.addControl(fluid);
        activeGroup.addControl(profitRemainingLabel);
        activeGroup.addControl(profitRemainingTitleLabel);

        this.addControl(activeGroup);

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i1)
    {
        if(container.owner == null)
        {
            drawInsertCardBackground();
        }
        else
        {
            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(background);
            drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        }
    }

}
