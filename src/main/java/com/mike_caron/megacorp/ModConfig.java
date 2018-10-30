package com.mike_caron.megacorp;

import com.mike_caron.megacorp.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;

public class ModConfig
{
    private static final String CATEGORY_GENERAL = "general";

    public static String[] workorderFileBlacklist = null;
    public static String[] workorderBlacklist = null;
    public static String[] rewardBlacklist = null;

    public static boolean vendingMachineEnabled = false;
    public static boolean vendingMachineRecipeEnabled = false;

    public static void readConfig()
    {
        Configuration cfg = CommonProxy.config;

        try
        {
            cfg.load();
            initGeneralConfig(cfg);
        }
        catch(Exception ex)
        {
            MegaCorpMod.logger.error("Error loading config file", ex);
        }
        finally
        {
            if(cfg.hasChanged())
            {
                cfg.save();
            }
        }
    }

    private static void initGeneralConfig(Configuration cfg)
    {
        cfg.addCustomCategoryComment(CATEGORY_GENERAL, "General Configuration");
        workorderBlacklist = cfg.getStringList("workorderBlacklist", CATEGORY_GENERAL, new String[0], "Any work order IDs to exclude from the default list. See the rewards folder inside the jar for more details");

        workorderFileBlacklist = cfg.getStringList("workorderFileBlacklist", CATEGORY_GENERAL, new String[0], "Any files to exclude from the default list. Only specify the root name (eg, 'foo' instead of 'foo.json')");

        rewardBlacklist = cfg.getStringList("rewardBlacklist", CATEGORY_GENERAL, new String[0], "Any rewards to disable");

        vendingMachineEnabled = cfg.getBoolean("vendingMachineEnabled", CATEGORY_GENERAL, false, "Enable the vending machine block (configure in vending.json)");

        vendingMachineRecipeEnabled = cfg.getBoolean("vendingMachineRecipeEnabled", CATEGORY_GENERAL, true, "Enable the vending machine block recipe (otherwise it's creative only)");
    }
}
