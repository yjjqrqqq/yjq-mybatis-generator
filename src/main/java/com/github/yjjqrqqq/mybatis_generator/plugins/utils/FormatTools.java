package com.github.yjjqrqqq.mybatis_generator.plugins.utils;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author liuyixin
 * @date 2018/11/2919:06
 */
public class FormatTools {
    public FormatTools() {
    }

    public static void addMethodWithBestPosition(InnerClass innerClass, Method method) {
        addMethodWithBestPosition(method, innerClass.getMethods());
    }

    public static void addMethodWithBestPosition(Interface interfacz, Method method) {
        Set<FullyQualifiedJavaType> importTypes = new TreeSet();
        if (method.getReturnType() != null) {
            importTypes.add(method.getReturnType());
            importTypes.addAll(method.getReturnType().getTypeArguments());
        }

        Iterator var3 = method.getParameters().iterator();

        while(var3.hasNext()) {
            Parameter parameter = (Parameter)var3.next();
            boolean flag = true;
            Iterator var6 = parameter.getAnnotations().iterator();

            while(var6.hasNext()) {
                String annotation = (String)var6.next();
                if (annotation.startsWith("@Param")) {
                    importTypes.add(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));
                    if (annotation.matches(".*selective.*") && parameter.getType().getShortName().equals("Column")) {
                        flag = false;
                    }
                }
            }

            if (flag) {
                importTypes.add(parameter.getType());
                importTypes.addAll(parameter.getType().getTypeArguments());
            }
        }

        interfacz.addImportedTypes(importTypes);
        addMethodWithBestPosition(method, interfacz.getMethods());
    }

    public static void addMethodWithBestPosition(InnerEnum innerEnum, Method method) {
        addMethodWithBestPosition(method, innerEnum.getMethods());
    }

    public static void addMethodWithBestPosition(TopLevelClass topLevelClass, Method method) {
        addMethodWithBestPosition(method, topLevelClass.getMethods());
    }

    public static void addElementWithBestPosition(XmlElement rootElement, XmlElement element) {
        if (element.getName().equals("sql")) {
            int index = 0;
            Iterator var3 = rootElement.getElements().iterator();

            while(var3.hasNext()) {
                Element ele = (Element)var3.next();
                if (ele instanceof XmlElement && ((XmlElement)ele).getName().equals("sql")) {
                    ++index;
                }
            }

            rootElement.addElement(index, element);
        } else {
            String id = getIdFromElement(element);
            if (id == null) {
                rootElement.addElement(element);
            } else {
                List<Element> elements = rootElement.getElements();
                int index = -1;

                for(int i = 0; i < elements.size(); ++i) {
                    Element ele = (Element)elements.get(i);
                    if (ele instanceof XmlElement) {
                        String eleId = getIdFromElement((XmlElement)ele);
                        if (eleId != null) {
                            if (eleId.startsWith(id)) {
                                if (index == -1) {
                                    index = i;
                                }
                            } else if (id.startsWith(eleId)) {
                                index = i + 1;
                            }
                        }
                    }
                }

                if (index != -1 && index < elements.size()) {
                    elements.add(index, element);
                } else {
                    rootElement.addElement(element);
                }
            }
        }

    }

    private static String getIdFromElement(XmlElement element) {
        Iterator var1 = element.getAttributes().iterator();

        Attribute attribute;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            attribute = (Attribute)var1.next();
        } while(!attribute.getName().equals("id"));

        return attribute.getValue();
    }

    private static void addMethodWithBestPosition(Method method, List<Method> methods) {
        int index = -1;

        for(int i = 0; i < methods.size(); ++i) {
            Method m = (Method)methods.get(i);
            if (m.getName().equals(method.getName())) {
                if (m.getParameters().size() <= method.getParameters().size()) {
                    index = i + 1;
                } else {
                    index = i;
                }
            } else if (m.getName().startsWith(method.getName())) {
                if (index == -1) {
                    index = i;
                }
            } else if (method.getName().startsWith(m.getName())) {
                index = i + 1;
            }
        }

        if (index != -1 && index < methods.size()) {
            methods.add(index, method);
        } else {
            methods.add(methods.size(), method);
        }

    }

    public static void replaceGeneralMethodComment(CommentGenerator commentGenerator, Method method, IntrospectedTable introspectedTable) {
        method.getJavaDocLines().clear();
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
    }

    public static void replaceComment(CommentGenerator commentGenerator, XmlElement element) {
        Iterator<Element> elementIterator = element.getElements().iterator();
        boolean flag = false;

        while(elementIterator.hasNext()) {
            Element ele = (Element)elementIterator.next();
            if (ele instanceof TextElement && ((TextElement)ele).getContent().matches(".*<!--.*")) {
                flag = true;
            }

            if (flag) {
                elementIterator.remove();
            }

            if (ele instanceof TextElement && ((TextElement)ele).getContent().matches(".*-->.*")) {
                flag = false;
            }
        }

        XmlElement tmpEle = new XmlElement("tmp");
        commentGenerator.addComment(tmpEle);

        for(int i = tmpEle.getElements().size() - 1; i >= 0; --i) {
            element.addElement(0, (Element)tmpEle.getElements().get(i));
        }

    }
}
