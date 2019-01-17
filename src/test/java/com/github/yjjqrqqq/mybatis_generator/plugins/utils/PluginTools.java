package com.github.yjjqrqqq.mybatis_generator.plugins.utils;

import com.github.yjjqrqqq.mybatis_generator.plugins.utils.hook.HookAggregator;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.PluginConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuyixin
 * @date 2018/11/2919:08
 */
public class PluginTools {

    public PluginTools() {
    }

    public static <T> T getHook(Class<T> clazz) {
        return (T) HookAggregator.getInstance();
    }

    public static boolean checkDependencyPlugin(Context context, Class... plugins) {
        Class[] var2 = plugins;
        int var3 = plugins.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            Class plugin = var2[var4];
            if (getPluginIndex(context, plugin) < 0) {
                return false;
            }
        }

        return true;
    }

    public static int getPluginIndex(Context context, Class plugin) {
        List<PluginConfiguration> list = getConfigPlugins(context);

        for (int i = 0; i < list.size(); ++i) {
            PluginConfiguration config = (PluginConfiguration) list.get(i);
            if (plugin.getName().equals(config.getConfigurationType())) {
                return i;
            }
        }

        return -1;
    }

    public static List<PluginConfiguration> getConfigPlugins(Context ctx) {
        try {
            return (List) BeanUtils.getProperty(ctx, "pluginConfigurations");
        } catch (Exception var2) {
            System.out.println("插件检查反射异常" + var2);
            return new ArrayList();
        }
    }

    public static PluginConfiguration getPluginConfiguration(Context context, Class plugin) {
        int index = getPluginIndex(context, plugin);
        return index > -1 ? (PluginConfiguration) getConfigPlugins(context).get(index) : null;
    }
}
