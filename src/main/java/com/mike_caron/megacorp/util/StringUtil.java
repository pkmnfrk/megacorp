package com.mike_caron.megacorp.util;

public final class StringUtil
{
    private StringUtil(){}

    public static boolean areEqual(String a, String b)
    {
        if(a == null && b == null) return true;
        if((a == null) != (b == null)) return false;

        return a.equals(b);
    }
}
