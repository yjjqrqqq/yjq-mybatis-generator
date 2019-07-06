package com.github.yjjqrqqq.mybatis_generator.plugins.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author liuyixin
 * @date 2018/11/2919:09
 */
public class BeanUtils {
    public BeanUtils() {
    }

    public static void setProperty(Object bean, String name, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = bean.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(bean, value);
    }

    public static Object getProperty(Object bean, String name) throws NoSuchFieldException, IllegalAccessException {
        Field field = bean.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(bean);
    }

    public static Object invoke(Object bean, Class clazz, String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = clazz.getDeclaredMethod(name);
        method.setAccessible(true);
        return method.invoke(bean);
    }
}
