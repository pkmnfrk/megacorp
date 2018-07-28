package com.mike_caron.megacorp.integrations;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.fluid.ModFluids;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class EnderioCompatability
{
    private static boolean registered = false;

    public static void register()
    {
        if(registered) return;

        registered = true;

        registerRecipes();

        MegaCorpMod.logger.info("EnderIO compatibility creeping up behind you!");
    }

    private static void registerRecipes()
    {
        registerXml("<enderio:recipes xmlns:enderio=\"http://enderio.com/recipes\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://enderio.com/recipes recipes.xsd \">\n" +
                "    <recipe name=\"Money\">\n" +
                "        <alloying energy=\"20000\" exp=\"1\">\n" +
                "            <input name=\"minecraft:emerald\" amount=\"12\"/>\n" +
                "            <input name=\"minecraft:gold_block\" amount=\"12\"/>\n" +
                "            <output name=\"megacorp:ingot_money\" amount=\"1\"/>\n" +
                "        </alloying>\n" +
                "    </recipe>\n" +
                "</enderio:recipes>");
        /*registerFluid(ModFluids.MONEY, "Money");
        registerFluid(ModFluids.DENSE_MONEY, "DenseMoney");

        NBTTagList tagList = new NBTTagList();

        NBTTagCompound fluid = new NBTTagCompound();
        fluid.setString("FluidName", "money");
        fluid.setInteger("Amount", 12);
        tagList.appendTag(fluid);

        fluid = new NBTTagCompound();
        fluid.setString("FluidName", "emerald");
        fluid.setInteger("Amount", 666);
        tagList.appendTag(fluid);

        fluid = new NBTTagCompound();
        fluid.setString("FluidName", "gold");
        fluid.setInteger("Amount", 144 * 9);
        tagList.appendTag(fluid);

        NBTTagCompound message = new NBTTagCompound();
        message.setTag("alloy", tagList);
        FMLInterModComms.sendMessage("tconstruct", "alloy", message);
        */
    }

    private static void registerXml(String xml)
    {
        FMLInterModComms.sendMessage("enderio", "recipe:xml", xml);
    }
}
