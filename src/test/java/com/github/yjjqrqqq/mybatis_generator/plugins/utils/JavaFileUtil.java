package com.github.yjjqrqqq.mybatis_generator.plugins.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author liuyixin
 * @date 2018/12/1417:19
 */
public class JavaFileUtil {
    /**
     * 获取某一个字段的注释和注记。
     *
     * @param fileContent
     * @param fieldName
     * @return
     */
    public static AnnotationsAndDocs getFieldComm(String fileContent, String fieldName) {
        AnnotationsAndDocs result = new AnnotationsAndDocs();
        String[] lines = fileContent.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String fieldPatter = String.format("(/\\*.*\\*/)*([^;\\n]*[ ]+%s[ ]*);(/\\*.*\\*/)*(//.*)*", fieldName);
            Pattern pattern = Pattern.compile(fieldPatter);
            if (pattern.matcher(line.trim()).matches()) {
                getCommit(result, lines, i);
                break;
            }
        }
        return result;
    }

    private static void getCommit(AnnotationsAndDocs result, String[] lines, int row) {
        boolean isInStar = false;
        for (int i = row - 1; i >= 0; i--) {
            String line = lines[i].trim().replaceAll("^(/\\*.*\\*/)", "");
            if (StringUtils.isBlank(line)) {
                continue;
            }

            if (StringUtils.startsWithAny(line, "public", "private", "protected", "class")) {
                break;
            }
            if (line.endsWith("*/")) {
                isInStar = true;
            }
            String first = line.substring(0, 1);
            if (!isInStar && first.matches("[a-zA-Z_]")) {
                break;
            }
            if (isInStar) {
                String toAdd = lines[i].trim();
                if (toAdd.startsWith("*")) {
                    toAdd = " " + toAdd;
                }
                result.docs.add(0, toAdd);
            } else {
                result.annotations.add(line);
            }
            if (line.startsWith("/*")) {
                isInStar = false;
            }
        }
    }

    public static class AnnotationsAndDocs {
        public List<String> annotations = new ArrayList<>();
        public List<String> docs = new ArrayList<>();
    }

    public static void main(String[] args) {
        String content = "\n" +
                "public class LoanBasisInfoBase implements Serializable {\n" +
                "    /**\n" +
                "     *\n" +
                "     * This field was generated by MyBatis Generator.\n" +
                "     * This field corresponds to the database column loan_basis_info.id\n" +
                "     *\n" +
                "     * @mbg.generated\n" +
                "     */\n" +
                "    private Long id;";
        System.out.println(getFieldComm(content, "id"));
    }
}
