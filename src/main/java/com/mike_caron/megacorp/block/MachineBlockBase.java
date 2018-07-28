package com.mike_caron.megacorp.block;

import net.minecraft.block.material.Material;

public class MachineBlockBase extends FacingBlockBase
{

    public MachineBlockBase(Material material, String name)
    {
        super(material, name);

        setHardness(10f);
        setHarvestLevel("pickaxe", 1);

    }
}
