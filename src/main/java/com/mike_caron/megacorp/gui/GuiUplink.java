package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.uplink.ContainerUplink;
import com.mike_caron.megacorp.gui.control.GuiButton;
import com.mike_caron.megacorp.gui.control.GuiContainerBase;
import com.mike_caron.megacorp.gui.control.GuiControl;
import com.mike_caron.megacorp.gui.control.GuiGroup;
import com.mike_caron.megacorp.network.CtoSMessage;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import java.text.NumberFormat;

public class GuiUplink
    extends GuiContainerBase
{
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;

    private static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/uplink.png");

    //private final TileEntityUplink te;
    private final ContainerUplink container;

    private static ResourceLocation cardImage = new ResourceLocation(MegaCorpMod.modId, "textures/gui/test.png");
    //private GuiButtonImage cardButton;

    private GuiGroup mainGroup;

    private GuiTextField corpNameField;
    private GuiButton establishCorporation;

    private int corpNameCounter = -1;

    public GuiUplink(ContainerUplink container)
    {
        super(container);

        xSize = WIDTH;
        ySize = HEIGHT;

        this.container = container;

        initControls();
    }

    @Override
    protected void onContainerRefresh()
    {
        initGui();
    }

    @Override
    protected void onActionPerformed(GuiControl button)
    {
        if(!button.getEnabled()) return;

        if(button == establishCorporation)
        {
            CtoSMessage packet = CtoSMessage.forGuiButton(container.getPos(), 1);
            MegaCorpMod.networkWrapper.sendToServer(packet);
            button.setEnabled(false);
        }
    }

    @Override
    protected void textFieldLostFocus(GuiTextField textField)
    {
        if(textField == corpNameField)
        {
            //container.corpName = corpNameField.getText();
            //send update
            CtoSMessage packet = CtoSMessage.forGuiString(container.getPos(), 2, corpNameField.getText());
            MegaCorpMod.networkWrapper.sendToServer(packet);
        }
    }

    @Override
    public void addControls()
    {
        super.addControls();

        mainGroup = new GuiGroup();

        this.addControl(mainGroup);

        String name = container.corpName;
        int cursor = 0;
        if(name != null)
        {
            cursor = name.length();
        }

        if(corpNameField != null)
        {
            name = corpNameField.getText();
            cursor = corpNameField.getCursorPosition();
        }

        if(container.owner != null)
        {
            addControl(corpNameField = new GuiTextField(2, this.fontRenderer, guiLeft + 7, guiTop + 29, 156, 10));
            corpNameField.setText(name);
            corpNameField.setCursorPosition(cursor);
            corpNameField.setCanLoseFocus(true);
        }
        else
        {
            String message = "tile.megacorp.uplink.createcorp";
            if(container.hasCorp)
            {
                message = "tile.megacorp.uplink.establish";
            }
            message = new TextComponentTranslation(message).getUnformattedText();
            mainGroup.addControl(establishCorporation = new GuiButton(2, guiLeft + 28, guiTop + 55, 120, 20, message));
        }
    }

    @Override
    protected String getTitleKey()
    {
        return "tile.megacorp:uplink.name";
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        if(container.owner == null)
        {
            String message = "tile.megacorp:uplink.newcorp";
            if(container.hasCorp)
            {
                message = "tile.megacorp:uplink.existcorp";
            }
            drawCenteredWrappedString(new TextComponentTranslation(message).getUnformattedText(), 88, 35, 154);
        }
        else
        {

            this.fontRenderer.drawString(new TextComponentTranslation("tile.megacorp:uplink.corpname").getUnformattedText(), 6, 19, GuiUtil.FONT_COLOUR);

            String prefix = new TextComponentTranslation("tile.megacorp:uplink.profit").getUnformattedText();

            prefix += " $" + NumberFormat.getIntegerInstance().format(container.profit);

            this.fontRenderer.drawString(prefix, 6, 43, GuiUtil.FONT_COLOUR);
        }


    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if(container.corpNameCounter != this.corpNameCounter)
        {
            if(corpNameField != null)
            {
                corpNameField.setText(container.corpName);
                this.corpNameCounter = container.corpNameCounter;
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i1)
    {
        if(container.owner == null)
        {
            drawInsertCardBackground();
        }
        else
        {
            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(background);
            drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        }
    }

}
