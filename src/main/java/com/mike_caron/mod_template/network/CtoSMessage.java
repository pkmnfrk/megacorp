package com.mike_caron.mod_template.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CtoSMessage implements IMessage
{
    private int dim;
    private BlockPos pos;
    private KindEnum kind;

    // add message-specific fields here

    public CtoSMessage() {}

    /*public CtoSMessage(int dim, BlockPos pos, int powerDelta)
    {
        this.dim = dim;
        this.pos = pos;

        this.kind = KindEnum.PowerDelta;
    }

    public CtoSMessage(int dim, BlockPos pos, boolean onOff)
    {
        this.dim = dim;
        this.pos = pos;

        this.kind = KindEnum.OnOff;
        this.onOff = onOff;
    }*/

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int o = buf.readInt();
        kind = KindEnum.values()[o];
        dim = buf.readInt();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        switch(kind)
        {
            /*case OnOff:
                onOff = buf.readBoolean();
                break;
            case PowerDelta:
                powerDelta = buf.readInt();
                break;*/
            default:
                throw new RuntimeException("What the? What kind of kind is " + kind);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(kind.ordinal());
        buf.writeInt(dim);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        switch(kind)
        {
            /*case OnOff:
                buf.writeBoolean(onOff);
                break;
            case PowerDelta:
                buf.writeInt(powerDelta);
                break;*/
            default:
                throw new RuntimeException("What the? What kind of kind is " + kind);
        }
    }

    public BlockPos getPos() { return pos; }

    /*public int getPowerDelta() { return powerDelta; }

    public boolean getOnOff() { return onOff; }*/

    public KindEnum getKind() { return kind; }

    public enum KindEnum {
        Unknown
    }
}
