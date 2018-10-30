package com.mike_caron.megacorp.util;

import java.util.Random;
import java.util.TreeMap;

public final class StringUtil
{
    private final static TreeMap<Integer, String> romanMap = new TreeMap<>();

    static {

        romanMap.put(1000, "M");
        romanMap.put(900,  "CM");
        romanMap.put(500,  "D");
        romanMap.put(400,  "CD");
        romanMap.put(100,  "C");
        romanMap.put(90,   "XC");
        romanMap.put(50,   "L");
        romanMap.put(40,   "XL");
        romanMap.put(10,   "X");
        romanMap.put(9,    "IX");
        romanMap.put(5,    "V");
        romanMap.put(4,    "IV");
        romanMap.put(1,    "I");

    }

    private StringUtil(){}

    public static String toRoman(int number) {
        if(number <= 0) return Integer.toString(number);

        int l = romanMap.floorKey(number);
        if ( number == l ) {
            return romanMap.get(number);
        }
        return romanMap.get(l) + toRoman(number-l);
    }

    public static boolean areEqual(String a, String b)
    {
        if(a == null && b == null) return true;
        if((a == null) != (b == null)) return false;

        return a.equals(b);
    }

    public static String randomString(int len)
    {
        Random r = new Random();
        StringBuilder ret = new StringBuilder();

        for(int i = 0; i < len; i++)
        {
            char c = (char)(((int)'a') + r.nextInt(26));

            ret.append(c);
        }

        return ret.toString();
    }

}
