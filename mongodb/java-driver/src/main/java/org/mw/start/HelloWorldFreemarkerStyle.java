package org.mw.start;

import com.google.common.collect.Maps;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class HelloWorldFreemarkerStyle {

    public static void main(String[] args) throws IOException, TemplateException {
        final Configuration configuration = new Configuration();
        configuration.setClassForTemplateLoading(HelloWorldFreemarkerStyle.class, "/");

        final Template template = configuration.getTemplate("hello.ftl");
        final StringWriter writer = new StringWriter();
        final Map<String, Object> map = Maps.newHashMap();

        map.put("name", "Maciek");
        template.process(map, writer);

        System.out.println(writer.toString());
    }
}