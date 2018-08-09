package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.profit_materializer.ContainerProfitMaterializer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;

import java.text.NumberFormat;

public class GuiProfitMaterializer
    extends GuiContainerBase
{
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;

    public static final int FONT_COLOUR = 0x404040;

    private static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/profit_materializer.png");

    private final ContainerProfitMaterializer container;

    private GuiFluid fluid;
    private GuiTranslatedLabel profitRemainingTitleLabel;
    private GuiTranslatedLabel profitRemainingLabel;

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if(container.owner == null && fluid.isVisible())
        {
            fluid.setVisible(false);
            profitRemainingLabel.setVisible(false);
            profitRemainingTitleLabel.setVisible(false);
        }
        else if(container.owner != null && !fluid.isVisible())
        {
            fluid.setVisible(true);
            profitRemainingLabel.setVisible(true);
            profitRemainingTitleLabel.setVisible(true);
        }

        if(fluid.isVisible())
        {
            fluid.setAmount(container.fluidAmount);
            fluid.setCapacity(container.fluidCapacity);
            fluid.setFluid(FluidRegistry.getFluid(container.fluid));
        }

        if(profitRemainingLabel.isVisible())
        {
            profitRemainingLabel.setPlaceholder(0, NumberFormat.getIntegerInstance().format(container.profitRemaining));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        if(container.owner == null)
        {
            this.drawInsertCardForeground();
        }
        else
        {

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

        fluid = new GuiFluid(26, 21, 43, 52);
        fluid.setGradEnabled(true);

        this.addControl(fluid);

        profitRemainingTitleLabel = new GuiTranslatedLabel(74, 22, "tile.megacorp:profit_materializer.remaining");
        profitRemainingLabel = new GuiTranslatedLabel(74, 31 , "tile.megacorp:profit_materializer.remainingval", "");

        this.addControl(profitRemainingLabel);
        this.addControl(profitRemainingTitleLabel);

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


    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY)
    {
        /*if(GuiUtil.inBounds(mouseX, mouseY, guiLeft + 66, guiTop + 21, 43, 52))
        {
            List<String> items = new ArrayList<>();

            items.add(FluidRegistry.getFluid(container.fluid).getLocalizedName(null));

            StringBuilder sb = new StringBuilder();
            sb.append(NumberFormat.getIntegerInstance().format(container.fluidAmount));
            sb.append("/");
            sb.append(NumberFormat.getIntegerInstance().format(container.fluidCapacity));
            sb.append("mb");
            items.add(sb.toString());

            this.drawHoveringText(items, mouseX, mouseY);
        }
        else*/
        {
            super.renderHoveredToolTip(mouseX, mouseY);
        }
    }

}
