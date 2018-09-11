package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.uplink.ContainerUplink;
import com.mike_caron.megacorp.gui.control.*;
import com.mike_caron.megacorp.network.CtoSMessage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.text.NumberFormat;

public class GuiUplink
    extends GuiContainerBase
    implements GuiButton.ClickedListener, GuiTextBox.TextboxListener
{
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;

    private static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/uplink.png");

    //private final TileEntityUplink te;
    private final ContainerUplink container;

    private static ResourceLocation cardImage = new ResourceLocation(MegaCorpMod.modId, "textures/gui/test.png");
    //private GuiButtonImage cardButton;

    private GuiGroup ownedGroup = new GuiGroup();
    private GuiGroup unownedGroup = new GuiGroup();

    private GuiWrappedTextBox corpNameField = new GuiWrappedTextBox(7, 29, 156, 10);
    private GuiButton establishCorporation = new GuiButton(1, guiLeft + 28, guiTop + 55, 120, 20, "");

    private GuiMultilineLabel unownedText = GuiUtil.staticMultilineLabelFromTranslationKey(11, 16, 154, 40, "");

    private GuiLabel corpNameHeader = GuiUtil.staticLabelFromTranslationKey(6, 19, "tile.megacorp:uplink.corpname");
    private GuiTranslatedLabel profitLabel = new GuiTranslatedLabel(6, 43, "tile.megacorp:uplink.profit", "");

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
        if(container.owner != null)
        {
            if(!ownedGroup.isVisible()) ownedGroup.setVisible(true);
            if(unownedGroup.isVisible()) unownedGroup.setVisible(false);

            if(container.corpNameCounter != this.corpNameCounter)
            {
                if(corpNameField != null)
                {
                    if(container.corpName != null)
                    {
                        corpNameField.setText(container.corpName);
                    }
                    else
                    {
                        corpNameField.setText("");
                    }
                    this.corpNameCounter = container.corpNameCounter;
                }
            }

            profitLabel.setPlaceholder(0, NumberFormat.getIntegerInstance().format(container.profit));
        }
        else
        {
            if(ownedGroup.isVisible()) ownedGroup.setVisible(false);
            if(!unownedGroup.isVisible()) unownedGroup.setVisible(true);

            establishCorporation.setLabel(GuiUtil.translateConditional(container.hasCorp, "tile.megacorp.uplink.establish", "tile.megacorp.uplink.createcorp"));
            unownedText.setString(GuiUtil.translateConditional(container.hasCorp, "tile.megacorp:uplink.existcorp", "tile.megacorp:uplink.newcorp"));
        }
    }

    @Override
    public void addControls()
    {
        super.addControls();

        this.addControl(ownedGroup);
        this.addControl(unownedGroup);

        ownedGroup.addControl(corpNameField);
        ownedGroup.addControl(corpNameHeader);
        ownedGroup.addControl(profitLabel);

        unownedGroup.addControl(establishCorporation);
        unownedGroup.addControl(unownedText);

        unownedText.setAlignment(GuiMultilineLabel.Alignment.CENTER);
        establishCorporation.addListener(this);
        corpNameField.addTextboxListener(this);
    }

    @Override
    protected String getTitleKey()
    {
        return "tile.megacorp:uplink.name";
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

    @Override
    public void clicked(GuiButton.ClickedEvent event)
    {
        switch (event.id)
        {
            case 1:
                CtoSMessage packet = CtoSMessage.forGuiButton(container.getPos(), 1);
                MegaCorpMod.networkWrapper.sendToServer(packet);
                event.control.setEnabled(false);
                break;
        }
    }

    @Override
    public void changed(GuiTextBox.ChangedEvent event)
    {
        if(event.control == corpNameField)
        {
            CtoSMessage packet = CtoSMessage.forGuiString(container.getPos(), 2, corpNameField.getText());
            MegaCorpMod.networkWrapper.sendToServer(packet);
        }
    }
}
