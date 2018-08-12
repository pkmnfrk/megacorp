package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.gui.control.GuiLabel;
import com.mike_caron.megacorp.gui.control.GuiMultilineLabel;
import com.mike_caron.megacorp.gui.control.GuiSized;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class GuiUtil
{
    private GuiUtil() {}

    public static final ResourceLocation MC_BLOCK_SHEET = new ResourceLocation("textures/atlas/blocks.png");
    public static final ResourceLocation EMPTY_GUI = new ResourceLocation(MegaCorpMod.modId, "textures/gui/empty.png");
    public static final ResourceLocation MISC_RESOURCES = new ResourceLocation(MegaCorpMod.modId, "textures/gui/misc.png");

    public static final int FONT_COLOUR = 0x404040;

    public static void bindTexture(ResourceLocation resource)
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(resource);
    }

    public static void setGLColorFromInt(int color) {

        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        GlStateManager.color(red, green, blue, 1.0F);
    }

    public static TextureAtlasSprite getTexture(ResourceLocation location) {

        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
    }

    public static boolean inBounds(int mouseX, int mouseY, GuiButton button)
    {
        return inBounds(mouseX, mouseY, button.x, button.y, button.width, button.height);
    }

    public static boolean inBounds(int mouseX, int mouseY, GuiSized button)
    {
        return inBounds(mouseX, mouseY, button.getX(), button.getY(), button.getWidth(), button.getHeight());
    }

    public static boolean inBounds(int mouseX, int mouseY, int bx, int by, int bw, int bh)
    {
        if(mouseX >= bx && mouseX < bx + bw && mouseY >= by && mouseY < by + bh)
        {
            return true;
        }

        return false;
    }

    public static void drawFluid(int x, int y, FluidStack fluid, int width, int height) {

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

    protected static void drawTiledTexture(int x, int y, TextureAtlasSprite icon, int width, int height) {

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

    protected static void drawScaledTexturedModelRectFromIcon(int x, int y, TextureAtlasSprite icon, int width, int height) {

        if (icon == null) {
            return;
        }
        double minU = icon.getMinU();
        double maxU = icon.getMaxU();
        double minV = icon.getMinV();
        double maxV = icon.getMaxV();
        float zLevel = 0f;

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, zLevel).tex(minU, minV + (maxV - minV) * height / 16F).endVertex();
        buffer.pos(x + width, y + height, zLevel).tex(minU + (maxU - minU) * width / 16F, minV + (maxV - minV) * height / 16F).endVertex();
        buffer.pos(x + width, y, zLevel).tex(minU + (maxU - minU) * width / 16F, minV).endVertex();
        buffer.pos(x, y, zLevel).tex(minU, minV).endVertex();
        Tessellator.getInstance().draw();
    }

    public static void drawTiledTexturePart(int x, int y, int width, int height, int tileX, int tileY, int tileWidth, int tileHeight, int sheetWidth, int sheetHeight)
    {
        float minU = tileX * 1f / sheetWidth;
        float minV = tileY * 1f / sheetHeight;

        int drawHeight;
        int drawWidth;

        for (int i = 0; i < width; i += tileWidth) {
            for (int j = 0; j < height; j += tileHeight) {       //  0    2
                drawWidth = Math.min(width - i, tileWidth);      //  v  / v
                drawHeight = Math.min(height - j, tileHeight);   //  1    3

                BufferBuilder buffer = Tessellator.getInstance().getBuffer();
                buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);
                buffer.pos(x + i, y + j, 0).tex(minU, minV).endVertex();
                buffer.pos(x + i, y + j + drawHeight, 0).tex(minU, (tileY + drawHeight) * 1f / sheetHeight).endVertex();
                buffer.pos(x + i + drawWidth, y + j, 0).tex((tileX + drawWidth) * 1f / sheetWidth, minV).endVertex();
                buffer.pos(x + i + drawWidth, y + j + drawHeight, 0).tex((tileX + drawWidth) * 1f / sheetWidth, (tileY + drawHeight) * 1f / sheetHeight).endVertex();
                Tessellator.getInstance().draw();
            }
        }


    }

    public static GuiButtonImage makeGuiButtonImage(int buttonId, int x, int y, int width, int height, int xTex, int yTex, int yDiff, ResourceLocation resourceLocation)
    {
        return new GuiButtonImage(buttonId, x, y, width, height, xTex, yTex, yDiff, resourceLocation);
    }

    public static GuiButtonImage makeGuiButtonImage(int buttonId, int x, int y, int width, int height, int xTex, int yTex, ResourceLocation resourceLocation)
    {
        return new GuiButtonImage(buttonId, x, y, width, height, xTex, yTex, 0, resourceLocation);
    }

    public static GuiLabel staticLabelFromTranslationKey(int x, int y, String key, Object ... placeholders)
    {
        String message = new TextComponentTranslation(key, placeholders).getFormattedText();
        return new GuiLabel(x, y, message);
    }

    public static GuiMultilineLabel staticMultilineLabelFromTranslationKey(int x, int y, int width, int height, String key, Object ... placeholders)
    {
        String message = new TextComponentTranslation(key, placeholders).getFormattedText();
        return new GuiMultilineLabel(x, y, width, height, message);
    }

    public static void drawDebugFlatRectangle(int x, int y, int width, int height)
    {
        bindTexture(MISC_RESOURCES);
        float minU = 14f / 256f;
        float maxU = 15f / 256f;
        float minV = 3 / 256f;
        float maxV = 4 / 256f;

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y, 0).tex(minU, minV).endVertex();
        buffer.pos(x, y + height, 0).tex(minU, maxV).endVertex();
        buffer.pos(x + width, y, 0).tex(maxU, minV).endVertex();
        buffer.pos(x + width, y + height, 0).tex(maxU, maxV).endVertex();
        Tessellator.getInstance().draw();
    }

    public static void draw3x3(int x, int y, int width, int height, int sx, int sy)
    {
        //top-left
        drawTiledTexturePart(x, y, 4, 4, sx, sy, 4, 4, 256, 256);
        //top-right
        drawTiledTexturePart(x + width - 4, y, 4, 4, sx + 20, sy, 4, 4, 256, 256);
        //bottom-left
        drawTiledTexturePart(x, y + height - 4, 4, 4, sx, sy + 20, 4, 4, 256, 256);
        //bottom-right
        drawTiledTexturePart(x + width - 4, y + height - 4, 4, 4, sx + 20, sy + 20, 4, 4, 256, 256);
        //top
        drawTiledTexturePart(x + 4, y, width - 8, 4, sx + 4, sy, 16, 4, 256, 256);
        //bottom
        drawTiledTexturePart(x + 4, y + height - 4, width - 8, 4, sx + 4, sy + 20, 16, 4, 256, 256);
        //left
        drawTiledTexturePart(x, y + 4, 4, height - 8, sx, sy + 4, 4, 16, 256, 256);
        //right
        drawTiledTexturePart(x + width - 4, y + 4, 4, height - 8, sx + 20, sy + 4, 4, 16, 256, 256);
        //center
        drawTiledTexturePart(x + 4, y + 4, width - 8, height - 8, sx + 4, sy + 4, 16, 16, 256, 256);
    }
}
