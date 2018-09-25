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
        this(x, y, GuiUtil.FONT_COLOUR, Alignment.LEFT, VerticalAlignment.BOTTOM, stringKey, placeholders);
    }

    public GuiTranslatedLabel(int x, int y, Alignment alignment, String stringKey, Object... placeholders)
    {
        this(x, y, GuiUtil.FONT_COLOUR, alignment, VerticalAlignment.BOTTOM, stringKey, placeholders);
    }

    public GuiTranslatedLabel(int x, int y, Color color, String stringKey, Object... placeholders)
    {
        this(x, y, color, Alignment.LEFT, VerticalAlignment.BOTTOM, stringKey, placeholders);
    }

    public GuiTranslatedLabel(int x, int y, Alignment alignment, VerticalAlignment verticalAlignment, String stringKey, Object... placeholders)
    {
        this(x, y, GuiUtil.FONT_COLOUR, alignment, verticalAlignment, stringKey, placeholders);
    }

    public GuiTranslatedLabel(int x, int y, Color color, Alignment alignment, VerticalAlignment verticalAlignment, String stringKey, Object... placeholders)
    {
        super(x, y, color, "", alignment, verticalAlignment);

        this.stringKey = stringKey;
        this.placeholders = placeholders.clone();
    }

    public void setPlaceholder(int i, Object value)
    {
        this.placeholders[i] = value;

        stringLabel = new TextComponentTranslation(stringKey, placeholders).getFormattedText();
    }
}
