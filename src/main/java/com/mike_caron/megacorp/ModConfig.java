package com.mike_caron.megacorp;

import com.mike_caron.megacorp.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;

public class ModConfig
{
    private static final String CATEGORY_GENERAL = "general";

    public static String[] workorderBlacklist = null;

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
        cfg.getStringList("workorderBlacklist", CATEGORY_GENERAL, new String[0], "Any files to exclude from the default list. Only specify the root name (eg, 'foo' instead of 'foo.json')");
    }
}
