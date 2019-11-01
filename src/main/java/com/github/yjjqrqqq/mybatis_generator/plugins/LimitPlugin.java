package com.github.yjjqrqqq.mybatis_generator.plugins;

import com.github.yjjqrqqq.mybatis_generator.plugins.utils.FormatTools;
import com.github.yjjqrqqq.mybatis_generator.plugins.utils.JavaElementGeneratorTools;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.Iterator;
import java.util.List;

/**
 * @author liuyixin
 * @date 2018/11/2919:02
 */
public class LimitPlugin extends BasePlugin {
    public LimitPlugin() {
    }

    public boolean validate(List<String> warnings) {
        if (!"com.mysql.jdbc.Driver".equalsIgnoreCase(this.getContext().getJdbcConnectionConfiguration().getDriverClass()) && !"com.mysql.cj.jdbc.Driver".equalsIgnoreCase(this.getContext().getJdbcConnectionConfiguration().getDriverClass())) {
            warnings.add("itfsw:插件" + this.getClass().getTypeName() + "只支持MySQL数据库！");
            return false;
        } else {
            return super.validate(warnings);
        }
    }

    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        PrimitiveTypeWrapper integerWrapper = FullyQualifiedJavaType.getIntInstance().getPrimitiveTypeWrapper();
        Field offsetField = JavaElementGeneratorTools.generateField("offset", JavaVisibility.PROTECTED, integerWrapper, (String) null);
        this.commentGenerator.addFieldComment(offsetField, introspectedTable);
        topLevelClass.addField(offsetField);
        Field rowsField = JavaElementGeneratorTools.generateField("rows", JavaVisibility.PROTECTED, integerWrapper, (String) null);
        this.commentGenerator.addFieldComment(rowsField, introspectedTable);
        topLevelClass.addField(rowsField);
        System.out.println("itfsw(MySQL分页插件):" + topLevelClass.getType().getShortName() + "增加offset和rows字段");
        Method mSetOffset = JavaElementGeneratorTools.generateSetterMethod(offsetField);
        this.commentGenerator.addGeneralMethodComment(mSetOffset, introspectedTable);
        FormatTools.addMethodWithBestPosition(topLevelClass, mSetOffset);
        Method mGetOffset = JavaElementGeneratorTools.generateGetterMethod(offsetField);
        this.commentGenerator.addGeneralMethodComment(mGetOffset, introspectedTable);
        FormatTools.addMethodWithBestPosition(topLevelClass, mGetOffset);
        Method mSetRows = JavaElementGeneratorTools.generateSetterMethod(rowsField);
        this.commentGenerator.addGeneralMethodComment(mSetRows, introspectedTable);
        FormatTools.addMethodWithBestPosition(topLevelClass, mSetRows);
        Method mGetRows = JavaElementGeneratorTools.generateGetterMethod(rowsField);
        this.commentGenerator.addGeneralMethodComment(mGetRows, introspectedTable);
        FormatTools.addMethodWithBestPosition(topLevelClass, mGetRows);
        System.out.println("itfsw(MySQL分页插件):" + topLevelClass.getType().getShortName() + "增加offset和rows的getter和setter实现。");
        Method setLimit = JavaElementGeneratorTools.generateMethod("limit", JavaVisibility.PUBLIC, topLevelClass.getType(), new Parameter[]{new Parameter(integerWrapper, "rows")});
        this.commentGenerator.addGeneralMethodComment(setLimit, introspectedTable);
        setLimit = JavaElementGeneratorTools.generateMethodBody(setLimit, new String[]{"this.rows = rows;", "return this;"});
        FormatTools.addMethodWithBestPosition(topLevelClass, setLimit);
        Method setLimit2 = JavaElementGeneratorTools.generateMethod("limit", JavaVisibility.PUBLIC, topLevelClass.getType(), new Parameter[]{new Parameter(integerWrapper, "offset"), new Parameter(integerWrapper, "rows")});
        this.commentGenerator.addGeneralMethodComment(setLimit2, introspectedTable);
        setLimit2 = JavaElementGeneratorTools.generateMethodBody(setLimit2, new String[]{"this.offset = offset;", "this.rows = rows;", "return this;"});
        FormatTools.addMethodWithBestPosition(topLevelClass, setLimit2);
        System.out.println("itfsw(MySQL分页插件):" + topLevelClass.getType().getShortName() + "增加limit方法。");
        Method setPage = JavaElementGeneratorTools.generateMethod("page", JavaVisibility.PUBLIC, topLevelClass.getType(), new Parameter[]{new Parameter(integerWrapper, "page"), new Parameter(integerWrapper, "pageSize")});
        this.commentGenerator.addGeneralMethodComment(setPage, introspectedTable);
        setPage = JavaElementGeneratorTools.generateMethodBody(setPage, new String[]{"this.offset = page * pageSize;", "this.rows = pageSize;", "return this;"});
        FormatTools.addMethodWithBestPosition(topLevelClass, setPage);
        System.out.println("itfsw(MySQL分页插件):" + topLevelClass.getType().getShortName() + "增加page方法");
        List<Method> methodList = topLevelClass.getMethods();
        Iterator var14 = methodList.iterator();

        while (var14.hasNext()) {
            Method method = (Method) var14.next();
            if (method.getName().equals("clear")) {
                method.addBodyLine("rows = null;");
                method.addBodyLine("offset = null;");
                System.out.println("itfsw(MySQL分页插件):" + topLevelClass.getType().getShortName() + "修改clear方法,增加rows和offset字段的清空");
            }
        }

        return true;
    }

    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        this.generateLimitElement(element, introspectedTable);
        return true;
    }

    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        this.generateLimitElement(element, introspectedTable);
        return true;
    }

    public void generateLimitElement(XmlElement element, IntrospectedTable introspectedTable) {
        XmlElement ifLimitNotNullElement = new XmlElement("if");
        ifLimitNotNullElement.addAttribute(new Attribute("test", "rows != null"));
        XmlElement ifOffsetNotNullElement = new XmlElement("if");
        ifOffsetNotNullElement.addAttribute(new Attribute("test", "offset != null"));
        ifOffsetNotNullElement.addElement(new TextElement("limit ${offset}, ${rows}"));
        ifLimitNotNullElement.addElement(ifOffsetNotNullElement);
        XmlElement ifOffsetNullElement = new XmlElement("if");
        ifOffsetNullElement.addAttribute(new Attribute("test", "offset == null"));
        ifOffsetNullElement.addElement(new TextElement("limit ${rows}"));
        ifLimitNotNullElement.addElement(ifOffsetNullElement);
        element.addElement(ifLimitNotNullElement);
        System.out.println("itfsw(MySQL分页插件):" + introspectedTable.getMyBatis3XmlMapperFileName() + "selectByExample方法增加分页条件。");
    }
}
