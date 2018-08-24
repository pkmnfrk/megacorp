package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.shipping_depot.ContainerShippingDepot;
import com.mike_caron.megacorp.gui.control.GuiButton;
import com.mike_caron.megacorp.gui.control.GuiGroup;
import com.mike_caron.megacorp.gui.control.GuiProgressBar;
import com.mike_caron.megacorp.gui.control.GuiTranslatedLabel;
import com.mike_caron.megacorp.network.CtoSMessage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class GuiShippingDepot
    extends GuiContainerOwnedBase
    implements GuiButton.ClickedListener
{
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;

    private static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/shipping_depot.png");

    private final ContainerShippingDepot container;

    private GuiGroup ownedGroup = new GuiGroup();
    private GuiGroup workorderGroup = new GuiGroup();

    private GuiTranslatedLabel itemLabel = new GuiTranslatedLabel(6, 20, "tile.megacorp:shipping_depot.item", "");
    private GuiTranslatedLabel quantityLabel = new GuiTranslatedLabel(6, 31, "tile.megacorp:shipping_depot.quantity", "");
    private GuiProgressBar progressBar = new GuiProgressBar(8, 67, 159, 12);
    private GuiTranslatedLabel progressLabel = new GuiTranslatedLabel(80, 69, "tile.megacorp:shipping_depot.progress", 0, 0);
    private GuiButton newQuestButton = new GuiButton(1, 7, 66, 161, 14, GuiUtil.translate("tile.megacorp:shipping_depot.new_quest"));
    public GuiShippingDepot(ContainerShippingDepot container)
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
        //update fluid
        if(container.owner != null)
        {
            ownedGroup.setVisible(true);
            this.insertCardLabel.setVisible(false);
            if(container.workOrder != null)
            {
                newQuestButton.setVisible(false);
                workorderGroup.setVisible(true);
                itemLabel.setPlaceholder(0, container.workOrder.getDesiredItem().getDisplayName());
                quantityLabel.setPlaceholder(0, container.workOrder.getDesiredItem().getCount());
                progressLabel.setPlaceholder(0, container.workOrder.getProgress());
                progressLabel.setPlaceholder(1, container.workOrder.getDesiredItem().getCount());
                progressBar.setProgress(((float)container.workOrder.getProgress()) / container.workOrder.getDesiredItem().getCount());
            }
            else
            {
                newQuestButton.setVisible(true);
                workorderGroup.setVisible(false);
            }
        }
        else
        {
            ownedGroup.setVisible(false);
            insertCardLabel.setVisible(true);
        }

    }

    @Override
    public void addControls()
    {
        super.addControls();

        this.addControl(ownedGroup);
        ownedGroup.addControl(workorderGroup);

        progressLabel.setzIndex(100);
        progressLabel.setColor(Color.WHITE);
        progressBar.setProgress(0.5f);

        ownedGroup.addControl(newQuestButton);

        workorderGroup.addControl(itemLabel);
        workorderGroup.addControl(quantityLabel);
        workorderGroup.addControl(progressBar);
        workorderGroup.addControl(progressLabel);

        newQuestButton.addClickedListener(this);
    }

    @Override
    protected String getTitleKey()
    {
        return "tile.megacorp:shipping_depot.name";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i1)
    {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    public void clicked(GuiButton.ClickedEvent event)
    {
        CtoSMessage packet = CtoSMessage.forGuiButton(container.getPos(), event.id);
        MegaCorpMod.networkWrapper.sendToServer(packet);
    }
}
