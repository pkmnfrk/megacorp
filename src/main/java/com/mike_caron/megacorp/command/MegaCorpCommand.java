package com.mike_caron.megacorp.command;

import com.mike_caron.megacorp.impl.RewardManager;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class MegaCorpCommand
    implements ICommand
{
    private static final List<String> aliases = new ArrayList<>();
    private static final Map<String, TriAction<MinecraftServer, ICommandSender, String[]>> subCommands = new HashMap<>();

    static
    {
        aliases.add("megacorp");
        aliases.add("corp");

        subCommands.put("reloadRewards", MegaCorpCommand::reloadRewards);
    }

    @Nonnull
    @Override
    public String getName()
    {
        return "megacorp";
    }

    @Nonnull
    @Override
    public List<String> getAliases()
    {
        return aliases;
    }

    @Override
    public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender)
    {
        return true;
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos)
    {
        List<String> ret = new ArrayList<>();
        if(args.length == 1)
        {
            ret = subCommands.keySet().stream().filter( s -> s.startsWith(args[0])).collect(Collectors.toList());
        }
        return ret;
    }

    @Override
    public boolean isUsernameIndex(@Nonnull String[] args, int index)
    {
        return false;
    }

    @Override
    public int compareTo(@Nonnull ICommand o)
    {
        return 0;
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender)
    {
        return "Usage: megacorp " + String.join("|", subCommands.keySet());
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
    {
        if(args.length == 0 || !subCommands.containsKey(args[0]))
        {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        subCommands.get(args[0]).execute(server, sender, Arrays.stream(args).skip(1).toArray(String[]::new));

    }

    private static void reloadRewards(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
        throws CommandException
    {
        RewardManager.INSTANCE.loadRewards();
        sender.sendMessage(new TextComponentString("Done"));
    }

    interface TriAction<X,Y,Z>
    {
        void execute(@Nonnull X x, @Nonnull Y y, @Nonnull Z z)
            throws CommandException;
    }
}
