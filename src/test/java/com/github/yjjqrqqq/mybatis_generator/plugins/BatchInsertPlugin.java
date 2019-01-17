package com.github.yjjqrqqq.mybatis_generator.plugins;

import com.github.yjjqrqqq.mybatis_generator.plugins.utils.FormatTools;
import com.github.yjjqrqqq.mybatis_generator.plugins.utils.JavaElementGeneratorTools;
import com.github.yjjqrqqq.mybatis_generator.plugins.utils.PluginTools;
import com.github.yjjqrqqq.mybatis_generator.plugins.utils.XmlElementGeneratorTools;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author liuyixin
 * @date 2018/11/2919:07
 */
public class BatchInsertPlugin extends BasePlugin {
    public static final String METHOD_BATCH_INSERT = "batchInsert";
    public static final String METHOD_BATCH_INSERT_SELECTIVE = "batchInsertSelective";
    public static final String PRO_ALLOW_MULTI_QUERIES = "allowMultiQueries";
    private boolean allowMultiQueries = false;

    public BatchInsertPlugin() {
    }

    public boolean validate(List<String> warnings) {
        if (!"com.mysql.jdbc.Driver".equalsIgnoreCase(this.getContext().getJdbcConnectionConfiguration().getDriverClass()) && !"com.microsoft.jdbc.sqlserver.SQLServer".equalsIgnoreCase(this.getContext().getJdbcConnectionConfiguration().getDriverClass()) && !"com.microsoft.sqlserver.jdbc.SQLServerDriver".equalsIgnoreCase(this.getContext().getJdbcConnectionConfiguration().getDriverClass()) && !"com.mysql.cj.jdbc.Driver".equalsIgnoreCase(this.getContext().getJdbcConnectionConfiguration().getDriverClass())) {
            warnings.add("itfsw:插件" + this.getClass().getTypeName() + "插件使用前提是数据库为MySQL或者SQLserver，因为返回主键使用了JDBC的getGenereatedKeys方法获取主键！");
            return false;
        } else if (!PluginTools.checkDependencyPlugin(this.getContext(), new Class[]{ModelColumnPlugin.class})) {
            warnings.add("itfsw:插件" + this.getClass().getTypeName() + "插件需配合com.itfsw.com.github.yjjqrqqq.mybatis_generator.plugins.generator.plugins.ModelColumnPlugin插件使用！");
            return false;
        } else {
            Properties properties = this.getProperties();
            String allowMultiQueries = properties.getProperty("allowMultiQueries");
            this.allowMultiQueries = allowMultiQueries == null ? false : StringUtility.isTrue(allowMultiQueries);
            if (this.allowMultiQueries) {
                warnings.add("itfsw:插件" + this.getClass().getTypeName() + "插件您开启了allowMultiQueries支持，注意在jdbc url 配置中增加“allowMultiQueries=true”支持（不怎么建议使用该功能，开启多sql提交会增加sql注入的风险，请确保你所有sql都使用MyBatis书写，请不要使用statement进行sql提交）！");
            }

            return super.validate(warnings);
        }
    }

    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType listType = FullyQualifiedJavaType.getNewListInstance();
        listType.addTypeArgument(introspectedTable.getRules().calculateAllFieldsClass());
        Method mBatchInsert = JavaElementGeneratorTools.generateMethod("batchInsert", JavaVisibility.DEFAULT, FullyQualifiedJavaType.getIntInstance(), new Parameter[]{new Parameter(listType, "list", "@Param(\"list\")")});
        this.commentGenerator.addGeneralMethodComment(mBatchInsert, introspectedTable);
        FormatTools.addMethodWithBestPosition(interfaze, mBatchInsert);
        System.out.println("itfsw(批量插入插件):" + interfaze.getType().getShortName() + "增加batchInsert方法。");
        FullyQualifiedJavaType selectiveType = new FullyQualifiedJavaType(introspectedTable.getRules().calculateAllFieldsClass().getShortName() + "." + "Column");
        Method mBatchInsertSelective = JavaElementGeneratorTools.generateMethod("batchInsertSelective", JavaVisibility.DEFAULT, FullyQualifiedJavaType.getIntInstance(), new Parameter[]{new Parameter(listType, "list", "@Param(\"list\")"), new Parameter(selectiveType, "selective", "@Param(\"selective\")", true)});
        this.commentGenerator.addGeneralMethodComment(mBatchInsertSelective, introspectedTable);
        FormatTools.addMethodWithBestPosition(interfaze, mBatchInsertSelective);
        System.out.println("itfsw(批量插入插件):" + interfaze.getType().getShortName() + "增加batchInsertSelective方法。");
        return true;
    }

    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement batchInsertEle = new XmlElement("insert");
        batchInsertEle.addAttribute(new Attribute("id", "batchInsert"));
        batchInsertEle.addAttribute(new Attribute("parameterType", "map"));
        this.commentGenerator.addComment(batchInsertEle);
        XmlElementGeneratorTools.useGeneratedKeys(batchInsertEle, introspectedTable);
        batchInsertEle.addElement(new TextElement("insert into " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        Iterator var4 = XmlElementGeneratorTools.generateKeys(ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns()), true).iterator();

        while (var4.hasNext()) {
            Element element = (Element) var4.next();
            batchInsertEle.addElement(element);
        }

        XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("collection", "list"));
        foreachElement.addAttribute(new Attribute("item", "item"));
        foreachElement.addAttribute(new Attribute("separator", ","));
        Iterator var13 = XmlElementGeneratorTools.generateValues(ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns()), "item.").iterator();

        while (var13.hasNext()) {
            Element element = (Element) var13.next();
            foreachElement.addElement(element);
        }

        batchInsertEle.addElement(new TextElement("values"));
        batchInsertEle.addElement(foreachElement);
        document.getRootElement().addElement(batchInsertEle);
        System.out.println("itfsw(批量插入插件):" + introspectedTable.getMyBatis3XmlMapperFileName() + "增加batchInsert实现方法。");
        XmlElement batchInsertSelectiveEle = new XmlElement("insert");
        this.commentGenerator.addComment(batchInsertSelectiveEle);
        batchInsertSelectiveEle.addAttribute(new Attribute("id", "batchInsertSelective"));
        batchInsertSelectiveEle.addAttribute(new Attribute("parameterType", "map"));
        XmlElementGeneratorTools.useGeneratedKeys(batchInsertSelectiveEle, introspectedTable);
        if (this.allowMultiQueries) {
            XmlElement chooseEle = new XmlElement("choose");
            XmlElement selectiveEnhancedEle = new XmlElement("when");
            selectiveEnhancedEle.addAttribute(new Attribute("test", "selective != null and selective.length > 0"));
            chooseEle.addElement(selectiveEnhancedEle);
            selectiveEnhancedEle.getElements().addAll(this.generateSelectiveEnhancedEles(introspectedTable));
            XmlElement selectiveNormalEle = new XmlElement("otherwise");
            chooseEle.addElement(selectiveNormalEle);
            XmlElement foreachEle = new XmlElement("foreach");
            selectiveNormalEle.addElement(foreachEle);
            foreachEle.addAttribute(new Attribute("collection", "list"));
            foreachEle.addAttribute(new Attribute("item", "item"));
            foreachEle.addAttribute(new Attribute("separator", ";"));
            foreachEle.addElement(new TextElement("insert into " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));
            XmlElement insertTrimElement = new XmlElement("trim");
            foreachEle.addElement(insertTrimElement);
            insertTrimElement.addElement(XmlElementGeneratorTools.generateKeysSelective(ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns()), "item."));
            foreachEle.addElement(new TextElement("values"));
            XmlElement valuesTrimElement = new XmlElement("trim");
            foreachEle.addElement(valuesTrimElement);
            valuesTrimElement.addElement(XmlElementGeneratorTools.generateValuesSelective(ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns()), "item."));
            batchInsertSelectiveEle.addElement(chooseEle);
        } else {
            batchInsertSelectiveEle.getElements().addAll(this.generateSelectiveEnhancedEles(introspectedTable));
        }

        document.getRootElement().addElement(batchInsertSelectiveEle);
        System.out.println("itfsw(批量插入插件):" + introspectedTable.getMyBatis3XmlMapperFileName() + "增加batchInsertSelective实现方法。");
        return true;
    }

    private List<Element> generateSelectiveEnhancedEles(IntrospectedTable introspectedTable) {
        List<Element> eles = new ArrayList();
        eles.add(new TextElement("insert into " + introspectedTable.getFullyQualifiedTableNameAtRuntime() + " ("));
        XmlElement foreachInsertColumns = new XmlElement("foreach");
        foreachInsertColumns.addAttribute(new Attribute("collection", "selective"));
        foreachInsertColumns.addAttribute(new Attribute("item", "column"));
        foreachInsertColumns.addAttribute(new Attribute("separator", ","));
        foreachInsertColumns.addElement(new TextElement("${column.escapedColumnName}"));
        eles.add(foreachInsertColumns);
        eles.add(new TextElement(")"));
        eles.add(new TextElement("values"));
        XmlElement foreachValues = new XmlElement("foreach");
        foreachValues.addAttribute(new Attribute("collection", "list"));
        foreachValues.addAttribute(new Attribute("item", "item"));
        foreachValues.addAttribute(new Attribute("separator", ","));
        foreachValues.addElement(new TextElement("("));
        XmlElement foreachInsertColumnsCheck = new XmlElement("foreach");
        foreachInsertColumnsCheck.addAttribute(new Attribute("collection", "selective"));
        foreachInsertColumnsCheck.addAttribute(new Attribute("item", "column"));
        foreachInsertColumnsCheck.addAttribute(new Attribute("separator", ","));
        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());
        List<IntrospectedColumn> columns1 = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());

        for (int i = 0; i < columns1.size(); ++i) {
            IntrospectedColumn introspectedColumn = (IntrospectedColumn) columns.get(i);
            XmlElement check = new XmlElement("if");
            check.addAttribute(new Attribute("test", "'" + introspectedColumn.getActualColumnName() + "'.toString() == column.value"));
            check.addElement(new TextElement(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "item.")));
            foreachInsertColumnsCheck.addElement(check);
        }

        foreachValues.addElement(foreachInsertColumnsCheck);
        foreachValues.addElement(new TextElement(")"));
        eles.add(foreachValues);
        return eles;
    }
}
