package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.ContainerBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiBase
        extends GuiContainer
{
    public static final ResourceLocation MC_BLOCK_SHEET = new ResourceLocation("textures/atlas/blocks.png");
    public static final ResourceLocation EMPTY_GUI = new ResourceLocation(MegaCorpMod.modId, "textures/gui/empty.png");
    public static final int FONT_COLOUR = 0x404040;


    public GuiBase(ContainerBase inventorySlotsIn)
    {
        super(inventorySlotsIn);

        inventorySlotsIn.setGuiListener(new Runnable()
        {
            @Override
            public void run()
            {
                onContainerRefresh();
            }
        });
    }

    protected void onContainerRefresh()
    {

    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        for(Gui control : this.controls)
        {
            if(control instanceof GuiTextField)
            {
                ((GuiTextField)control).updateCursorCounter();
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        for (Gui control : this.controls) {
            if(control instanceof GuiButton)
            {
                if(state == 0)
                {
                    ((GuiButton) control).mouseReleased(mouseX, mouseY);
                }
            }

        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        for (Gui control : this.controls) {
            if (control instanceof GuiTextField) {
                GuiTextField textField = (GuiTextField)control;
                boolean wasFocused = textField.isFocused();
                textField.mouseClicked(mouseX, mouseY, mouseButton);
                if(wasFocused && !textField.isFocused())
                {
                    this.textFieldLostFocus(textField);
                }
            }
            else if(control instanceof GuiButton)
            {
                if(mouseButton == 0)
                {
                    if(((GuiButton) control).mousePressed(this.mc, mouseX, mouseY))
                    {
                        ((GuiButton)control).playPressSound(this.mc.getSoundHandler());
                        this.actionPerformed((GuiButton)control);

                    }
                }
            }

        }

        Slot slot = this.getSlotUnderMouse();

        if(slot != null)
        {

        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        boolean textFocused = false;

        for (Gui control : this.controls) {
            if (control instanceof GuiTextField) {
                GuiTextField textField = (GuiTextField) control;
                if (textField.isFocused()) {
                    textFocused = true;
                    textField.textboxKeyTyped(typedChar, keyCode);

                    if(keyCode == Keyboard.KEY_ESCAPE)
                    {
                        textField.setFocused(false);
                        this.textFieldLostFocus(textField);
                    }

                    break;
                }
            }
        }

        if (!textFocused)
            super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String title = getTitle();
        if(title != null)
        {
            this.fontRenderer.drawString(title, 6, 6, FONT_COLOUR);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(-guiLeft, -guiTop, 0);

        for (Gui control : this.controls) {
            if (control instanceof GuiTextField) {
                ((GuiTextField)control).drawTextBox();
            } else if (control instanceof GuiButton) {
                GlStateManager.color(1, 1, 1, 1);
                ((GuiButton)control).drawButton(this.mc, mouseX, mouseY, 0f);
            }
        }

        GlStateManager.popMatrix();

        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    protected final List<Gui> controls = new ArrayList<>();

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    protected void addControl(Gui control)
    {
        this.controls.add(control);
    }

    public void drawFluid(int x, int y, FluidStack fluid, int width, int height) {

        if (fluid == null) {
            return;
        }
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        bindTexture(MC_BLOCK_SHEET);
        int color = fluid.getFluid().getColor(fluid);
        setGLColorFromInt(color);

        drawTiledTexture(x, y, getTexture(fluid.getFluid().getStill(fluid)), width, height);
        GL11.glPopMatrix();
    }

    protected static void bindTexture(ResourceLocation resource)
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(resource);
    }

    protected static void setGLColorFromInt(int color) {

        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        GlStateManager.color(red, green, blue, 1.0F);
    }

    protected void drawTiledTexture(int x, int y, TextureAtlasSprite icon, int width, int height) {

        int i;
        int j;

        int drawHeight;
        int drawWidth;

        for (i = 0; i < width; i += 16) {
            for (j = 0; j < height; j += 16) {
                drawWidth = Math.min(width - i, 16);
                drawHeight = Math.min(height - j, 16);
                drawScaledTexturedModelRectFromIcon(x + i, y + j, icon, drawWidth, drawHeight);
            }
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected void drawScaledTexturedModelRectFromIcon(int x, int y, TextureAtlasSprite icon, int width, int height) {

        if (icon == null) {
            return;
        }
        double minU = icon.getMinU();
        double maxU = icon.getMaxU();
        double minV = icon.getMinV();
        double maxV = icon.getMaxV();

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, this.zLevel).tex(minU, minV + (maxV - minV) * height / 16F).endVertex();
        buffer.pos(x + width, y + height, this.zLevel).tex(minU + (maxU - minU) * width / 16F, minV + (maxV - minV) * height / 16F).endVertex();
        buffer.pos(x + width, y, this.zLevel).tex(minU + (maxU - minU) * width / 16F, minV).endVertex();
        buffer.pos(x, y, this.zLevel).tex(minU, minV).endVertex();
        Tessellator.getInstance().draw();
    }

    protected static TextureAtlasSprite getTexture(ResourceLocation location) {

        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
    }

    protected GuiButtonImage makeGuiButtonImage(int buttonId, int x, int y, int width, int height, int xTex, int yTex, int yDiff, ResourceLocation resourceLocation)
    {
        return new GuiButtonImage(buttonId, x, y, width, height, xTex, yTex, yDiff, resourceLocation);
    }

    protected GuiButtonImage makeGuiButtonImage(int buttonId, int x, int y, int width, int height, int xTex, int yTex, ResourceLocation resourceLocation)
    {
        return new GuiButtonImage(buttonId, x, y, width, height, xTex, yTex, 0, resourceLocation);
    }

    protected void drawInsertCardBackground()
    {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(EMPTY_GUI);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    protected void drawInsertCardForeground()
    {
        String message = new TextComponentTranslation("tile.megacorp:misc.insertcard").getUnformattedText();

        drawCenteredWrappedString(message, 88, 45, 154);
    }

    protected String getTitle()
    {
        return null;
    }

    protected void drawCenteredWrappedString(String message, int x, int y, int wrapWidth)
    {
        List<String> lines = this.fontRenderer.listFormattedStringToWidth(message, wrapWidth);
        int height = 10 * lines.size();
        int ty = y - height / 2;

        for(int i = 0; i < lines.size(); i++)
        {
            int w = this.fontRenderer.getStringWidth(lines.get(i));
            this.fontRenderer.drawString(lines.get(i), x - w / 2, ty, FONT_COLOUR);
            ty += 10;
        }
    }

    protected void textFieldLostFocus(GuiTextField textField)
    {

    }

}
