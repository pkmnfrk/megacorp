package com.mike_caron.megacorp.gui;

import com.google.common.base.Preconditions;
import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.uplink.ContainerUplink;
import com.mike_caron.megacorp.gui.control.GuiContainerBase;
import com.mike_caron.megacorp.gui.control.GuiList;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiTest
    extends GuiContainerBase
    implements GuiList.Producer
{
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;

    private static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/uplink.png");

    private GuiList list = new GuiList(8, 16, WIDTH - 16, 60, this);

    private boolean goingLeft = false;
    private boolean goingUp = false;

    private List<TestListItem> items = new ArrayList<>();
    private TestListItem selectedItem = null;

    public GuiTest(ContainerUplink container)
    {
        super(container, WIDTH, HEIGHT);

        initControls();

        for(int i = 0; i < 20; i++)
        {
            items.add(new TestListItem("Item " + i));
        }
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
        return items.size();
    }

    @Override
    public int getItemHeight()
    {
        return 15;
    }

    @Override
    public GuiList.ListItem getItem(int i)
    {
        return items.get(i);
    }

    @Override
    public void onClick(int i)
    {
        Preconditions.checkArgument( i >= 0 && i < items.size());

        selectedItem = items.get(i);
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
        public void draw(int width, int height, GuiList.ListItemState state)
        {
            if(selectedItem == this)
            {
                GuiUtil.setGLColor(Color.YELLOW);
            }
            else
            {
                GuiUtil.setGLColor(Color.RED);
            }
            GuiUtil.bindTexture(GuiUtil.MISC_RESOURCES);

            int sy = 16;

            if(state.isOver())
            {
                sy = 32;
            }

            GuiUtil.draw3x3Stretched(0, 0, width, height, 16, sy);

            fontRenderer.drawString(string, 1, height / 2 - 5, Color.WHITE.getRGB());
        }
    }
}
