package org.mw.start;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

// uses embedded Jetty server
public class SparkRoutes {

    public static void main(String[] args) {

        // GET

        // localhost:4567/
        get(new Route("/") {
            @Override
            public Object handle(Request request, Response response) {
                return "Hello world";
            }
        });

        // localhost:4567/test
        get(new Route("/test") {
            @Override
            public Object handle(Request request, Response response) {
                return "Test page";
            }
        });

        // localhost:4567/echo/Maciek
        get(new Route("/echo/:thing") {
            @Override
            public Object handle(Request request, Response response) {
                return request.params(":thing");
            }
        });

        // localhost:4567/fruit
        get(new Route("/fruit") {
            @Override
            public Object handle(Request request, Response response) {
                final Configuration configuration = new Configuration();
                configuration.setClassForTemplateLoading(getClass(), "/");

                try {
                    final Template template = configuration.getTemplate("fruitPicker.ftl");
                    final StringWriter writer = new StringWriter();
                    final Map<String, Object> map = Maps.newHashMap();

                    map.put("fruits", Lists.newArrayList("apple", "orange", "banana", "peach"));
                    template.process(map, writer);

                    return writer;

                } catch (IOException | TemplateException e) {
                    halt(500);
                    return null;
                }
            }
        });

        // POST

        post(new Route("/favorite_fruit") {
            @Override
            public Object handle(Request request, Response response) {
                final String fruit = request.queryParams("fruit");

                if (fruit == null) {
                    return "Why don't you pick one?";
                } else {
                    return "Your favorite fruit is " + fruit;
                }
            }
        });
    }
}