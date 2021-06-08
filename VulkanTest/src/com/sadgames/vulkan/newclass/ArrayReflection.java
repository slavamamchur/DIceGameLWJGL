//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sadgames.vulkan.newclass;


public final class ArrayReflection {
    public ArrayReflection() {
    }

    public static Object newInstance(Class c, int size) {
        return ReflectionCache.newArray(c, size);
    }

    public static int getLength(Object array) {
        return ReflectionCache.getType(array.getClass()).getArrayLength(array);
    }

    public static Object get(Object array, int index) {
        return ReflectionCache.getType(array.getClass()).getArrayElement(array, index);
    }

    public static void set(Object array, int index, Object value) {
        ReflectionCache.getType(array.getClass()).setArrayElement(array, index, value);
    }
}
