package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.profit_materializer.ContainerProfitMaterializer;
import com.mike_caron.megacorp.block.profit_materializer.TileEntityProfitMaterializer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class GuiProfitMaterializer
    extends GuiBase
{
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;

    public static final int FONT_COLOUR = 0x404040;

    private static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/profit_materializer.png");

    private final TileEntityProfitMaterializer te;
    private final ContainerProfitMaterializer container;

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        if(te.getOwner() == null)
        {
            this.drawInsertCardForeground();
        }
        else
        {

        }

    }

    @Override
    protected String getTitle()
    {
        return new TextComponentTranslation("tile.megacorp:profit_materializer.name", new Object[0]).getUnformattedText();
    }

    public GuiProfitMaterializer(TileEntityProfitMaterializer te, ContainerProfitMaterializer container)
    {
        super(container);

        xSize = WIDTH;
        ySize = HEIGHT;

        this.te = te;
        this.container = container;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i1)
    {
        if(te.getOwner() == null)
        {
            drawInsertCardBackground();
        }
        else
        {
            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(background);
            drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

            int scaledHeight = getScaled(52);

            this.drawFluid(guiLeft + 66, guiTop + 21 + (52 - scaledHeight), te.fluidTank.getFluid(), 43, scaledHeight);
        }
    }

    private int getScaled(int height)
    {
        int ret = (int)Math.floor(((double)te.fluidTank.getFluidAmount()) / te.fluidTank.getCapacity() * height);
        if(ret == 0 && te.fluidTank.getFluidAmount() > 0)
            return 1;
        return ret;
    }
}
