package com.github.yjjqrqqq.mybatis_generator.plugins;

import com.github.yjjqrqqq.mybatis_generator.plugins.utils.JavaFileUtil;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liuyixin
 * @date 2018/11/2917:57
 */
public class RenamePlugin extends PluginAdapter {
    public static String projectName = "dao";
    public static final String VALIDATION_ERROR_MESSAGE = "ValidationError.18";
    public static final String CLASS_SEARCH_PROPERTY = "classMethodSearchString";
    public static final String CLASS_REPLACE_PROPERTY = "classMethodReplaceString";
    public static final String PARAM_SEARCH_PROPERTY = "parameterSearchString";
    public static final String PARAM_REPLACE_PROPERTY = "parameterReplaceString";
    static final String XML_ID_ATTRIBUTE = "id";
    private static final String BASE_SUFFIX = "Base";

    private String classMethodReplaceString;
    private Pattern classMethodPattern;
    private String parameterReplaceString;
    private Pattern parameterPattern;

    private final FullyQualifiedJavaType serializable;

    public RenamePlugin() {
        super();
        serializable = new FullyQualifiedJavaType("java.io.Serializable");
    }

    @Override
    public boolean validate(List<String> warnings) {
        String classMethodSearchString = properties.getProperty(CLASS_SEARCH_PROPERTY);
        classMethodReplaceString = properties.getProperty(CLASS_REPLACE_PROPERTY);
        System.out.println(getContext().getTargetRuntime());
        String parameterSearchString = properties.getProperty(PARAM_SEARCH_PROPERTY);
        parameterReplaceString = properties.getProperty(PARAM_REPLACE_PROPERTY);
        classMethodPattern = Pattern.compile(classMethodSearchString);
        parameterPattern = Pattern.compile(parameterSearchString);
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        String oldType = introspectedTable.getExampleType();
        Matcher matcher = classMethodPattern.matcher(oldType);
        oldType = matcher.replaceAll(classMethodReplaceString);
        introspectedTable.setExampleType(oldType);
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(
            IntrospectedTable introspectedTable) {
        return null;
    }

    @Override
    //重写dao
    public boolean clientGenerated(Interface interfaze,
                                   TopLevelClass topLevelClass,
                                   IntrospectedTable introspectedTable) {
        try {
            if (interfaze == null) {
                return true;
            }
            generateSuperInterface(interfaze);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
                                              IntrospectedTable introspectedTable) {
        return true;
    }

    private boolean isset(String s) {
        return s != null && s.trim().length() > 0;
    }

    private void generateSuperInterface(Interface inz) throws IOException {
        String superClassFullName = inz.getType().getPackageName() + "." + BASE_SUFFIX.toLowerCase() + "." + inz.getType().getShortName() + BASE_SUFFIX;
        File file = new File(projectName + "/src/main/java/" + superClassFullName.replaceAll("\\.", "/") + ".java");
        Interface superClassTemplate = new Interface(superClassFullName);
        System.out.println(superClassFullName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        superClassTemplate.addSuperInterface(serializable);
        superClassTemplate.addImportedType(serializable);
        for (Method method : inz.getMethods()) {
            superClassTemplate.addMethod(method);
        }
        for (Field field : inz.getFields()) {
            superClassTemplate.addField(field);
        }
        for (FullyQualifiedJavaType fullyQualifiedJavaType : inz.getImportedTypes()) {
            superClassTemplate.addImportedType(fullyQualifiedJavaType);
        }
        superClassTemplate.setVisibility(JavaVisibility.PUBLIC);
        file.delete();
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(superClassTemplate.getFormattedContent());
        fileWriter.close();
        inz.getFields().clear();
        inz.getMethods().clear();
        inz.getImportedTypes().clear();
        inz.addImportedType(new FullyQualifiedJavaType(superClassFullName));
        inz.addSuperInterface(new FullyQualifiedJavaType(inz.getType().getShortName() + BASE_SUFFIX));
    }

    boolean methodDeal(Method method) {//方法处理。
        String oldMethodName = method.getName();
        Matcher matcher = classMethodPattern.matcher(oldMethodName);
        String newMethodName = matcher.replaceAll(classMethodReplaceString);
        method.setName(newMethodName);

        for (int i = 0; i < method.getParameters().size(); i++) {
            Parameter parameter = method.getParameters().get(i);
            String oldParamName = parameter.getName();
            matcher = parameterPattern.matcher(oldParamName);
            if (matcher.lookingAt()) {
                String newName = matcher.replaceAll(parameterReplaceString);
                Parameter newParam = new Parameter(parameter.getType(), newName, parameter.isVarargs());
                for (String annotation : parameter.getAnnotations()) {
                    newParam.addAnnotation(annotation);
                }
                method.getParameters().set(i, newParam);
            }
        }
        return true;
    }

    boolean renameElement(XmlElement element) {
        for (int i = 0; i < element.getAttributes().size(); i++) {
            Attribute attribute = element.getAttributes().get(i);
            if (XML_ID_ATTRIBUTE.equals(attribute.getName())) {
                String oldValue = attribute.getValue();
                Matcher matcher = classMethodPattern.matcher(oldValue);
                String newValue = matcher.replaceAll(classMethodReplaceString);
                Attribute newAtt = new Attribute(attribute.getName(), newValue);
                element.getAttributes().set(i, newAtt);
            }
        }

        return true;
    }

    /**
     * Remove the id columns from the sql statement. Useful when the generated update statement is trying to update an
     * id column.
     *
     * @param introspectedTable the table
     * @param element           the element
     */
    void removeIdColumns(IntrospectedTable introspectedTable, XmlElement element) {
        List<String> updates = new ArrayList<>();

        String alias = introspectedTable.getTableConfiguration().getAlias();
        if (alias == null) {
            alias = "";
        } else {
            alias = alias + ".";
        }

        List<IntrospectedColumn> ids = introspectedTable.getPrimaryKeyColumns();
        for (IntrospectedColumn column : ids) {
            String typeHandler = column.getTypeHandler() != null ? String.format(",typeHandler=%s", column.getTypeHandler()) : "";
            String update = String.format("%4$s%1$s = #{record.%2$s,jdbcType=%3$s%5$s},", column.getActualColumnName(),
                    column.getJavaProperty(), column.getJdbcTypeName(), alias, typeHandler);
            updates.add(update);
        }

        if (!updates.isEmpty()) {
            removeIdColumns(updates, element, null, -1);
        }
    }

    /**
     * Remove the id columns from the sql statement. Useful when the generated update statement is trying to update an
     * id column.
     *
     * @param updates the update statements
     * @param element the element
     * @param parent  the parent element
     * @param index   the index of the element in the parent list
     */
    void removeIdColumns(List<String> updates, Element element, XmlElement parent, int index) {

        if (element instanceof TextElement) {
            TextElement textElement = (TextElement) element;
            for (String update : updates) {
                if (textElement.getContent().contains(update)) {
                    TextElement newElement = new TextElement(textElement.getContent().replace(update, ""));
                    parent.getElements().set(index, newElement);
                }
            }
        } else if (element instanceof XmlElement) {
            XmlElement xmlElement = (XmlElement) element;
            for (int i = 0; i < xmlElement.getElements().size(); i++) {
                Element e = xmlElement.getElements().get(i);
                removeIdColumns(updates, e, xmlElement, i);
            }
        }
    }

    @Override
    public boolean clientCountByExampleMethodGenerated(Method method, Interface interfaze,
                                                       IntrospectedTable introspectedTable) {
        return methodDeal(method);
    }

    @Override
    public boolean clientCountByExampleMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                       IntrospectedTable introspectedTable) {
        return methodDeal(method);
    }

    @Override
    public boolean clientDeleteByExampleMethodGenerated(Method method, Interface interfaze,
                                                        IntrospectedTable introspectedTable) {
        return methodDeal(method);
    }

    @Override
    public boolean clientDeleteByExampleMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                        IntrospectedTable introspectedTable) {
        return methodDeal(method);
    }

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze,
                                                           IntrospectedTable introspectedTable) {
        return methodDeal(method);
    }

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                           IntrospectedTable introspectedTable) {
        return methodDeal(method);
    }

    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze,
                                                                 IntrospectedTable introspectedTable) {
        return methodDeal(method);
    }

    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                 IntrospectedTable introspectedTable) {
        return methodDeal(method);
    }

    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze,
                                                                    IntrospectedTable introspectedTable) {
        return methodDeal(method);
    }

    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                    IntrospectedTable introspectedTable) {
        return methodDeal(method);
    }

    @Override
    public boolean clientUpdateByExampleSelectiveMethodGenerated(Method method, Interface interfaze,
                                                                 IntrospectedTable introspectedTable) {
        return methodDeal(method);
    }

    @Override
    public boolean clientUpdateByExampleSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                 IntrospectedTable introspectedTable) {
        return methodDeal(method);
    }

    @Override
    public boolean clientUpdateByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze,
                                                                 IntrospectedTable introspectedTable) {
        return methodDeal(method);
    }

    @Override
    public boolean clientUpdateByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                 IntrospectedTable introspectedTable) {
        return methodDeal(method);
    }

    @Override
    public boolean clientUpdateByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze,
                                                                    IntrospectedTable introspectedTable) {
        return methodDeal(method);
    }

    @Override
    public boolean clientUpdateByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                    IntrospectedTable introspectedTable) {
        return methodDeal(method);
    }

    @Override
    public boolean sqlMapCountByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return renameElement(element);
    }

    @Override
    public boolean sqlMapDeleteByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return renameElement(element);
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
                                                                     IntrospectedTable introspectedTable) {
        return renameElement(element);
    }

    @Override
    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element,
                                                                  IntrospectedTable introspectedTable) {
        return renameElement(element);
    }

    @Override
    public boolean sqlMapUpdateByExampleSelectiveElementGenerated(XmlElement element,
                                                                  IntrospectedTable introspectedTable) {
        removeIdColumns(introspectedTable, element);
        return renameElement(element);
    }

    @Override
    public boolean sqlMapUpdateByExampleWithBLOBsElementGenerated(XmlElement element,
                                                                  IntrospectedTable introspectedTable) {
        removeIdColumns(introspectedTable, element);
        return renameElement(element);
    }

    @Override
    public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(XmlElement element,
                                                                     IntrospectedTable introspectedTable) {
        removeIdColumns(introspectedTable, element);
        return renameElement(element);
    }


}
