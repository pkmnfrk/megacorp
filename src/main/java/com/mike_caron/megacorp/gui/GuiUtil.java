package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.gui.control.GuiButton;
import com.mike_caron.megacorp.gui.control.GuiControl;
import com.mike_caron.megacorp.gui.control.GuiLabel;
import com.mike_caron.megacorp.gui.control.GuiMultilineLabel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.Color;

public class GuiUtil
{
    private GuiUtil() {}

    public static final ResourceLocation MC_BLOCK_SHEET = new ResourceLocation("textures/atlas/blocks.png");
    public static final ResourceLocation EMPTY_GUI = new ResourceLocation(MegaCorpMod.modId, "textures/gui/empty.png");
    public static final ResourceLocation MISC_RESOURCES = new ResourceLocation(MegaCorpMod.modId, "textures/gui/misc.png");

    public static final Color FONT_COLOUR = new Color(0x404040);

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

    public static void setGLColor(Color color)
    {
        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    public static TextureAtlasSprite getTexture(ResourceLocation location) {

        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
    }

    public static boolean inBounds(int mouseX, int mouseY, GuiControl button)
    {
        return inBounds(mouseX, mouseY, button.getX(), button.getY(), button.getWidth(), button.getHeight());
    }

    public static boolean inBoundsThis(int mouseX, int mouseY, GuiControl control)
    {
        return inBounds(mouseX, mouseY, 0, 0, control.getWidth(), control.getHeight());
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

    public static void drawStretchedTexturePart(float x, float y, int width, int height, int tileX, int tileY, int tileWidth, int tileHeight, int sheetWidth, int sheetHeight)
    {
        float minU = tileX * 1f / sheetWidth;
        float minV = tileY * 1f / sheetHeight;
        float maxU = (tileX + tileWidth) * 1f / sheetWidth;
        float maxV = (tileY + tileHeight) * 1f / sheetHeight;

        //  0    2
        //  v  / v
        //  1    3

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y, 0).tex(minU, minV).endVertex();
        buffer.pos(x, y + height, 0).tex(minU, maxV).endVertex();
        buffer.pos(x + width, y, 0).tex(maxU, minV).endVertex();
        buffer.pos(x + width, y + height, 0).tex(maxU, maxV).endVertex();
        Tessellator.getInstance().draw();
    }

    public static void drawTexturePart(float x, float y, int width, int height, int tileX, int tileY, int sheetWidth, int sheetHeight)
    {
        float minU = tileX * 1f / sheetWidth;
        float minV = tileY * 1f / sheetHeight;
        float maxU = (tileX + width) * 1f / sheetWidth;
        float maxV = (tileY + height) * 1f / sheetHeight;

        //  0    2
        //  v  / v
        //  1    3

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y, 0).tex(minU, minV).endVertex();
        buffer.pos(x, y + height, 0).tex(minU, maxV).endVertex();
        buffer.pos(x + width, y, 0).tex(maxU, minV).endVertex();
        buffer.pos(x + width, y + height, 0).tex(maxU, maxV).endVertex();
        Tessellator.getInstance().draw();
    }

    public static GuiButtonImage makeGuiButtonImage(int buttonId, int x, int y, int width, int height, int xTex, int yTex, int yDiff, ResourceLocation resourceLocation)
    {
        return new GuiButtonImage(buttonId, x, y, width, height, xTex, yTex, yDiff, resourceLocation);
    }

    public static GuiButtonImage makeGuiButtonImage(int buttonId, int x, int y, int width, int height, int xTex, int yTex, ResourceLocation resourceLocation)
    {
        return new GuiButtonImage(buttonId, x, y, width, height, xTex, yTex, 0, resourceLocation);
    }

    public static GuiButton translatedButton(int buttonId, int x, int y, int width, int height, String key, Object... replacements)
    {
        String translated = new TextComponentTranslation(key, replacements).getFormattedText();

        return new GuiButton(buttonId, x, y, width, height, translated);
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
        final int CORNER_SIZE = 4;
        final int CENTER_SIZE = 8;

        draw3x3(x, y, width, height, sx, sy, CORNER_SIZE, CENTER_SIZE);
    }

    public static void draw3x3(int x, int y, int width, int height, int sx, int sy, int cornerSize, int centerSize)
    {
        final int SHEET_SIZE = 256;
        final int TILE_SIZE = cornerSize * 2 + centerSize;

        //top-left
        drawTiledTexturePart(x, y, cornerSize, cornerSize, sx, sy, cornerSize, cornerSize, SHEET_SIZE, SHEET_SIZE);
        //top-right
        drawTiledTexturePart(x + width - cornerSize, y, cornerSize, cornerSize, sx + TILE_SIZE - cornerSize, sy, cornerSize, cornerSize, SHEET_SIZE, SHEET_SIZE);
        //bottom-left
        drawTiledTexturePart(x, y + height - cornerSize, cornerSize, cornerSize, sx, sy + TILE_SIZE - cornerSize, cornerSize, cornerSize, SHEET_SIZE, SHEET_SIZE);
        //bottom-right
        drawTiledTexturePart(x + width - cornerSize, y + height - cornerSize, cornerSize, cornerSize, sx + TILE_SIZE - cornerSize, sy + TILE_SIZE - cornerSize, cornerSize, cornerSize, SHEET_SIZE, SHEET_SIZE);
        //top
        drawTiledTexturePart(x + cornerSize, y, width - cornerSize * 2, cornerSize, sx + cornerSize, sy, centerSize, cornerSize, SHEET_SIZE, SHEET_SIZE);
        //bottom
        drawTiledTexturePart(x + cornerSize, y + height - cornerSize, width - cornerSize * 2, cornerSize, sx + cornerSize, sy + TILE_SIZE - cornerSize, centerSize, cornerSize, SHEET_SIZE, SHEET_SIZE);
        //left
        drawTiledTexturePart(x, y + cornerSize, cornerSize, height - cornerSize * 2, sx, sy + cornerSize, cornerSize, centerSize, SHEET_SIZE, SHEET_SIZE);
        //right
        drawTiledTexturePart(x + width - cornerSize, y + cornerSize, cornerSize, height - cornerSize * 2, sx + TILE_SIZE - cornerSize, sy + cornerSize, cornerSize, centerSize, SHEET_SIZE, SHEET_SIZE);
        //center
        drawTiledTexturePart(x + cornerSize, y + cornerSize, width - cornerSize * 2, height - cornerSize * 2, sx + cornerSize, sy + cornerSize, centerSize, centerSize, SHEET_SIZE, SHEET_SIZE);
    }

