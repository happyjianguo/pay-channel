package com.dream.pay.channel.service.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

/**
 * freemark工具类
 *
 * @author mengzhenbin
 * @version 1.0
 * @freemark读取报文模版
 */
public class FreeMarkerUtil {
    private static final Logger logger = LoggerFactory.getLogger(FreeMarkerUtil.class);
    private Configuration config = null;
    private String charset = "UTF-8";

    private FreeMarkerUtil() {
        config = new Configuration();
        config.setClassForTemplateLoading(this.getClass(), "/template/");// 类路径
    }

    private static class SingletonHolder {
        private static FreeMarkerUtil signletion = new FreeMarkerUtil();

        static {
            try {
                freemarker.log.Logger.selectLoggerLibrary(freemarker.log.Logger.LIBRARY_SLF4J);
            } catch (ClassNotFoundException e) {
            }
        }
    }

    public static FreeMarkerUtil getInstance() {
        return SingletonHolder.signletion;
    }

    /**
     * 根据绝对路径读取模版
     * <p>
     * Description:
     * </p>
     *
     * @param path
     * @Title: load
     * @author suxx
     */
    public void loadAbsPath(String path) {
        try {
            config = new Configuration();
            config.setDirectoryForTemplateLoading(new File(path));
        } catch (IOException e) {
            logger.error("########调用freemarker出错", e);
        }
    }

    /**
     * 根据类路径读取模版
     * <p>
     * Description:
     * </p>
     *
     * @param path
     * @Title: loadClassPath
     * @author suxx
     */
    private void loadClassPath(String path) {
        config = new Configuration();
        config.setClassForTemplateLoading(this.getClass(), path);
    }

    /**
     * 根据模版名称，数据模型，组装模版
     *
     * @param root       数据模型
     * @param templeName 模版名称
     * @return
     */
    public String getStrByTemplate(Map<String, String> root, String templeName) throws Exception {
        return getStrByTemplate(root, templeName, charset);
    }

    public String getStrByTemplate(Map<String, String> root, String templeName, String charset) throws Exception {
        String result = "";
        try {
            config.setEncoding(Locale.getDefault(), charset);
            Template template = config.getTemplate(templeName);
            StringWriter out = new StringWriter();
            template.process(root, out);
            out.flush();
            out.close();
            result = out.toString();
        } catch (Exception e) {
            logger.error("########调用freemarker出错", e);
            throw new Exception();
        }
        return result;
    }

    /**
     * 根据模版名称，数据模型，组装模版
     *
     * @param obj        数据对象
     * @param templeName 模版名称
     * @return
     */
    public String getStrByTemplateObj(Object obj, String templeName) {
        return getStrByTemplateObj(obj, templeName, charset);
    }

    public String getStrByTemplateObj(Object obj, String templeName, String charset) {
        String result = "";
        try {
            config.setEncoding(Locale.getDefault(), charset);
            Template template = config.getTemplate(templeName);
            StringWriter out = new StringWriter();
            template.process(obj, out);
            out.flush();
            out.close();
            result = out.toString();
        } catch (TemplateException e) {
            logger.error("########调用freemarker模版出错", e);
        } catch (IOException e) {
            logger.error("########调用freemarker出错", e);
        }
        return result;
    }

    /**
     * 根据全路径转换模版
     * <p>
     * Description:
     * </p>
     *
     * @param root 数据模型
     * @param ftl  /template/com/test/test.ftl
     * @return
     * @Title: getStrByFullPath
     * @author suxx
     */
    public String getStrByFullPath(Map<String, String> root, String ftl) throws Exception {
        int l = ftl.lastIndexOf("/");
        loadAbsPath(ftl.substring(0, l));
        return getStrByTemplate(root, ftl.substring(l + 1, ftl.length()));
    }

}
