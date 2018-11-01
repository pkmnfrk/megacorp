package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.manufactory_supplier.ContainerManufactorySupplier;
import com.mike_caron.megacorp.gui.control.*;
import com.mike_caron.megacorp.impl.Quest;
import com.mike_caron.megacorp.impl.QuestLocalization;
import com.mike_caron.megacorp.impl.QuestManager;
import com.mike_caron.megacorp.network.CtoSMessage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class GuiManufactorySupplier
    extends GuiContainerOwnedBase
    implements GuiButton.ClickedListener, GuiList.Producer
{
    private static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/manufactory_supplier.png");

    private final ContainerManufactorySupplier container;

    private GuiGroup ownedGroup = new GuiGroup();
    private GuiGroup workorderGroup = new GuiGroup();
    private GuiGroup noQuestGroup = new GuiGroup();

    private GuiTranslatedLabel questLabel = new GuiTranslatedLabel(6, 20, "tile.megacorp:shipping_depot.quest", "", 0);
    private GuiTranslatedLabel itemLabel = new GuiTranslatedLabel(6, 31, "tile.megacorp:shipping_depot.item", "");
    //private GuiTranslatedLabel quantityLabel = new GuiTranslatedLabel(6, 42, "tile.megacorp:shipping_depot.quantity", "");
    private GuiTranslatedLabel profitLabel = new GuiTranslatedLabel(6, 42, "tile.megacorp:shipping_depot.profit", "");

    private GuiProgressBar timerBar = new GuiProgressBar(8, 67, 239, 12);
    private GuiTranslatedLabel timerLabel = new GuiTranslatedLabel(127, 69, GuiLabel.Alignment.CENTER, "tile.megacorp:manufactory_supplier.timer", 0);

    private GuiProgressBar progressBar = new GuiProgressBar(8, 83, 161, 12);
    private GuiTranslatedLabel progressLabel = new GuiTranslatedLabel(88, 69, GuiLabel.Alignment.CENTER, "tile.megacorp:shipping_depot.progress", 0, 0);

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
                //quantityLabel.setPlaceholder(0, NumberFormat.getIntegerInstance().format());
                timerLabel.setPlaceholder(0, NumberFormat.getIntegerInstance().format(container.ticksRemaining / 20));
                timerBar.setProgress(((float)container.ticksRemaining) / container.ticksPerCycle);
                progressBar.setProgress(((float)container.progress) / container.levelUpThreshold);

                QuestLocalization questLocalization = QuestManager.INSTANCE.getLocalizationForCurrent(container.questId);
                questLabel.setPlaceholder(0, questLocalization.title);
                questLabel.setPlaceholder(1, container.level);
                questLabel.setTooltip(questLocalization.description);

                profitLabel.setPlaceholder(0, NumberFormat.getIntegerInstance().format(container.reward));



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
        timerBar.setProgress(0.5f);
        timerBar.setForeColor(Color.BLUE);
        progressBar.setForeColor(Color.GREEN);

        noQuestGroup.addControl(newQuestButton);
        noQuestGroup.addControl(questList);

        workorderGroup.addControl(itemLabel);
        workorderGroup.addControl(timerBar);
        workorderGroup.addControl(timerLabel);
        workorderGroup.addControl(questLabel);
        workorderGroup.addControl(discardQuestButton);
        workorderGroup.addControl(progressBar);
        workorderGroup.addControl(profitLabel);
        workorderGroup.addControl(levelUpButton);

        newQuestButton.addListener(this);
        discardQuestButton.addListener(this);

        discardQuestButton.setTooltip(new TextComponentTranslation("tile.megacorp:shipping_depot.reroll").getUnformattedText());
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
                CtoSMessage packet = CtoSMessage.forGuiButton(container.getPos(), event.id, selectedQuest.id);
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
            this.questLocalization = QuestManager.INSTANCE.getLocalizationForCurrent(this.quest.id);
            this.possibleItems = this.quest.possibleItems();
        }

        @Override
        public List<String> getTooltip(int mouseX, int mouseY, int width)
        {
            if(GuiUtil.inBounds(mouseX, mouseY, width - 14, 4, 10, 10))
            {
                ArrayList<String> ret = new ArrayList<>();
                ret.add(questLocalization.description);
                return ret;
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
