package com.github.yjjqrqqq.mybatis_generator;

import com.github.yjjqrqqq.mybatis_generator.plugins.BaseClassPlugin;
import org.apache.commons.io.FileUtils;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuyixin
 * @date 2019/1/1717:31
 */
public class Generator {
    /**
     * 生成MyBatis代码
     *
     * @param currentProjectName 当前项目名
     * @param configFile         new File(Thread.currentThread().getContextClassLoader().getResource("mybatis-generator-config.xml").getPath());
     */
    public static void generator(String currentProjectName, File configFile) throws InvalidConfigurationException, IOException, XMLParserException, SQLException, InterruptedException {
        BaseClassPlugin.projectName = currentProjectName;
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = false;
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new MyCallBack(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
        for (String warning : warnings) {
            System.out.println(warning);
        }
    }

    public static class MyIntrospectedTable extends IntrospectedTableMyBatis3Impl {
        public MyIntrospectedTable() {
            super();
        }

        @Override
        protected void calculateModelAttributes() {
            String pakkage = calculateJavaModelPackage();
            StringBuilder sb = new StringBuilder();
            sb.append(pakkage);
            sb.append('.');
            sb.append(fullyQualifiedTable.getDomainObjectName());
            sb.append("Key"); //$NON-NLS-1$
            setPrimaryKeyType(sb.toString());

            sb.setLength(0);
            sb.append(pakkage);
            sb.append('.');
            sb.append(fullyQualifiedTable.getDomainObjectName());
            setBaseRecordType(sb.toString());

            sb.setLength(0);
            sb.append(pakkage);
            sb.append('.');
            sb.append(fullyQualifiedTable.getDomainObjectName());
            sb.append("WithBLOBs"); //$NON-NLS-1$
            setRecordWithBLOBsType(sb.toString());

            sb.setLength(0);
            sb.append(pakkage.replaceFirst("([^\\.]*)$", "criteria"));
            sb.append('.');
            //.replaceFirst("([^\\.]*)$", "criteria")
            sb.append(fullyQualifiedTable.getDomainObjectName());
            sb.append("Criteria"); //$NON-NLS-1$
            setExampleType(sb.toString());
        }

        @Override
        public List<GeneratedJavaFile> getGeneratedJavaFiles() {
            List<GeneratedJavaFile> result = super.getGeneratedJavaFiles();
            for (GeneratedJavaFile generatedJavaFile : result) {
                if (generatedJavaFile.getCompilationUnit() instanceof TopLevelClass) {
                    TopLevelClass topLevelClass = (TopLevelClass) generatedJavaFile.getCompilationUnit();
                    remainCommits(generatedJavaFile, topLevelClass);
                    if (topLevelClass.getType().getShortName().endsWith("Criteria")) {
                        InnerClass criteria = getInnerClassByName(topLevelClass, "Criteria");
                        Method method = getMethodByName(topLevelClass, "createCriteriaInternal");
                        if (criteria != null && method != null) {
                            Field owner = new Field("owner", topLevelClass.getType());
                            owner.setVisibility(JavaVisibility.PRIVATE);
                            criteria.addField(owner);
                            method.addBodyLine(method.getBodyLines().size() - 1, "criteria.owner=this;");
                            //rename  创建一个简化的createCriteria访问
                            {
                                Method createCriteria = getMethodByName(topLevelClass, "createCriteria");
                                Method simpleCreateCriteria = new Method("c");
                                simpleCreateCriteria.setReturnType(criteria.getType());
                                simpleCreateCriteria.addBodyLine("return createCriteria();");
                                simpleCreateCriteria.setVisibility(JavaVisibility.PUBLIC);
                                topLevelClass.getMethods().add(simpleCreateCriteria);
                            }

                            Method ownerGet = new Method("owner");
                            ownerGet.setReturnType(topLevelClass.getType());
                            ownerGet.addBodyLine("return owner;");
                            ownerGet.setVisibility(JavaVisibility.PUBLIC);
                            criteria.addMethod(ownerGet);

                            ownerGet = new Method(ownerGet);
                            ownerGet.setName("o");
                            criteria.addMethod(ownerGet);
                        }
                        {//ascOrderBy
                            Method ascOrderBy = new Method("ascOrderBy");
                            ascOrderBy.setVisibility(JavaVisibility.PUBLIC);
                            ascOrderBy.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "column"));
                            ascOrderBy.setReturnType(topLevelClass.getType());
                            ascOrderBy.addBodyLine("setOrderByClause(column+\" asc\");");
                            ascOrderBy.addBodyLine("return this;");
                            topLevelClass.getMethods().add(ascOrderBy);
                        }

                        {//ascOrderBy
                            Method descOrderBy = new Method("descOrderBy");
                            descOrderBy.setVisibility(JavaVisibility.PUBLIC);
                            descOrderBy.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "column"));
                            descOrderBy.setReturnType(topLevelClass.getType());
                            descOrderBy.addBodyLine("setOrderByClause(column+\" desc\");");
                            descOrderBy.addBodyLine("return this;");
                            topLevelClass.getMethods().add(descOrderBy);
                        }
                    }
                }
            }
            return result;
        }
    }

    /**
     * 保留类的注释
     *
     * @param topLevelClass
     */
    private static void remainCommits(GeneratedJavaFile file, TopLevelClass topLevelClass) {
        String className = topLevelClass.getType().getFullyQualifiedName();
        try {
            Class<?> clz = Class.forName(className);
            for (Field field : topLevelClass.getFields()) {
                try {
                    java.lang.reflect.Field fieldInfo = clz.getField(field.getName());
                    for (Annotation annotation : fieldInfo.getAnnotations()) {
                        topLevelClass.addImportedType(new FullyQualifiedJavaType(annotation.getClass().getCanonicalName()));
                        System.out.println(annotation.toString());
                        field.addAnnotation(String.format("@%s", annotation.getClass().getName()));
                    }
                } catch (NoSuchFieldException e) {
                    continue;
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println(className + " Not found!");
        }
    }


    private static InnerClass getInnerClassByName(TopLevelClass clz, String name) {
        for (InnerClass innerClass : clz.getInnerClasses()) {
            if (innerClass.getType().getShortName().equals(name)) {
                return innerClass;
            }
        }
        return null;
    }

    private static Method getMethodByName(TopLevelClass clz, String name) {
        for (Method method : clz.getMethods()) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }

    private static class MyCallBack extends DefaultShellCallback {

        /**
         * Instantiates a new default shell callback.
         *
         * @param overwrite the overwrite
         */
        public MyCallBack(boolean overwrite) {
            super(overwrite);
        }

        @Override
        public boolean isMergeSupported() {
            return true;
        }

        @Override
        public String mergeJavaFile(String newFileSource,
                                    File existingFile, String[] javadocTags, String fileEncoding)
                throws ShellException {
            if (existingFile.getName().endsWith("Criteria.java")) {
                return newFileSource;
            }
            try {
                return FileUtils.readFileToString(existingFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newFileSource;
        }
    }
}
