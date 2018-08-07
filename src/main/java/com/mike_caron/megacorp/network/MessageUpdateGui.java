package com.mike_caron.megacorp.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageUpdateGui implements IMessage
{
    //on server side
    IGuiUpdater gui;

    //on client side
    int id;
    NBTTagCompound tag;


    public MessageUpdateGui()
    {

    }

    public MessageUpdateGui(IGuiUpdater gui)
    {
        this.gui = gui;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf)
    {
        id = byteBuf.readInt();
        tag = ByteBufUtils.readTag(byteBuf);
    }

    @Override
    public void toBytes(ByteBuf byteBuf)
    {
        byteBuf.writeInt(this.gui.getId());
        ByteBufUtils.writeTag(byteBuf, this.gui.serializeNBT());
    }

    public static class Handler implements IMessageHandler<MessageUpdateGui, IMessage>
    {
        @Override
        public IMessage onMessage(MessageUpdateGui message, MessageContext messageContext)
        {
            final IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    Container container = Minecraft.getMinecraft().player.openContainer;
                    if(container instanceof IGuiUpdater)
                    {
                        IGuiUpdater updater = (IGuiUpdater)container;

                        int id = updater.getId();

                        if(id == message.id)
                        {
                            updater.deserializeNBT(message.tag);
                        }

                    }
                }
            });
            return null;
        }
    }
}
