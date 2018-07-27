package com.mike_caron.mod_template.network;

import com.mike_caron.mod_template.ModTemplateMod;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketHandlerServer implements IMessageHandler<CtoSMessage, IMessage>
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
                    TileEntity te = world.getTileEntity(message.getPos());

                    switch (message.getKind())
                    {
                        /*
                        case PowerDelta:
                            if(te instanceof TransmutationGeneratorTileEntity)
                            {
                                TransmutationGeneratorTileEntity gen = (TransmutationGeneratorTileEntity)te;

                                gen.setPowerPerTick(gen.getPowerPerTick() + message.getPowerDelta());
                            }
                            break;
                        case OnOff:
                            if(te instanceof TransmutationGeneratorTileEntity)
                            {
                                ((TransmutationGeneratorTileEntity)te).setGenerating(message.getOnOff());
                            }
                            break;
                            */
                    }

                }
                catch (Exception e)
                {
                    ModTemplateMod.logger.error("Error while handling message", e);
                }
            }
        });

        return null;
    }
}
