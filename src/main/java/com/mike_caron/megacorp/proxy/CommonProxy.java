package com.mike_caron.megacorp.proxy;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.ModConfig;
import com.mike_caron.megacorp.fluid.ModFluids;
import com.mike_caron.megacorp.impl.QuestManager;
import com.mike_caron.megacorp.impl.RewardManager;
import com.mike_caron.megacorp.recipes.PCRecipeManager;
import com.mike_caron.megacorp.recipes.SBSRecipeManager;
import com.mike_caron.megacorp.reward.*;
import com.mike_caron.megacorp.util.FileUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.io.File;

@Mod.EventBusSubscriber
public class CommonProxy
    implements IModProxy
{
    public static Configuration config;
    public static File megacorpDirectory;
    public static File questsDirectory;
    public static File rewardsDirectory;

    public void preInit(FMLPreInitializationEvent e)
    {
        File directory = e.getModConfigurationDirectory();
        megacorpDirectory = new File(directory.getPath(), "megacorp");
        questsDirectory = new File(megacorpDirectory.getPath(), "quests");
        rewardsDirectory = new File(megacorpDirectory.getPath(), "rewards");
        if (!megacorpDirectory.exists() && !megacorpDirectory.mkdir())
        {
            MegaCorpMod.logger.error("Unable to create config directory");
        }
        if (!questsDirectory.exists())
        {
            if(!questsDirectory.mkdir())
            {
                MegaCorpMod.logger.error("Unable to create config directory");
            }
        }
        if (!rewardsDirectory.exists() && !rewardsDirectory.mkdir())
        {
            MegaCorpMod.logger.error("Unable to create config directory");
        }

        File readme = new File(questsDirectory.getPath(), "readme.txt");
        FileUtils.exportResource("/assets/megacorp/quests_readme.txt", readme);

        config = new Configuration(new File(megacorpDirectory.getPath(), "megacorp.cfg"));
        ModConfig.readConfig();

        ModFluids.register();
    }

    public void init(FMLInitializationEvent e)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(MegaCorpMod.instance, new GuiProxy());
        CapabilityManager.INSTANCE.register(IPlayerRewards.class, new PlayerRewardCapabilityStorage(), PlayerRewards::new);
    }

    public void postInit(FMLPostInitializationEvent e)
    {
        SBSRecipeManager.addDefaultRecipes();
        PCRecipeManager.addDefaultRecipes();
        QuestManager.INSTANCE.loadQuests(questsDirectory);
        RewardManager.INSTANCE.loadRewards();
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> registry)
    {
        //registry.getRegistry().register(new FluidRecipe().setRegistryName(MegaCorpMod.modId, "uplink"));
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.START)
        {
            PlayerTickRewards.handle(event.player);
        }
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof EntityPlayer)
        {
            event.addCapability(new ResourceLocation(MegaCorpMod.modId, "rewards"), new PlayerRewardsProvider());
        }
    }
}
