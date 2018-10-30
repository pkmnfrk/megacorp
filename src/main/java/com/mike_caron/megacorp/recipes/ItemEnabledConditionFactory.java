package com.mike_caron.megacorp.recipes;

import com.google.gson.JsonObject;
import com.mike_caron.megacorp.ModConfig;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

public class ItemEnabledConditionFactory
    implements IConditionFactory
{
    @Override
    public BooleanSupplier parse(JsonContext jsonContext, JsonObject jsonObject)
    {
        return () -> {
            if (!jsonObject.has("item"))
                return false;

            switch(jsonObject.get("item").getAsString())
            {
                case "vending_machine":
                    return ModConfig.vendingMachineEnabled && ModConfig.vendingMachineRecipeEnabled;
            }

            return false;
        };
    }

}
