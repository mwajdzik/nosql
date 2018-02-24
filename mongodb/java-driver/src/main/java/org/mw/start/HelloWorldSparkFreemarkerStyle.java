package org.mw.start;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.io.IOException;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class HelloWorldSparkFreemarkerStyle {

    public static void main(String[] args) throws UnknownHostException {
        final Configuration configuration = new Configuration();
        configuration.setClassForTemplateLoading(HelloWorldFreemarkerStyle.class, "/");

        Spark.get(new Route("/") {
            @Override
            public Object handle(Request request, Response response) {
                final StringWriter writer = new StringWriter();

                try {
                    final Template template = configuration.getTemplate("hello.ftl");
                    final Map<String, Object> map = new HashMap<>();

                    map.put("name", "Maciek");
                    template.process(map, writer);

                } catch (IOException | TemplateException e) {
                    halt(500);
                }

                return writer;
            }
        });
    }
}