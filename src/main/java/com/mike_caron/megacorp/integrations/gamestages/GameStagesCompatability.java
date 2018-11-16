package com.mike_caron.megacorp.integrations.gamestages;


import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.EntityPlayer;

public class GameStagesCompatability
{
    private static boolean registered = false;

    public static void register()
    {
        if(registered) return;

        registered = true;

        // do stuff
    }

    public boolean hasStageUnlocked(EntityPlayer player, String stage)
    {
        if(!registered) return true;

        return GameStageHelper.hasStage(player, stage);
    }


}
