package com.mike_caron.megacorp.impl;

import java.util.Random;

public class CorporationNameGenerator
{
    private static String[][] parts = new String[][]
    {
        new String[]
        {
            "Heavy",
            "Light",
            "Hot",
            "Cold",
            "Quick",
            "Slow",
            "Flaming",
            "Rocky",
            "Icy",
        },
        new String[]
        {
            "Iron",
            "Gold",
            "Lead",
            "Marble",
            "Cobble",
            "Diamond",
            "Emerald",
            "Granite",
            "Oak",
            "Plastic",
            "Titanium"
        },

        new String[]
        {
            "Inc.",
            "Corp.",
            "Ltd",
            "Ind.",
            "Group"
        }
    };

    public static String generateName()
    {
        StringBuilder ret = new StringBuilder();

        int used = 0;
        Random rng = new Random();

        while(used == 0)
        {
            for (int i = 0; i < parts.length - 1; i++)
            {
                if(rng.nextBoolean())
                {
                    used++;
                    if(ret.length() > 0)
                    {
                        ret.append(' ');
                    }
                    ret.append(parts[i][rng.nextInt(parts[i].length)]);
                }
            }
        }

        ret.append(' ');
        ret.append(parts[parts.length - 1][rng.nextInt(parts[parts.length - 1].length)]);

        return ret.toString();
    }
}
