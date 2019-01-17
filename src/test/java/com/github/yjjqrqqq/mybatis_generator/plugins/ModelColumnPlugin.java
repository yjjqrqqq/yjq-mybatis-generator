package com.github.yjjqrqqq.mybatis_generator.plugins;

import com.github.yjjqrqqq.mybatis_generator.plugins.utils.FormatTools;
import com.github.yjjqrqqq.mybatis_generator.plugins.utils.JavaElementGeneratorTools;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.Iterator;

/**
 * @author liuyixin
 * @date 2018/11/2919:11
 */
public class ModelColumnPlugin extends BasePlugin {
    public static final String ENUM_NAME = "Column";
    public static final String METHOD_EXCLUDES = "excludes";
    public static final String METHOD_GET_ESCAPED_COLUMN_NAME = "getEscapedColumnName";
    public static final String CONST_BEGINNING_DELIMITER = "BEGINNING_DELIMITER";
    public static final String CONST_ENDING_DELIMITER = "ENDING_DELIMITER";

    public ModelColumnPlugin() {
    }

    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addInnerEnum(this.generateColumnEnum(topLevelClass, introspectedTable));
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addInnerEnum(this.generateColumnEnum(topLevelClass, introspectedTable));
        return super.modelRecordWithBLOBsClassGenerated(topLevelClass, introspectedTable);
    }

    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addInnerEnum(this.generateColumnEnum(topLevelClass, introspectedTable));
        return super.modelPrimaryKeyClassGenerated(topLevelClass, introspectedTable);
    }

    private InnerEnum generateColumnEnum(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        InnerEnum innerEnum = new InnerEnum(new FullyQualifiedJavaType("Column"));
        innerEnum.setVisibility(JavaVisibility.PUBLIC);
        innerEnum.setStatic(true);
        this.commentGenerator.addEnumComment(innerEnum, introspectedTable);
        System.out.println("itfsw(数据Model属性对应Column获取插件):" + topLevelClass.getType().getShortName() + "增加内部Builder类。");
        Field beginningDelimiterField = JavaElementGeneratorTools.generateField("BEGINNING_DELIMITER", JavaVisibility.PRIVATE, FullyQualifiedJavaType.getStringInstance(), "\"" + StringUtility.escapeStringForJava(this.context.getBeginningDelimiter()) + "\"");
        beginningDelimiterField.setStatic(true);
        beginningDelimiterField.setFinal(true);
        this.commentGenerator.addFieldComment(beginningDelimiterField, introspectedTable);
        innerEnum.addField(beginningDelimiterField);
        Field endingDelimiterField = JavaElementGeneratorTools.generateField("ENDING_DELIMITER", JavaVisibility.PRIVATE, FullyQualifiedJavaType.getStringInstance(), "\"" + StringUtility.escapeStringForJava(this.context.getEndingDelimiter()) + "\"");
        endingDelimiterField.setStatic(true);
        endingDelimiterField.setFinal(true);
        this.commentGenerator.addFieldComment(endingDelimiterField, introspectedTable);
        innerEnum.addField(endingDelimiterField);
        Field columnField = new Field("column", FullyQualifiedJavaType.getStringInstance());
        columnField.setVisibility(JavaVisibility.PRIVATE);
        columnField.setFinal(true);
        this.commentGenerator.addFieldComment(columnField, introspectedTable);
        innerEnum.addField(columnField);
        Field isColumnNameDelimitedField = new Field("isColumnNameDelimited", FullyQualifiedJavaType.getBooleanPrimitiveInstance());
        isColumnNameDelimitedField.setVisibility(JavaVisibility.PRIVATE);
        isColumnNameDelimitedField.setFinal(true);
        this.commentGenerator.addFieldComment(isColumnNameDelimitedField, introspectedTable);
        innerEnum.addField(isColumnNameDelimitedField);
        Field javaPropertyField = new Field("javaProperty", FullyQualifiedJavaType.getStringInstance());
        javaPropertyField.setVisibility(JavaVisibility.PRIVATE);
        javaPropertyField.setFinal(true);
        this.commentGenerator.addFieldComment(javaPropertyField, introspectedTable);
        innerEnum.addField(javaPropertyField);
        Field jdbcTypeField = new Field("jdbcType", FullyQualifiedJavaType.getStringInstance());
        jdbcTypeField.setVisibility(JavaVisibility.PRIVATE);
        jdbcTypeField.setFinal(true);
        this.commentGenerator.addFieldComment(jdbcTypeField, introspectedTable);
        innerEnum.addField(jdbcTypeField);
        Method mValue = new Method("value");
        mValue.setVisibility(JavaVisibility.PUBLIC);
        mValue.setReturnType(FullyQualifiedJavaType.getStringInstance());
        mValue.addBodyLine("return this.column;");
        this.commentGenerator.addGeneralMethodComment(mValue, introspectedTable);
        FormatTools.addMethodWithBestPosition(innerEnum, mValue);
        Method mGetValue = new Method("getValue");
        mGetValue.setVisibility(JavaVisibility.PUBLIC);
        mGetValue.setReturnType(FullyQualifiedJavaType.getStringInstance());
        mGetValue.addBodyLine("return this.column;");
        this.commentGenerator.addGeneralMethodComment(mGetValue, introspectedTable);
        FormatTools.addMethodWithBestPosition(innerEnum, mGetValue);
        Method mGetJavaProperty = JavaElementGeneratorTools.generateGetterMethod(javaPropertyField);
        this.commentGenerator.addGeneralMethodComment(mGetJavaProperty, introspectedTable);
        FormatTools.addMethodWithBestPosition(innerEnum, mGetJavaProperty);
        Method mGetJdbcType = JavaElementGeneratorTools.generateGetterMethod(jdbcTypeField);
        this.commentGenerator.addGeneralMethodComment(mGetJdbcType, introspectedTable);
        FormatTools.addMethodWithBestPosition(innerEnum, mGetJdbcType);
        Method constructor = new Method("Column");
        constructor.setConstructor(true);
        constructor.addBodyLine("this.column = column;");
        constructor.addBodyLine("this.javaProperty = javaProperty;");
        constructor.addBodyLine("this.jdbcType = jdbcType;");
        constructor.addBodyLine("this.isColumnNameDelimited = isColumnNameDelimited;");
        constructor.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "column"));
        constructor.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "javaProperty"));
        constructor.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "jdbcType"));
        constructor.addParameter(new Parameter(FullyQualifiedJavaType.getBooleanPrimitiveInstance(), "isColumnNameDelimited"));
        this.commentGenerator.addGeneralMethodComment(constructor, introspectedTable);
        FormatTools.addMethodWithBestPosition(innerEnum, constructor);
        System.out.println("itfsw(数据Model属性对应Column获取插件):" + topLevelClass.getType().getShortName() + ".Column增加构造方法和column属性。");
        Iterator var15 = introspectedTable.getAllColumns().iterator();

        while (var15.hasNext()) {
            IntrospectedColumn introspectedColumn = (IntrospectedColumn) var15.next();
            Field field = JavaBeansUtil.getJavaBeansField(introspectedColumn, this.context, introspectedTable);
            StringBuffer sb = new StringBuffer();
            sb.append(field.getName());
            sb.append("(\"");
            sb.append(introspectedColumn.getActualColumnName());
            sb.append("\", \"");
            sb.append(introspectedColumn.getJavaProperty());
            sb.append("\", \"");
            sb.append(introspectedColumn.getJdbcTypeName());
            sb.append("\", ");
            sb.append(introspectedColumn.isColumnNameDelimited());
            sb.append(")");
            innerEnum.addEnumConstant(sb.toString());
            System.out.println("itfsw(数据Model属性对应Column获取插件):" + topLevelClass.getType().getShortName() + ".Column增加" + field.getName() + "枚举。");
        }

        Method desc = new Method("desc");
        desc.setVisibility(JavaVisibility.PUBLIC);
        desc.setReturnType(FullyQualifiedJavaType.getStringInstance());
        desc.addBodyLine("return this.getEscapedColumnName() + \" DESC\";");
        this.commentGenerator.addGeneralMethodComment(desc, introspectedTable);
        FormatTools.addMethodWithBestPosition(innerEnum, desc);
        Method asc = new Method("asc");
        asc.setVisibility(JavaVisibility.PUBLIC);
        asc.setReturnType(FullyQualifiedJavaType.getStringInstance());
        asc.addBodyLine("return this.getEscapedColumnName() + \" ASC\";");
        this.commentGenerator.addGeneralMethodComment(asc, introspectedTable);
        FormatTools.addMethodWithBestPosition(innerEnum, asc);
        System.out.println("itfsw(数据Model属性对应Column获取插件):" + topLevelClass.getType().getShortName() + ".Column增加asc()和desc()方法。");
        topLevelClass.addImportedType("java.util.Arrays");
        topLevelClass.addImportedType(FullyQualifiedJavaType.getNewArrayListInstance());
        Method mExcludes = JavaElementGeneratorTools.generateMethod("excludes", JavaVisibility.PUBLIC, new FullyQualifiedJavaType("Column[]"), new Parameter[]{new Parameter(innerEnum.getType(), "excludes", true)});
        this.commentGenerator.addGeneralMethodComment(mExcludes, introspectedTable);
        mExcludes.setStatic(true);
        JavaElementGeneratorTools.generateMethodBody(mExcludes, new String[]{"ArrayList<Column> columns = new ArrayList<>(Arrays.asList(Column.values()));", "if (excludes != null && excludes.length > 0) {", "columns.removeAll(new ArrayList<>(Arrays.asList(excludes)));", "}", "return columns.toArray(new Column[]{});"});
        FormatTools.addMethodWithBestPosition(innerEnum, mExcludes);
        System.out.println("itfsw(数据Model属性对应Column获取插件):" + topLevelClass.getType().getShortName() + ".Column增加excludes方法。");
        Method mGetEscapedColumnName = JavaElementGeneratorTools.generateMethod("getEscapedColumnName", JavaVisibility.PUBLIC, FullyQualifiedJavaType.getStringInstance(), new Parameter[0]);
        this.commentGenerator.addGeneralMethodComment(mGetEscapedColumnName, introspectedTable);
        JavaElementGeneratorTools.generateMethodBody(mGetEscapedColumnName, new String[]{"if (this.isColumnNameDelimited) {", "return new StringBuilder().append(BEGINNING_DELIMITER).append(this.column).append(ENDING_DELIMITER).toString();", "} else {", "return this.column;", "}"});
        FormatTools.addMethodWithBestPosition(innerEnum, mGetEscapedColumnName);
        System.out.println("itfsw(数据Model属性对应Column获取插件):" + topLevelClass.getType().getShortName() + ".Column增加getEscapedColumnName方法。");
        return innerEnum;
    }
}
