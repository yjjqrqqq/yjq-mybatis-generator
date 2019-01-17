package com.github.yjjqrqqq.mybatis_generator.plugins;

import com.github.yjjqrqqq.mybatis_generator.plugins.utils.PluginTools;
import com.github.yjjqrqqq.mybatis_generator.plugins.utils.hook.HookAggregator;
import com.github.yjjqrqqq.mybatis_generator.plugins.utils.BeanUtils;
import com.github.yjjqrqqq.mybatis_generator.plugins.utils.PluginTools;
import com.github.yjjqrqqq.mybatis_generator.plugins.utils.enhanced.TemplateCommentGenerator;
import com.github.yjjqrqqq.mybatis_generator.plugins.utils.hook.ITableConfigurationHook;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.List;

/**
 * @author liuyixin
 * @date 2018/11/2919:03
 */
public class BasePlugin extends PluginAdapter {
    protected CommentGenerator commentGenerator;
    protected List<String> warnings;

    public BasePlugin() {
    }

    public void setContext(Context context) {
        super.setContext(context);
        HookAggregator.getInstance().setContext(context);
        PluginConfiguration cfg = PluginTools.getPluginConfiguration(context, CommentPlugin.class);
        if (cfg != null && cfg.getProperty("template") != null) {
            TemplateCommentGenerator templateCommentGenerator = new TemplateCommentGenerator(cfg.getProperty("template"), false);
            this.commentGenerator = templateCommentGenerator;

            try {
                context.getCommentGenerator();
                BeanUtils.setProperty(context, "commentGenerator", templateCommentGenerator);
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        } else if (context.getCommentGenerator() instanceof DefaultCommentGenerator) {
            this.commentGenerator = new TemplateCommentGenerator("default-comment.ftl", true);
        } else {
            this.commentGenerator = context.getCommentGenerator();
        }

    }

    public boolean validate(List<String> warnings) {
        this.warnings = warnings;
       /* if (StringUtility.stringHasValue(this.getContext().getTargetRuntime()) && !"MyBatis3".equalsIgnoreCase(this.getContext().getTargetRuntime())) {
            warnings.add("itfsw:插件" + this.getClass().getTypeName() + "要求运行targetRuntime必须为MyBatis3！");
            return false;
        } else {
            return true;
        }*/
       return true;
    }

    public void initialized(IntrospectedTable introspectedTable) {
        super.initialized(introspectedTable);
        if (StringUtility.stringHasValue(introspectedTable.getTableConfiguration().getAlias())) {
            this.warnings.add("itfsw:插件" + this.getClass().getTypeName() + "请不要配置alias属性，这个属性官方支持也很混乱，导致插件支持会存在问题！");
        }

        ((ITableConfigurationHook) PluginTools.getHook(ITableConfigurationHook.class)).tableConfiguration(introspectedTable);
    }
}
