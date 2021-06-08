package com.sadgames.sysutils.common;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.List;

public class LuaUtils {

    public static LuaValue[] luaTable2Array(LuaTable table) {
        LuaValue[] result = new LuaValue[table.length()];

        for (int i = 0; i < table.length(); i++)
            result[i] = table.get(i + 1);

        return result;
    }

    public static float[] luaTable2FloatArray(LuaTable table) {
        float[] result = new float[table.length()];

        for (int i = 0; i < table.length(); i++)
            result[i] = table.get(i + 1).tofloat();

        return result;
    }

    public static LuaTable javaList2LuaTable(List list) {
        LuaTable result = new LuaTable();

        int i = 1;
        for (Object item : list) {
            result.insert(i, CoerceJavaToLua.coerce(item));
            i++;
        }

        return result;
    }

    public static Object getUserData(LuaValue value, Class type) {
        return value.touserdata(type);
    }


}
