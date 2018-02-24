package org.mw.start;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.bson.Document;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.io.IOException;
import java.io.StringWriter;
import java.net.UnknownHostException;

public class HelloWorldMongoDbSparkFreemarkerStyle {

    public static void main(String[] args) throws UnknownHostException {
        final Configuration configuration = new Configuration();
        configuration.setClassForTemplateLoading(HelloWorldFreemarkerStyle.class, "/");

        final MongoClient client = new MongoClient("localhost", 27017);
        final MongoDatabase database = client.getDatabase("course");
        final MongoCollection<Document> collection = database.getCollection("hello");

        collection.insertOne(new Document("name", "Madzia"));

        Spark.get(new Route("/") {
            @Override
            public Object handle(Request request, Response response) {
                final StringWriter writer = new StringWriter();

                try {
                    final Template template = configuration.getTemplate("hello.ftl");
                    final Document document = collection.find().first();

                    // acceptable because Document is a Map
                    template.process(document, writer);

                } catch (IOException | TemplateException e) {
                    halt(500);
                }

                return writer;
            }
        });
    }
}