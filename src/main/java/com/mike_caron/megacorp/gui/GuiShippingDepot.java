package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.shipping_depot.ContainerShippingDepot;
import com.mike_caron.megacorp.gui.control.*;
import com.mike_caron.megacorp.impl.QuestLocalization;
import com.mike_caron.megacorp.impl.QuestManager;
import com.mike_caron.megacorp.network.CtoSMessage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import java.awt.*;
import java.text.NumberFormat;

public class GuiShippingDepot
    extends GuiContainerOwnedBase
    implements GuiButton.ClickedListener, GuiToggleButton.ChangedListener
{
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;

    private static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/shipping_depot.png");

    private final ContainerShippingDepot container;

    private GuiGroup ownedGroup = new GuiGroup();
    private GuiGroup workorderGroup = new GuiGroup();

    private GuiTranslatedLabel questLabel = new GuiTranslatedLabel(6, 20, "tile.megacorp:shipping_depot.quest", "", 0);
    private GuiTranslatedLabel itemLabel = new GuiTranslatedLabel(6, 31, "tile.megacorp:shipping_depot.item", "");
    private GuiTranslatedLabel quantityLabel = new GuiTranslatedLabel(6, 42, "tile.megacorp:shipping_depot.quantity", "");
    private GuiTranslatedLabel profitLabel = new GuiTranslatedLabel(6, 54, "tile.megacorp:shipping_depot.profit", "");

    private GuiProgressBar progressBar = new GuiProgressBar(8, 67, 159, 12);
    private GuiTranslatedLabel progressLabel = new GuiTranslatedLabel(80, 69, "tile.megacorp:shipping_depot.progress", 0, 0);
    private GuiButton newQuestButton = new GuiButton(ContainerShippingDepot.GUI_NEW_QUEST, 7, 66, 161, 14, GuiUtil.translate("tile.megacorp:shipping_depot.new_quest"));

    private GuiImageToggleButton lockQuestButton = new GuiImageToggleButton(ContainerShippingDepot.GUI_LOCK_QUEST,157, 18, 14, 14, background, 6, 8, 181, 37);
    private GuiButton rerollQuestButton = new GuiImageButton(ContainerShippingDepot.GUI_REROLL_QUEST, 157, 34, 14, 14, background, 7, 10, 180, 19);
    private GuiImageToggleButton automaticQuestButton = new GuiImageToggleButton(ContainerShippingDepot.GUI_AUTOMATIC_QUEST,157, 50, 14, 14, background, 8, 8, 180, 4);



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
                quantityLabel.setPlaceholder(0, NumberFormat.getIntegerInstance().format(container.workOrder.getDesiredCount()));
                progressLabel.setPlaceholder(0, NumberFormat.getIntegerInstance().format(container.workOrder.getProgress()));
                progressLabel.setPlaceholder(1, NumberFormat.getIntegerInstance().format(container.workOrder.getDesiredCount()));
                progressBar.setProgress(((float)container.workOrder.getProgress()) / container.workOrder.getDesiredCount());

                QuestLocalization questLocalization = QuestManager.INSTANCE.getLocalizationForCurrent(container.workOrder.getQuestId());
                questLabel.setPlaceholder(0, questLocalization.title);
                questLabel.setPlaceholder(1, container.workOrder.getLevel());
                questLabel.setTooltip(questLocalization.description);

                profitLabel.setPlaceholder(0, NumberFormat.getIntegerInstance().format(container.workOrder.getProfit()));

                automaticQuestButton.setPressed(container.automaticallyGenerate);
                lockQuestButton.setPressed(container.questLocked);

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
        workorderGroup.addControl(questLabel);
        workorderGroup.addControl(rerollQuestButton);
        workorderGroup.addControl(automaticQuestButton);
        workorderGroup.addControl(lockQuestButton);
        workorderGroup.addControl(profitLabel);

        newQuestButton.addClickedListener(this);
        rerollQuestButton.addClickedListener(this);
        lockQuestButton.addListener(this);
        automaticQuestButton.addListener(this);

        rerollQuestButton.setTooltip(new TextComponentTranslation("tile.megacorp:shipping_depot.reroll").getUnformattedText());
        automaticQuestButton.setTooltip(new TextComponentTranslation("tile.megacorp:shipping_depot.automate").getUnformattedText());
        lockQuestButton.setTooltip(new TextComponentTranslation("tile.megacorp:shipping_depot.lock").getUnformattedText());
    }

    @Override
    protected String getTitleKey()
    {
        return "tile.megacorp:shipping_depot.name";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i1)
    {
        if(this.container.owner == null)
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
        CtoSMessage packet = CtoSMessage.forGuiButton(container.getPos(), event.id);
        MegaCorpMod.networkWrapper.sendToServer(packet);
    }

    @Override
    public void changed(GuiToggleButton.ChangedEvent event)
    {
        CtoSMessage packet = CtoSMessage.forGuiToggle(container.getPos(), event.id, event.newState);
        MegaCorpMod.networkWrapper.sendToServer(packet);
    }
}
