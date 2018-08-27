package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.uplink.ContainerUplink;
import com.mike_caron.megacorp.gui.control.GuiContainerBase;
import com.mike_caron.megacorp.gui.control.GuiList;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class GuiTest
    extends GuiContainerBase
    implements GuiList.Producer
{
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;

    private static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/uplink.png");

    private GuiList list = new GuiList(10, 20, WIDTH - 20, 60, this);

    private boolean goingLeft = false;
    private boolean goingUp = false;

    public GuiTest(ContainerUplink container)
    {
        super(container);

        initControls();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

    }

    @Override
    protected void addControls()
    {
        super.addControls();

        this.addControl(list);

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i1)
    {
        drawInsertCardBackground();
    }


    @Override
    public int getNumItems()
    {
        return 3;
    }

    @Override
    public int getItemHeight()
    {
        return 20;
    }

    @Override
    public GuiList.ListItem getItem(int i)
    {
        return new TestListItem(Integer.toString(i));
    }

    class TestListItem
        implements GuiList.ListItem
    {
        String string;

        public TestListItem(String string)
        {
            this.string = string;
        }

        @Override
        public void draw(int width, int height)
        {
            GuiUtil.setGLColor(Color.RED);
            GuiUtil.bindTexture(GuiUtil.MISC_RESOURCES);
            GuiUtil.draw3x3Stretched(0, 0, width, height, 16, 16);

            fontRenderer.drawString(string, 1, 1, Color.WHITE.getRGB());
        }
    }
}