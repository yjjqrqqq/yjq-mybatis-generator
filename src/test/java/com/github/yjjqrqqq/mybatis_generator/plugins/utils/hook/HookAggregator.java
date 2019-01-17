/*
 * Copyright (c) 2018.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.yjjqrqqq.mybatis_generator.plugins.utils.hook;

import com.github.yjjqrqqq.mybatis_generator.plugins.utils.BeanUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * ---------------------------------------------------------------------------
 * <p>
 * ---------------------------------------------------------------------------
 *
 * @author: hewei
 * @time:2018/4/27 11:33
 * ---------------------------------------------------------------------------
 */
public class HookAggregator implements IUpsertPluginHook,
        IModelBuilderPluginHook,
        IIncrementsPluginHook,
        IOptimisticLockerPluginHook,
        ISelectOneByExamplePluginHook,
        ITableConfigurationHook,
        ILombokPluginHook,
        ILogicalDeletePluginHook {

    private final static HookAggregator instance = new HookAggregator();
    private Context context;

    /**
     * constructor
     */
    public HookAggregator() {
    }

    /**
     * Getter method for property <tt>instance</tt>.
     *
     * @return property value of instance
     * @author hewei
     */
    public static HookAggregator getInstance() {
        return instance;
    }

    /**
     * Setter method for property <tt>context</tt>.
     *
     * @param context value to be assigned to property context
     * @author hewei
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * 获取插件
     *
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> List<T> getPlugins(Class<T> clazz) {
        List list = new ArrayList();
        // 反射获取插件列表，不能用单例去弄，不然因为类释放的问题而导致测试用例出问题
        try {
            List<Plugin> plugins = (List<Plugin>) BeanUtils.getProperty(this.context.getPlugins(), "plugins");
            for (Plugin plugin : plugins) {
                if (clazz.isInstance(plugin)) {
                    list.add(plugin);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
//            logger.error("获取插件列表失败！", e);
        }
        return list;
    }

    // ============================================= IIncrementsPluginHook ==============================================

    @Override
    public List<Element> incrementSetElementGenerated(IntrospectedColumn introspectedColumn, String prefix, boolean hasComma) {
        if (this.getPlugins(IIncrementsPluginHook.class).isEmpty()) {
            return new ArrayList<>();
        } else {
            return this.getPlugins(IIncrementsPluginHook.class).get(0).incrementSetElementGenerated(introspectedColumn, prefix, hasComma);
        }
    }

    @Override
    public Element incrementSetsWithSelectiveEnhancedPluginElementGenerated(IntrospectedColumn versionColumn) {
        if (this.getPlugins(IIncrementsPluginHook.class).isEmpty()) {
            return null;
        } else {
            return this.getPlugins(IIncrementsPluginHook.class).get(0).incrementSetsWithSelectiveEnhancedPluginElementGenerated(versionColumn);
        }
    }

    // ============================================ IModelBuilderPluginHook =============================================

    @Override
    public boolean modelBuilderClassGenerated(TopLevelClass topLevelClass, InnerClass builderClass, List<IntrospectedColumn> columns, IntrospectedTable introspectedTable) {
        for (IModelBuilderPluginHook plugin : this.getPlugins(IModelBuilderPluginHook.class)) {
            if (!plugin.modelBuilderClassGenerated(topLevelClass, builderClass, columns, introspectedTable)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean modelBuilderSetterMethodGenerated(Method method, TopLevelClass topLevelClass, InnerClass builderClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable) {
        for (IModelBuilderPluginHook plugin : this.getPlugins(IModelBuilderPluginHook.class)) {
            if (!plugin.modelBuilderSetterMethodGenerated(method, topLevelClass, builderClass, introspectedColumn, introspectedTable)) {
                return false;
            }
        }
        return true;
    }

    // ================================================= IUpsertPluginHook ===============================================

    @Override
    public boolean clientUpsertSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        for (IUpsertPluginHook plugin : this.getPlugins(IUpsertPluginHook.class)) {
            if (!plugin.clientUpsertSelectiveMethodGenerated(method, interfaze, introspectedTable)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean clientUpsertByExampleSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        for (IUpsertPluginHook plugin : this.getPlugins(IUpsertPluginHook.class)) {
            if (!plugin.clientUpsertByExampleSelectiveMethodGenerated(method, interfaze, introspectedTable)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean sqlMapUpsertSelectiveElementGenerated(XmlElement element, List<IntrospectedColumn> columns, XmlElement insertColumnsEle, XmlElement insertValuesEle, XmlElement setsEle, IntrospectedTable introspectedTable) {
        for (IUpsertPluginHook plugin : this.getPlugins(IUpsertPluginHook.class)) {
            if (!plugin.sqlMapUpsertSelectiveElementGenerated(element, columns, insertColumnsEle, insertValuesEle, setsEle, introspectedTable)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean sqlMapUpsertByExampleSelectiveElementGenerated(XmlElement element, List<IntrospectedColumn> columns, XmlElement insertColumnsEle, XmlElement insertValuesEle, XmlElement setsEle, IntrospectedTable introspectedTable) {
        for (IUpsertPluginHook plugin : this.getPlugins(IUpsertPluginHook.class)) {
            if (!plugin.sqlMapUpsertByExampleSelectiveElementGenerated(element, columns, insertColumnsEle, insertValuesEle, setsEle, introspectedTable)) {
                return false;
            }
        }
        return true;
    }

    // ================================================= IOptimisticLockerPluginHook ===============================================

    @Override
    public boolean clientUpdateWithVersionByExampleSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        for (IOptimisticLockerPluginHook plugin : this.getPlugins(IOptimisticLockerPluginHook.class)) {
            if (!plugin.clientUpdateWithVersionByExampleSelectiveMethodGenerated(method, interfaze, introspectedTable)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean clientUpdateWithVersionByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        for (IOptimisticLockerPluginHook plugin : this.getPlugins(IOptimisticLockerPluginHook.class)) {
            if (!plugin.clientUpdateWithVersionByPrimaryKeySelectiveMethodGenerated(method, interfaze, introspectedTable)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean generateSetsSelectiveElement(List<IntrospectedColumn> columns, IntrospectedColumn versionColumn, XmlElement setsElement) {
        if (this.getPlugins(IOptimisticLockerPluginHook.class).isEmpty()) {
            return false;
        } else {
            return this.getPlugins(IOptimisticLockerPluginHook.class).get(0).generateSetsSelectiveElement(columns, versionColumn, setsElement);
        }
    }

    // ============================================= ISelectOneByExamplePluginHook ==============================================

    @Override
    public boolean clientSelectOneByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        for (ISelectOneByExamplePluginHook plugin : this.getPlugins(ISelectOneByExamplePluginHook.class)) {
            if (!plugin.clientSelectOneByExampleWithBLOBsMethodGenerated(method, interfaze, introspectedTable)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean clientSelectOneByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        for (ISelectOneByExamplePluginHook plugin : this.getPlugins(ISelectOneByExamplePluginHook.class)) {
            if (!plugin.clientSelectOneByExampleWithoutBLOBsMethodGenerated(method, interfaze, introspectedTable)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean sqlMapSelectOneByExampleWithoutBLOBsElementGenerated(Document document, XmlElement element, IntrospectedTable introspectedTable) {
        for (ISelectOneByExamplePluginHook plugin : this.getPlugins(ISelectOneByExamplePluginHook.class)) {
            if (!plugin.sqlMapSelectOneByExampleWithoutBLOBsElementGenerated(document, element, introspectedTable)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean sqlMapSelectOneByExampleWithBLOBsElementGenerated(Document document, XmlElement element, IntrospectedTable introspectedTable) {
        for (ISelectOneByExamplePluginHook plugin : this.getPlugins(ISelectOneByExamplePluginHook.class)) {
            if (!plugin.sqlMapSelectOneByExampleWithBLOBsElementGenerated(document, element, introspectedTable)) {
                return false;
            }
        }
        return true;
    }

    // ============================================= ITableConfigurationHook ==============================================

    @Override
    public void tableConfiguration(IntrospectedTable introspectedTable) {
        if (!this.getPlugins(ITableConfigurationHook.class).isEmpty()) {
            this.getPlugins(ITableConfigurationHook.class).get(0).tableConfiguration(introspectedTable);
        }
    }

    // ============================================= ILogicalDeletePluginHook ==============================================

    @Override
    public boolean clientLogicalDeleteByExampleMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        for (ILogicalDeletePluginHook plugin : this.getPlugins(ILogicalDeletePluginHook.class)) {
            if (!plugin.clientLogicalDeleteByExampleMethodGenerated(method, interfaze, introspectedTable)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean clientLogicalDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        for (ILogicalDeletePluginHook plugin : this.getPlugins(ILogicalDeletePluginHook.class)) {
            if (!plugin.clientLogicalDeleteByPrimaryKeyMethodGenerated(method, interfaze, introspectedTable)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean sqlMapLogicalDeleteByExampleElementGenerated(Document document, XmlElement element, IntrospectedColumn logicalDeleteColumn, String logicalDeleteValue, IntrospectedTable introspectedTable) {
        for (ILogicalDeletePluginHook plugin : this.getPlugins(ILogicalDeletePluginHook.class)) {
            if (!plugin.sqlMapLogicalDeleteByExampleElementGenerated(document, element, logicalDeleteColumn, logicalDeleteValue, introspectedTable)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean sqlMapLogicalDeleteByPrimaryKeyElementGenerated(Document document, XmlElement element, IntrospectedColumn logicalDeleteColumn, String logicalDeleteValue, IntrospectedTable introspectedTable) {
        for (ILogicalDeletePluginHook plugin : this.getPlugins(ILogicalDeletePluginHook.class)) {
            if (!plugin.sqlMapLogicalDeleteByPrimaryKeyElementGenerated(document, element, logicalDeleteColumn, logicalDeleteValue, introspectedTable)) {
                return false;
            }
        }
        return true;
    }

    // ============================================= ILombokPluginHook ==============================================


    @Override
    public boolean modelBaseRecordBuilderClassGenerated(TopLevelClass topLevelClass, List<IntrospectedColumn> columns, IntrospectedTable introspectedTable) {
        for (ILombokPluginHook plugin : this.getPlugins(ILombokPluginHook.class)) {
            if (!plugin.modelBaseRecordBuilderClassGenerated(topLevelClass, columns, introspectedTable)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean modelPrimaryKeyBuilderClassGenerated(TopLevelClass topLevelClass, List<IntrospectedColumn> columns, IntrospectedTable introspectedTable) {
        for (ILombokPluginHook plugin : this.getPlugins(ILombokPluginHook.class)) {
            if (!plugin.modelPrimaryKeyBuilderClassGenerated(topLevelClass, columns, introspectedTable)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsBuilderClassGenerated(TopLevelClass topLevelClass, List<IntrospectedColumn> columns, IntrospectedTable introspectedTable) {
        for (ILombokPluginHook plugin : this.getPlugins(ILombokPluginHook.class)) {
            if (!plugin.modelRecordWithBLOBsBuilderClassGenerated(topLevelClass, columns, introspectedTable)) {
                return false;
            }
        }
        return true;
    }

}
