package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.manufactory_supplier.ContainerManufactorySupplier;
import com.mike_caron.megacorp.gui.control.*;
import com.mike_caron.megacorp.impl.Quest;
import com.mike_caron.megacorp.impl.QuestLocalization;
import com.mike_caron.megacorp.impl.QuestManager;
import com.mike_caron.megacorp.network.CtoSMessage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class GuiManufactorySupplier
    extends GuiContainerOwnedBase
    implements GuiButton.ClickedListener, GuiList.Producer, GuiToggleButton.ChangedListener
{
    private static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/manufactory_supplier.png");

    private final ContainerManufactorySupplier container;

    private GuiGroup ownedGroup = new GuiGroup();
    private GuiGroup workorderGroup = new GuiGroup();
    private GuiGroup noQuestGroup = new GuiGroup();

    private GuiTranslatedLabel questLabel = new GuiTranslatedLabel(6, 20, "tile.megacorp:shipping_depot.quest", "", 0);
    private GuiTranslatedLabel itemLabel = new GuiTranslatedLabel(6, 31, "tile.megacorp:shipping_depot.item", "");
    private GuiTranslatedLabel quantityLabel = new GuiTranslatedLabel(6, 42, "tile.megacorp:manufactory_supplier.quantity", "", 0);
    private GuiTranslatedLabel profitLabel = new GuiTranslatedLabel(6, 53, "tile.megacorp:shipping_depot.profit", "");

    private GuiProgressBar timerBar = new GuiProgressBar(8, 67, 239, 12);
    private GuiTranslatedLabel timerLabel = new GuiTranslatedLabel(127, 69, GuiLabel.Alignment.CENTER, "tile.megacorp:manufactory_supplier.timer", 0);

    private GuiProgressBar progressBar = new GuiProgressBar(8, 83, 161, 12);
    private GuiTranslatedLabel progressLabel = new GuiTranslatedLabel(88, 85, GuiLabel.Alignment.CENTER, "tile.megacorp:shipping_depot.progress", 0, 0);
    private GuiTranslatedLabel maxProgressLabel = new GuiTranslatedLabel(88, 85, GuiLabel.Alignment.CENTER, "tile.megacorp:manufactory_supplier.max_progress");

    private GuiButton discardQuestButton = new GuiButton(
        ContainerManufactorySupplier.GUI_STOP_QUEST,
        231, 21,
        16, 16,
        null,
        new GuiImageTexture(0, 0, 8, 8, 10, 221, background)
    );

    private GuiList questList = new GuiList(6, 19, 242, 81, this);
    private GuiButton newQuestButton = new GuiButton(ContainerManufactorySupplier.GUI_CHOOSE_QUEST, 176, 3, 72, 14, GuiUtil.translate("tile.megacorp:manufactory_supplier.new_quest"));

    private GuiButton levelUpButton = new GuiButton(ContainerManufactorySupplier.GUI_LEVEL_UP, 172, 83, 75, 12, GuiUtil.translate("tile.megacorp:manufactory_supplier.level_up"));

    private GuiToggleButton autoLevelButton = new GuiImageToggleButton(ContainerManufactorySupplier.GUI_AUTOLEVEL, 231, 45, 16, 16, new GuiImageTexture(0, 0, 8, 8, 25, 221, 8, 8, background) );

    private List<Quest> listOfQuests;
    private List<QuestListItem> listOfListItems;

    private Quest selectedQuest = null;

    private int frameCounter = 0;
    private int currentItemStackIndex = 0;
    private ItemStack currentItemStack = ItemStack.EMPTY;

    public GuiManufactorySupplier(ContainerManufactorySupplier container)
    {
        super(container, 256, 186);

        this.container = container;

        listOfQuests = QuestManager.INSTANCE.getQuests();
        listOfQuests.sort((o1, o2) -> {
            QuestLocalization l1 = QuestManager.INSTANCE.getLocalizationForCurrent(o1);
            QuestLocalization l2 = QuestManager.INSTANCE.getLocalizationForCurrent(o2);
            return String.CASE_INSENSITIVE_ORDER.compare(l1.title, l2.title);
        });

        initControls();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if(noQuestGroup.isVisible())
        {
            newQuestButton.setEnabled(selectedQuest != null);
        }

        frameCounter += 1;

        if (frameCounter >= 20)
        {
            frameCounter = 0;
            currentItemStackIndex += 1;

            if(container.desiredItems != null)
            {
                currentItemStack = container.desiredItems.get(currentItemStackIndex % container.desiredItems.size());
                itemLabel.setPlaceholder(0, currentItemStack.getDisplayName());
            }
        }
    }

    @Override
    protected void onContainerRefresh()
    {
        //update fluid
        if(container.owner != null)
        {
            ownedGroup.setVisible(true);

            this.insertCardLabel.setVisible(false);

            if(container.desiredItems != null)
            {
                currentItemStack = container.desiredItems.get(currentItemStackIndex % container.desiredItems.size());

                noQuestGroup.setVisible(false);
                workorderGroup.setVisible(true);
                itemLabel.setPlaceholder(0, currentItemStack.getDisplayName());
                quantityLabel.setPlaceholder(0, NumberFormat.getIntegerInstance().format(container.itemsPerCycle));
                quantityLabel.setPlaceholder(1, NumberFormat.getIntegerInstance().format(container.ticksPerCycle / 20));

                //quantityLabel.setTooltip(GuiUtil.translate("tile.megacorp:manufactory_supplier.quantity_tooltip", container.ticksPerCycle * (container.desiredItems.get(0).getMaxStackSize()/container.itemsPerCycle)));
                timerLabel.setPlaceholder(0, NumberFormat.getIntegerInstance().format(container.ticksRemaining));
                int secsPerCycle = container.ticksPerCycle / 20;
                timerBar.setProgress(((float)container.ticksRemaining) / secsPerCycle);

                QuestLocalization questLocalization = QuestManager.INSTANCE.getLocalizationForCurrent(container.questId);
                questLabel.setPlaceholder(0, questLocalization.title);
                questLabel.setPlaceholder(1, container.level);
                questLabel.setTooltip(questLocalization.description);

                profitLabel.setPlaceholder(0, NumberFormat.getIntegerInstance().format(container.reward));

                levelUpButton.setEnabled(container.canLevelUp);

                autoLevelButton.setPressed(container.autoLevel);

                if(container.level >= 50 && container.progress >= 0)
                {
                    progressLabel.setVisible(false);
                    maxProgressLabel.setVisible(true);
                    progressBar.setProgress(1f);
                    progressBar.setForeColor(Color.GREEN);
                }
                else
                {
                    progressLabel.setVisible(true);
                    maxProgressLabel.setVisible(false);
                    if (container.progress >= 0)
                    {
                        progressBar.setForeColor(Color.GREEN);
                        progressBar.setProgress(((float) container.progress) / container.levelUpThreshold);
                        progressLabel.setPlaceholder(0, container.progress);
                        progressLabel.setPlaceholder(1, container.levelUpThreshold);
                    }
                    else
                    {
                        progressBar.setForeColor(Color.RED);
                        progressBar.setProgress(container.progress / -10f);
                        progressLabel.setPlaceholder(0, container.progress);
                        progressLabel.setPlaceholder(1, 10);
                    }
                }
            }
            else
            {
                noQuestGroup.setVisible(true);
                workorderGroup.setVisible(false);
            }
        }
        else
        {
            ownedGroup.setVisible(false);
            insertCardLabel.setVisible(true);

            listOfListItems = null;
        }

    }

    @Override
    public void addControls()
    {
        super.addControls();

        this.addControl(ownedGroup);

        ownedGroup.addControl(workorderGroup);
        ownedGroup.addControl(noQuestGroup);

        timerLabel.setzIndex(100);
        timerLabel.setColor(Color.WHITE);
        progressLabel.setzIndex(100);
        progressLabel.setColor(Color.WHITE);
        maxProgressLabel.setzIndex(101);
        maxProgressLabel.setColor(Color.WHITE);
        timerBar.setProgress(0.5f);
        timerBar.setForeColor(Color.BLUE);
        progressBar.setForeColor(Color.GREEN);

        noQuestGroup.addControl(newQuestButton);
        noQuestGroup.addControl(questList);

        workorderGroup.addControl(itemLabel);
        workorderGroup.addControl(timerBar);
        workorderGroup.addControl(quantityLabel);
        workorderGroup.addControl(timerLabel);
        workorderGroup.addControl(questLabel);
        workorderGroup.addControl(discardQuestButton);
        workorderGroup.addControl(progressBar);
        workorderGroup.addControl(progressLabel);
        workorderGroup.addControl(maxProgressLabel);
        workorderGroup.addControl(profitLabel);
        workorderGroup.addControl(levelUpButton);
        workorderGroup.addControl(autoLevelButton);

        newQuestButton.addListener(this);
        discardQuestButton.addListener(this);
        levelUpButton.addListener(this);
        autoLevelButton.addListener(this);

        discardQuestButton.setTooltip(GuiUtil.translate("tile.megacorp:shipping_depot.reroll"));
        autoLevelButton.setTooltip(GuiUtil.translate("tile.megacorp:manufactory_supplier.auto_level"));
    }

    @Override
    protected String getTitleKey()
    {
        return "tile.megacorp:manufactory_supplier.name";
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
        if(event.id == ContainerManufactorySupplier.GUI_CHOOSE_QUEST)
        {
            if (selectedQuest != null)
            {
                CtoSMessage packet = CtoSMessage.forGuiButton(container.getPos(), event.id, selectedQuest.getId());
                MegaCorpMod.networkWrapper.sendToServer(packet);
            }
        }
        else
        {
            CtoSMessage packet = CtoSMessage.forGuiButton(container.getPos(), event.id);
            MegaCorpMod.networkWrapper.sendToServer(packet);
        }
    }

    @Override
    public void changed(GuiToggleButton.ChangedEvent event)
    {
        switch(event.id)
        {
            case ContainerManufactorySupplier.GUI_AUTOLEVEL:
                CtoSMessage packet = CtoSMessage.forGuiToggle(container.getPos(), event.id, !container.autoLevel);
                MegaCorpMod.networkWrapper.sendToServer(packet);
                break;
        }
    }

    @Override
    public int getNumItems()
    {
        return listOfQuests != null ? listOfQuests.size() : 0;
    }

    @Override
    public int getItemHeight()
    {
        return 18;
    }

    @Override
    public GuiList.ListItem getItem(int i)
    {
        if(listOfListItems == null)
        {
            listOfListItems = new ArrayList<>(listOfQuests.size());
            for(Quest q : listOfQuests)
            {
                listOfListItems.add(new QuestListItem(q));
            }
        }
        return listOfListItems.get(i);
    }

    @Override
    public void onClick(int i)
    {
        selectedQuest = listOfQuests.get(i);
    }

    class QuestListItem
        implements GuiList.ListItem
    {
        Quest quest;
        QuestLocalization questLocalization;

        private NonNullList<ItemStack> possibleItems;

        public QuestListItem(Quest quest)
        {
            this.quest = quest;
            this.questLocalization = QuestManager.INSTANCE.getLocalizationForCurrent(this.quest.getId());
            this.possibleItems = this.quest.possibleItems();
        }

        @Override
        public List<String> getTooltip(EntityPlayer player, ITooltipFlag tooltipFlag, int mouseX, int mouseY, int width)
        {
            if(GuiUtil.inBounds(mouseX, mouseY, width - 14, 4, 10, 10))
            {
                ArrayList<String> ret = new ArrayList<>();
                ret.add(questLocalization.description);
                return ret;
            }
            if(GuiUtil.inBounds(mouseX, mouseY, 1, 1, 16, 16))
            {
                return possibleItems.get(currentItemStackIndex % possibleItems.size()).getTooltip(player, tooltipFlag);
            }
            return null;
        }

        @Override
        public void draw(int width, int height, GuiList.ListItemState state)
        {
            Color color = Color.BLACK;

            if(state.isOver())
            {
                color = Color.GRAY;
            }
            else if(selectedQuest == quest)
            {
                color = Color.DARK_GRAY;
            }

            drawGradientRect(0, 0, width, height, color.getRGB(), color.getRGB());

            if(possibleItems.size() > 0)
            {
                GuiUtil.drawItemStack(possibleItems.get(currentItemStackIndex % possibleItems.size()), 1, 1, itemRender, null);
            }

            fontRenderer.drawString(questLocalization.title, 20, 5, Color.WHITE.getRGB());

            GuiUtil.bindTexture(GuiUtil.MISC_RESOURCES);
            GuiUtil.drawTexturePart(width - 14, 4, 10, 10, 80, 0, 256, 256);
        }
    }
}
