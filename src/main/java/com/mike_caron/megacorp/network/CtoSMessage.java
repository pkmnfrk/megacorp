package com.mike_caron.megacorp.network;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.TileEntityOwnedBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CtoSMessage implements IMessage
{
    //private int dim;
    private BlockPos pos;
    private KindEnum kind;
    private int guiElement;
    private String theString;

    // add message-specific fields here

    public CtoSMessage() {}

    public static CtoSMessage forGuiButton(BlockPos pos, int guiElement)
    {
        CtoSMessage ret = new CtoSMessage();
        ret.kind = KindEnum.GuiButton;
        //ret.dim = dim;
        ret.pos = pos;
        ret.guiElement = guiElement;
        return ret;
    }

    public static CtoSMessage forGuiString(BlockPos pos, int guiElement, String string)
    {
        CtoSMessage ret = new CtoSMessage();
        ret.kind = KindEnum.GuiString;
        //ret.dim = dim;
        ret.pos = pos;
        ret.guiElement = guiElement;
        ret.theString = string;
        return ret;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int o = buf.readInt();
        kind = KindEnum.values()[o];
        //dim = buf.readInt();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        switch(kind)
        {
            case GuiButton:
                guiElement = buf.readInt();
                break;
            case GuiString:
                guiElement = buf.readInt();
                theString = readString(buf);
                break;
            default:
                throw new RuntimeException("What the? What kind of kind is " + kind);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(kind.ordinal());
        //buf.writeInt(dim);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        switch(kind)
        {
            case GuiButton:
                buf.writeInt(guiElement);
                break;
            case GuiString:
                buf.writeInt(guiElement);
                writeString(buf, theString);
                break;
            default:
                throw new RuntimeException("What the? What kind of kind is " + kind);
        }
    }

    public BlockPos getPos() { return pos; }

    public int getGuiElement() { return guiElement; }

    public String getString() { return theString; }

    public KindEnum getKind() { return kind; }

    public enum KindEnum {
        Unknown,
        GuiButton,
        GuiString
    }

    private static String readString(ByteBuf byteBuf)
    {
        return ByteBufUtils.readUTF8String(byteBuf);
    }

    private static void writeString(ByteBuf byteBuf, String string)
    {
        ByteBufUtils.writeUTF8String(byteBuf, string);
    }

    public static class Handler implements IMessageHandler<CtoSMessage, IMessage>
    {
        @Override
        public IMessage onMessage(final CtoSMessage message, MessageContext ctx)
        {

            final EntityPlayerMP player = ctx.getServerHandler().player;
            final World world = player.world;

            final IThreadListener mainThread = (WorldServer)world;
            mainThread.addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    try {
                        if(world.isBlockLoaded(message.getPos()))
                        {
                            TileEntity te = world.getTileEntity(message.getPos());

                            switch (message.getKind())
                            {
                                case GuiButton:
                                {
                                    TileEntityOwnedBase teb = (TileEntityOwnedBase) te;
                                    teb.handleGuiButton(player, message.getGuiElement());
                                }
                                break;
                                case GuiString:
                                {
                                    TileEntityOwnedBase teb = (TileEntityOwnedBase) te;
                                    teb.handleGuiString(player, message.getGuiElement(), message.getString());
                                }
                                break;
                            }
                        }

                    }
                    catch (Exception e)
                    {
                        MegaCorpMod.logger.error("Error while handling message", e);
                    }
                }
            });

            return null;
        }
    }
}
