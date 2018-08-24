package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.util.text.TextComponentTranslation;

import java.awt.*;

public class GuiTranslatedLabel
    extends GuiLabel
{
    protected Object[] placeholders;
    protected String stringKey;

    public GuiTranslatedLabel(int x, int y, String stringKey, Object... placeholders)
    {
        this(x, y, GuiUtil.FONT_COLOUR, stringKey, placeholders);
    }
    public GuiTranslatedLabel(int x, int y, Color color, String stringKey, Object... placeholders)
    {
        super(x, y, color, "");

        this.stringKey = stringKey;
        this.placeholders = placeholders.clone();
    }

    public void setPlaceholder(int i, Object value)
    {
        this.placeholders[i] = value;
    }

    @Override
    public void draw()
    {
        if(!this.visible) return;

        stringLabel = new TextComponentTranslation(stringKey, placeholders).getFormattedText();

        super.draw();
    }
}