    public static void draw3x3Stretched(int x, int y, int width, int height, int sx, int sy)
    {
        final int CORNER_SIZE = 4;
        final int CENTER_SIZE = 8;

        draw3x3(x, y, width, height, sx, sy, CORNER_SIZE, CENTER_SIZE);
    }

    public static void draw3x3Stretched(int x, int y, int width, int height, int sx, int sy, int cornerSize, int centerSize)
    {
        final int SHEET_SIZE = 256;
        final int TILE_SIZE = cornerSize * 2 + centerSize;

        //top-left
        drawStretchedTexturePart(x, y, cornerSize, cornerSize, sx, sy, cornerSize, cornerSize, SHEET_SIZE, SHEET_SIZE);
        //top-right
        drawStretchedTexturePart(x + width - cornerSize, y, cornerSize, cornerSize, sx + TILE_SIZE - cornerSize, sy, cornerSize, cornerSize, SHEET_SIZE, SHEET_SIZE);
        //bottom-left
        drawStretchedTexturePart(x, y + height - cornerSize, cornerSize, cornerSize, sx, sy + TILE_SIZE - cornerSize, cornerSize, cornerSize, SHEET_SIZE, SHEET_SIZE);
        //bottom-right
        drawStretchedTexturePart(x + width - cornerSize, y + height - cornerSize, cornerSize, cornerSize, sx + TILE_SIZE - cornerSize, sy + TILE_SIZE - cornerSize, cornerSize, cornerSize, SHEET_SIZE, SHEET_SIZE);
        //top
        drawStretchedTexturePart(x + cornerSize, y, width - cornerSize * 2, cornerSize, sx + cornerSize, sy, centerSize, cornerSize, SHEET_SIZE, SHEET_SIZE);
        //bottom
        drawStretchedTexturePart(x + cornerSize, y + height - cornerSize, width - cornerSize * 2, cornerSize, sx + cornerSize, sy + TILE_SIZE - cornerSize, centerSize, cornerSize, SHEET_SIZE, SHEET_SIZE);
        //left
        drawStretchedTexturePart(x, y + cornerSize, cornerSize, height - cornerSize * 2, sx, sy + cornerSize, cornerSize, centerSize, SHEET_SIZE, SHEET_SIZE);
        //right
        drawStretchedTexturePart(x + width - cornerSize, y + cornerSize, cornerSize, height - cornerSize * 2, sx + TILE_SIZE - cornerSize, sy + cornerSize, cornerSize, centerSize, SHEET_SIZE, SHEET_SIZE);
        //center
        drawStretchedTexturePart(x + cornerSize, y + cornerSize, width - cornerSize * 2, height - cornerSize * 2, sx + cornerSize, sy + cornerSize, centerSize, centerSize, SHEET_SIZE, SHEET_SIZE);
    }

    public static String translate(String key, Object... args)
    {
        return new TextComponentTranslation(key, args).getFormattedText();
    }

    public static String translateConditional(boolean condition, String trueKey, String falseKey, Object... args)
    {
        return new TextComponentTranslation(condition ? trueKey : falseKey, args).getFormattedText();
    }

    public static int getRealX(int x)
    {
        final Minecraft mc = Minecraft.getMinecraft();
        final ScaledResolution scaledresolution = new ScaledResolution(mc);

        return (int)(x / scaledresolution.getScaledWidth_double() * mc.displayWidth);
    }

    public static int getRealWidth(int x)
    {
        final Minecraft mc = Minecraft.getMinecraft();
        final ScaledResolution scaledresolution = new ScaledResolution(mc);

        return (int)(x / scaledresolution.getScaledWidth_double() * mc.displayWidth);
    }

    public static int getRealY(int y)
    {
        final Minecraft mc = Minecraft.getMinecraft();
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        return (int)(mc.displayHeight - (y / scaledresolution.getScaledHeight_double() * mc.displayHeight));
    }

    public static int getRealHeight(int y)
    {
        final Minecraft mc = Minecraft.getMinecraft();
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        return (int)(y / scaledresolution.getScaledHeight_double() * mc.displayHeight);
    }

    public static String i18n(String key, Object ... variables)
    {
        return new TextComponentTranslation(key, variables).getFormattedText();
    }

    public static void drawItemStack(ItemStack stack, int x, int y, RenderItem itemRender, @Nullable FontRenderer fontRenderer)
    {
        GlStateManager.translate(0.0F, 0.0F, 32.0F);

        itemRender.zLevel = 100.0F;

        GlStateManager.translate(0.0F, 0.0F, 32.0F);

        RenderHelper.enableGUIStandardItemLighting();

        itemRender.renderItemIntoGUI(stack, x, y);
        if(fontRenderer != null)
        {
            itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, x, y, null);
        }

        RenderHelper.disableStandardItemLighting();

        itemRender.zLevel = 0.0F;
    }
}
