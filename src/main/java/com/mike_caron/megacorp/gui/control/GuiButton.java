package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.client.renderer.GlStateManager;

public class GuiButton
    extends GuiSized
{
    private int id;

    private String label;
    private State state;


    public GuiButton(int id, int x, int y, int width, int height, String label)
    {
        super(x, y, width, height);

        this.id = id;
        this.label = label;
        this.state = State.NORMAL;
    }

    @Override
    public void draw()
    {
        if(!visible)
            return;

        int sx = 0;
        switch(state)
        {
            case NORMAL:
                sx = 16;
                break;
            case HOVERED:
                sx = 40;
                break;
            case PRESSED:
                sx = 64;
                break;
        }

        GuiUtil.bindTexture(GuiUtil.MISC_RESOURCES);
        GlStateManager.color(1, 1, 1, 1);
        GuiUtil.draw3x3(this.x, this.y, this.width, this.height, sx, 0);
    }

    @Override
    public void onMouseEnter()
    {
        this.state = State.HOVERED;
    }

    @Override
    public void onMouseExit()
    {
        this.state = State.NORMAL;
    }

    enum State
    {
        NORMAL,
        HOVERED,
        PRESSED
    }
}
