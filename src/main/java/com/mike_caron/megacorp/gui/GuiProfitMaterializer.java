package com.mike_caron.megacorp.gui;

import com.mike_caron.mikesmodslib.gui.GuiUtil;
import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.profit_materializer.ContainerProfitMaterializer;
import com.mike_caron.mikesmodslib.gui.GuiFluid;
import com.mike_caron.mikesmodslib.gui.GuiGroup;
import com.mike_caron.mikesmodslib.gui.GuiLabel;
import com.mike_caron.mikesmodslib.gui.GuiTranslatedLabel;
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
    private GuiFluid fluid = new GuiFluid(26, 21, 43, 52);
    private GuiLabel profitRemainingTitleLabel = GuiUtil.staticLabelFromTranslationKey(74, 22, "tile.megacorp:profit_materializer.remaining");
    private GuiTranslatedLabel profitRemainingLabel = new GuiTranslatedLabel(74, 31 , "tile.megacorp:profit_materializer.remainingval", "");
    private GuiLabel speedTitleLabel = GuiUtil.staticLabelFromTranslationKey(74,45, "tile.megacorp:profit_materializer.speed");
    private GuiTranslatedLabel speedLabel = new GuiTranslatedLabel(74, 56, "tile.megacorp:profit_materializer.speedval", "", "");

    @Override
    protected void onContainerRefresh()
    {
        super.onContainerRefresh();

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
            speedLabel.setPlaceholder(0, NumberFormat.getNumberInstance().format(container.speed));
        }
    }

    @Override
    protected String getTitleKey()
    {
        return "tile.megacorp:profit_materializer.name";
    }

    public GuiProfitMaterializer(ContainerProfitMaterializer container)
    {
        super(container, WIDTH, HEIGHT);

        this.container = container;

        initControls();
    }

    @Override
    protected void addControls()
    {
        super.addControls();

        activeGroup = new GuiGroup();

        fluid.setGradEnabled(true);

        activeGroup.addControl(fluid);
        activeGroup.addControl(profitRemainingLabel);
        activeGroup.addControl(profitRemainingTitleLabel);
        activeGroup.addControl(speedTitleLabel);
        activeGroup.addControl(speedLabel);

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
