package com.mike_caron.megacorp.command;

import com.google.gson.JsonArray;
import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.api.ICorporationManager;
import com.mike_caron.megacorp.impl.*;
import com.mike_caron.megacorp.proxy.CommonProxy;
import com.mike_caron.mikesmodslib.command.CommandBase;
import com.mike_caron.mikesmodslib.util.StringUtil;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

public class MegaCorpCommand
    extends CommandBase
{
    private static final List<String> aliases = new ArrayList<>();
    private static final Map<String, TriAction<MinecraftServer, ICommandSender, String[]>> subCommands = new HashMap<>();

    public MegaCorpCommand()
    {
        addAlias("megacorp");
        addAlias("corp");

        addSubCommand("reloadRewards", MegaCorpCommand::reloadRewards, true);
        addSubCommand("setRewardLevel", MegaCorpCommand::setRewardLevel, true);
        addSubCommand("clearRewards", MegaCorpCommand::clearRewards, true);
        addSubCommand("reloadQuests", MegaCorpCommand::reloadQuests, true);
        addSubCommand("removeCorporation", MegaCorpCommand::removeCorporation, true);
        addSubCommand("reloadVending", MegaCorpCommand::reloadVending, true);
        addSubCommand("dumpQuests", MegaCorpCommand::dumpQuests, false);
    }

    @Nonnull
    @Override
    public String getName()
    {
        return "megacorp";
    }

    private static void reloadRewards(@Nullable MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
        throws CommandException
    {
        RewardManager.INSTANCE.loadRewards(CommonProxy.rewardsDirectory);
        sender.sendMessage(new TextComponentTranslation("command.megacorp.done"));
    }

    private static void reloadQuests(@Nullable MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
        throws CommandException
    {
        //RewardManager.INSTANCE.loadRewards();
        QuestManager.INSTANCE.loadQuests(CommonProxy.questsDirectory);
        sender.sendMessage(new TextComponentTranslation("command.megacorp.done"));
    }

    private static void reloadVending(@Nullable MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
        throws CommandException
    {
        if(VendingManager.INSTANCE.loadVendingItems(CommonProxy.megacorpDirectory))
        {
            sender.sendMessage(new TextComponentTranslation("command.megacorp.done"));
        }
        else
        {
            sender.sendMessage(new TextComponentTranslation("command.megacorp.error"));
        }
    }

    // /megacorp setRewardLevel <rewardid> <level>
    private static void setRewardLevel(@Nullable MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
        throws CommandException
    {
        if(args.length < 2)
        {
            sender.sendMessage(new TextComponentTranslation("command.megacorp:setRewardLevel.usage"));
            return;
        }

        try {
            String reward_id = args[0];
            int level = Integer.parseInt(args[1]);

            UUID id = ((EntityPlayerMP)sender).getUniqueID();

            ICorporationManager manager = CorporationManager.get(((EntityPlayerMP) sender).getServerWorld());

            Corporation corp = null;

            if(manager.ownerHasCorporation(id))
            {
                corp = (Corporation) manager.getCorporationForOwner(id);
            }

            if(corp == null)
            {
                sender.sendMessage(new TextComponentTranslation("command.megacorp.noCorp"));
                return;
            }

            if(level < 0)
            {
                sender.sendMessage(new TextComponentTranslation("command.megacorp:setRewardLevel.invalidLevel"));
                return;
            }

            corp.setRewardLevel(reward_id, level);
        }
        catch(NumberFormatException ex)
        {
            sender.sendMessage(new TextComponentTranslation("command.megacorp:setRewardLevel.invalidLevel"));
            return;
        }
        catch(IllegalArgumentException ex)
        {
            sender.sendMessage(new TextComponentTranslation("command.megacorp:setRewardLevel.invalidReward"));
            return;
        }


        sender.sendMessage(new TextComponentTranslation("command.megacorp.done"));
    }

    // megacorp clearRewards
    private static void clearRewards(@Nullable MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
        throws CommandException
    {
        UUID id = ((EntityPlayerMP)sender).getUniqueID();

        ICorporationManager manager = CorporationManager.get(((EntityPlayerMP) sender).getServerWorld());

        Corporation corp = null;
        if(manager.ownerHasCorporation(id))
        {
            corp = (Corporation) manager.getCorporationForOwner(id);
        }

        if(corp == null)
        {
            sender.sendMessage(new TextComponentTranslation("command.megacorp.noCorp"));
            return;
        }

        corp.clearRewards();

        sender.sendMessage(new TextComponentTranslation("command.megacorp.done"));
    }

    private static void removeCorporation(@Nullable MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
        throws CommandException
    {
        UUID id = ((EntityPlayerMP)sender).getUniqueID();

        ICorporationManager manager = CorporationManager.get(((EntityPlayerMP) sender).getServerWorld());

        Corporation corp = null;
        if(manager.ownerHasCorporation(id))
        {
            corp = (Corporation) manager.getCorporationForOwner(id);
        }

        if(corp == null)
        {
            sender.sendMessage(new TextComponentTranslation("command.megacorp.noCorp"));
            return;
        }

        CorporationManager.get(((EntityPlayerMP)sender).getServerWorld()).deleteCorporationForOwner(id);

        sender.sendMessage(new TextComponentTranslation("command.megacorp.done"));
    }

    private static void dumpQuests(@Nullable MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
        throws CommandException
    {
        try
        {
            JsonArray array = new JsonArray();

            for(Quest quest : QuestManager.INSTANCE.getQuests())
            {
                array.add(quest.toJson());
            }

            String json = StringUtil.prettyPrintJson(array);

            File dumpFile = new File(CommonProxy.megacorpDirectory.getPath(), "loaded_quests.json");

            OutputStreamWriter osw = null;

            osw = new OutputStreamWriter(new FileOutputStream(dumpFile));

            osw.write(json);

            osw.close();
        }
        catch(IOException ex)
        {
            MegaCorpMod.logger.error("Error while executing dumpQuests command", ex);
            sender.sendMessage(new TextComponentTranslation("command.megacorp.error"));
        }

        sender.sendMessage(new TextComponentTranslation("command.megacorp:dumpQuests.done"));
    }
}
