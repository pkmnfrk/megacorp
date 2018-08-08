package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.uplink.ContainerUplink;
import com.mike_caron.megacorp.block.uplink.TileEntityUplink;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import java.io.IOException;
import java.text.NumberFormat;

public class GuiUplink
    extends GuiBase
{
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;

    private static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/uplink.png");

    private final TileEntityUplink te;
    private final ContainerUplink container;

    private static ResourceLocation cardImage = new ResourceLocation(MegaCorpMod.modId, "textures/gui/test.png");
    private GuiButtonImage cardButton;
    private GuiTextField corpNameField;
    private GuiButton establishCorporation;

    private String corpName = "Minecorp";


    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if(!button.enabled) return;

        if(button == cardButton)
        {

        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);

        if(corpNameField.isFocused())
        {
            corpName = corpNameField.getText();
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.controls.clear();

        addControl(corpNameField = new GuiTextField(2, this.fontRenderer, guiLeft + 7, guiTop + 29, 156, 10));
        corpNameField.setText(corpName);
        corpNameField.setCanLoseFocus(true);


        addControl(cardButton = makeGuiButtonImage(1, guiLeft + 7, guiTop + 58, 18, 18, 176, 0, 18, background));

    }

    @Override
    protected String getTitle()
    {
        return new TextComponentTranslation("tile.megacorp:uplink.name").getUnformattedText();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        this.fontRenderer.drawString(new TextComponentTranslation("tile.megacorp:uplink.corpname").getUnformattedText(), 6, 19, FONT_COLOUR);

        String prefix = new TextComponentTranslation("tile.megacorp:uplink.profit").getUnformattedText();

        prefix += " $" + NumberFormat.getIntegerInstance().format(123456);

        this.fontRenderer.drawString(prefix, 6, 43, FONT_COLOUR);



    }

    public GuiUplink(TileEntityUplink te, ContainerUplink container)
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
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

    }

}
