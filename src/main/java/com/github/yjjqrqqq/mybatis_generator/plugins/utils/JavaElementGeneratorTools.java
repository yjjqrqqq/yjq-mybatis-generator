package com.github.yjjqrqqq.mybatis_generator.plugins.utils;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;

/**
 * @author liuyixin
 * @date 2018/11/2919:05
 */
public class JavaElementGeneratorTools {
    public JavaElementGeneratorTools() {
    }

    public static Field generateStaticFinalField(String fieldName, FullyQualifiedJavaType javaType, String initString) {
        Field field = new Field(fieldName, javaType);
        field.setVisibility(JavaVisibility.PUBLIC);
        field.setStatic(true);
        field.setFinal(true);
        if (initString != null) {
            field.setInitializationString(initString);
        }

        return field;
    }

    public static Field generateField(String fieldName, JavaVisibility visibility, FullyQualifiedJavaType javaType, String initString) {
        Field field = new Field(fieldName, javaType);
        field.setVisibility(visibility);
        if (initString != null) {
            field.setInitializationString(initString);
        }

        return field;
    }

    public static Method generateMethod(String methodName, JavaVisibility visibility, FullyQualifiedJavaType returnType, Parameter... parameters) {
        Method method = new Method(methodName);
        method.setVisibility(visibility);
        method.setReturnType(returnType);
        if (parameters != null) {
            Parameter[] var5 = parameters;
            int var6 = parameters.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                Parameter parameter = var5[var7];
                method.addParameter(parameter);
            }
        }

        return method;
    }

    public static Method generateMethodBody(Method method, String... bodyLines) {
        if (bodyLines != null) {
            String[] var2 = bodyLines;
            int var3 = bodyLines.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String bodyLine = var2[var4];
                method.addBodyLine(bodyLine);
            }
        }

        return method;
    }

    public static Method generateSetterMethod(Field field) {
        Method method = generateMethod("set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), JavaVisibility.PUBLIC, (FullyQualifiedJavaType)null, new Parameter(field.getType(), field.getName()));
        return generateMethodBody(method, "this." + field.getName() + " = " + field.getName() + ";");
    }

    public static Method generateGetterMethod(Field field) {
        Method method = generateMethod("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), JavaVisibility.PUBLIC, field.getType());
        return generateMethodBody(method, "return this." + field.getName() + ";");
    }

    public static FullyQualifiedJavaType getModelTypeWithoutBLOBs(IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType type;
        if (introspectedTable.getRules().generateBaseRecordClass()) {
            type = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        } else {
            if (!introspectedTable.getRules().generatePrimaryKeyClass()) {
                throw new RuntimeException(Messages.getString("RuntimeError.12"));
            }

            type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
        }

        return type;
    }

    public static FullyQualifiedJavaType getModelTypeWithBLOBs(IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType type;
        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            type = new FullyQualifiedJavaType(introspectedTable.getRecordWithBLOBsType());
        } else {
            type = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        }

        return type;
    }
}
