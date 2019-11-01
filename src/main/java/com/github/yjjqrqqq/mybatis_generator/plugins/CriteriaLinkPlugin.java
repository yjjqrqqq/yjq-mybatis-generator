package com.github.yjjqrqqq.mybatis_generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * @author liuyixin
 * @date 2018/12/317:26
 */
public class CriteriaLinkPlugin extends BasePlugin {
    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        System.out.println("clientGenerated:" + topLevelClass.getType().getShortName());
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);

    }
}
