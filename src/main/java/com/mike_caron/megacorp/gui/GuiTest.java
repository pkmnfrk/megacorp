package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.uplink.ContainerUplink;
import com.mike_caron.megacorp.gui.control.GuiContainerBase;
import com.mike_caron.megacorp.gui.control.GuiLabel;
import com.mike_caron.megacorp.gui.control.GuiScrollPort;
import net.minecraft.util.ResourceLocation;

public class GuiTest
    extends GuiContainerBase
{
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;

    private static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/uplink.png");

    private GuiScrollPort port = new GuiScrollPort(10, 20, WIDTH - 20, 60);
    private GuiLabel testLabel = new GuiLabel(0, 0, "Hello");

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

        /*
        if(goingLeft)
        {
            port.setScrollX(port.getScrollX() - 1);
            if(port.getScrollX() <= -(WIDTH - 20))
            {
                goingLeft = false;
            }
        }
        else
        {
            port.setScrollX(port.getScrollX() + 1);
            if(port.getScrollX() >= 20)
            {
                goingLeft = true;
            }
        }

        if(goingUp)
        {
            port.setScrollY(port.getScrollY() - 1);
            if(port.getScrollY() <= -60)
            {
                goingUp = false;
            }
        }
        else
        {
            port.setScrollY(port.getScrollY() + 1);
            if(port.getScrollY() >= 9)
            {
                goingUp = true;
            }
        }
        */
    }

    @Override
    protected void addControls()
    {
        super.addControls();

        this.addControl(port);

        port.addControl(testLabel);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i1)
    {
        drawInsertCardBackground();
    }



}
