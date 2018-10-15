package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.capital_investor.ContainerCapitalInvestor;
import com.mike_caron.megacorp.block.shipping_depot.ContainerShippingDepot;
import com.mike_caron.megacorp.gui.control.GuiButton;
import com.mike_caron.megacorp.gui.control.GuiFluid;
import com.mike_caron.megacorp.gui.control.GuiGroup;
import com.mike_caron.megacorp.gui.control.GuiList;
import com.mike_caron.megacorp.impl.QuestLocalization;
import com.mike_caron.megacorp.impl.RewardManager;
import com.mike_caron.megacorp.network.CtoSMessage;
import com.mike_caron.megacorp.util.StringUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class GuiCapitalInvestor
    extends GuiContainerOwnedBase
    implements GuiButton.ClickedListener, GuiList.Producer
{
    public static final int WIDTH = 176;
    public static final int HEIGHT = 182;

    public static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/capital_investor.png");

    private final ContainerCapitalInvestor container;

    private GuiGroup ownedGroup = new GuiGroup();

    private GuiList rewardList = new GuiList(6, 17, 163, 62, this);
    private GuiButton buyRewardButton = new GuiButton(ContainerShippingDepot.GUI_NEW_QUEST,  124, 82, 45, 14, GuiUtil.translate("tile.megacorp:capital_investor.buy"));
    private GuiFluid fluidGauge = new GuiFluid(7, 83, 113, 12, GuiFluid.Orientation.HORIZONTAL);

    private ContainerCapitalInvestor.RewardData selectedReward = null;

    public GuiCapitalInvestor(ContainerCapitalInvestor container)
    {
        super(container, WIDTH, HEIGHT);

        this.container = container;

        initControls();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        buyRewardButton.setEnabled(selectedReward != null && container.fluidAmount >= selectedReward.nextRankCost);
    }

    @Override
    protected void onContainerRefresh()
    {
        //update fluid
        if(container.owner != null)
        {

            if(selectedReward != null)
            {
                String curReward = selectedReward.id;

                for(ContainerCapitalInvestor.RewardData r : container.rewardList)
                {
                    if(r.id.equals(curReward))
                    {
                        selectedReward = r;
                        break;
                    }
                }
            }

            ownedGroup.setVisible(true);
            insertCardLabel.setVisible(false);

            fluidGauge.setFluid(FluidRegistry.getFluid(container.fluid));
            fluidGauge.setAmount(container.fluidAmount);
            fluidGauge.setCapacity(container.fluidCapacity);
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

        ownedGroup.addControl(buyRewardButton);
        ownedGroup.addControl(rewardList);
        ownedGroup.addControl(fluidGauge);

        buyRewardButton.addListener(this);
    }

    @Override
    protected String getTitleKey()
    {
        return "tile.megacorp:capital_investor.name";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i1)
    {
        if(container.owner != null)
        {
            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(background);
            drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        }
        else
        {
            drawInsertCardBackground();
        }
    }

    @Override
    public void clicked(GuiButton.ClickedEvent event)
    {
        if(event.id == ContainerCapitalInvestor.GUI_BUY_REWARD)
        {
            if (selectedReward != null)
            {
                CtoSMessage packet = CtoSMessage.forGuiButton(container.getPos(), event.id, selectedReward.id);
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
        return (container != null && container.rewardList != null) ? container.rewardList.size() : 0;
    }

    @Override
    public int getItemHeight()
    {
        return 18;
    }

    @Override
    public GuiList.ListItem getItem(int i)
    {
        return new RewardListItem(container.rewardList.get(i));
    }

    @Override
    public void onClick(int i)
    {
        selectedReward = container.rewardList.get(i);
    }

    class RewardListItem
        implements GuiList.ListItem
    {
        ContainerCapitalInvestor.RewardData reward;
        QuestLocalization rewardLocalization;
        String suffix;
        List<String> toolTip;

        public RewardListItem(ContainerCapitalInvestor.RewardData reward)
        {
            this.reward = reward;
            this.rewardLocalization = RewardManager.INSTANCE.getLocalizationForCurrent(this.reward.id);
            suffix = " " + StringUtil.toRoman(this.reward.nextRank);

            toolTip = new ArrayList<>();
            String tmp = GuiUtil.i18n("tile.megacorp:capital_investor.rank", StringUtil.toRoman(reward.nextRank));
            toolTip.add(tmp);

            tmp = NumberFormat.getIntegerInstance().format(reward.nextRankCost) + "mB";
            if(reward.nextRankCost > container.fluidAmount)
            {
                tmp = TextFormatting.RED + tmp;
            }

            tmp = GuiUtil.i18n("tile.megacorp:capital_investor.cost", tmp);

            toolTip.add(tmp);

            Fluid fluid = FluidRegistry.getFluid(reward.currencyType.name().toLowerCase());
            tmp = fluid.getLocalizedName(null);
            if(!fluid.getName().equals(container.fluid))
            {
                tmp = TextFormatting.RED + tmp;
            }
            tmp = GuiUtil.i18n("tile.megacorp:capital_investor.currency", tmp);

            toolTip.add(tmp);
            String[] vars = new String[reward.nextRankVariables.length];
            NumberFormat floatFormat = NumberFormat.getNumberInstance();
            NumberFormat intFormat = NumberFormat.getIntegerInstance();
            for(int i = 0; i < vars.length; i++)
            {
                //if(reward.nextRankVariables[i] != (int)reward.nextRankVariables[i].floatValue())
                //{
                    vars[i] = floatFormat.format(reward.nextRankVariables[i]);
                //}
                //else
                //{
                //    vars[i] = intFormat.format(reward.nextRankVariables[i]);
                //}
            }
            toolTip.add(String.format(rewardLocalization.description, (Object[])vars));
        }

        @Override
        public List<String> getTooltip(int mouseX, int mouseY, int width)
        {
            if(GuiUtil.inBounds(mouseX, mouseY, width - 14, 4, 10, 10))
            {
               return toolTip;
            }
            return null;
        }

        @Override
        public void draw(int width, int height, GuiList.ListItemState state)
        {
            Color backColor = Color.BLACK;
            Color foreColor = Color.WHITE;

            if(state.isOver())
            {
                backColor = Color.GRAY;
            }
            else if(selectedReward == reward)
            {
                backColor = Color.DARK_GRAY;
            }

            if(!reward.available)
            {
                foreColor = Color.RED;
            }

            drawGradientRect(0, 0, width, height, backColor.getRGB(), backColor.getRGB());

            //drawItemStack(reward.item, 1, 1, "");
            fontRenderer.drawString(rewardLocalization.title + suffix, 20, 5, foreColor.getRGB());

            GuiUtil.setGLColor(Color.WHITE);
            GuiUtil.bindTexture(GuiUtil.MISC_RESOURCES);
            GuiUtil.drawTexturePart(width - 14, 4, 10, 10, 80, 0, 256, 256);
        }
    }
}
