package org.mw.mongodb;

import com.google.common.collect.Lists;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

public class Ex05FieldSelectionTest {

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws UnknownHostException {
        final MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(100)
                .build();

        final MongoClient client = new MongoClient("localhost", options);

        final MongoDatabase db = client.getDatabase("course")
                .withReadPreference(ReadPreference.primary());

        final MongoCollection<Document> collection = db.getCollection("fieldSelectionTest");

        collection.drop();

        for (int i = 1; i <= 100; i++) {
            collection.insertOne(new Document()
                    .append("x", RANDOM.nextInt(2))
                    .append("y", RANDOM.nextInt(100))
                    .append("z", RANDOM.nextInt(1000)));
        }

        System.out.println(collection.count());

        final Bson criteria = and(eq("x", 0), gt("y", 40), lt("y", 60));
        final Bson keys = fields(include("y", "z"), excludeId());

        final ArrayList<Document> documents = collection.find(criteria)
                .projection(keys)
                .into(Lists.newArrayList());

        System.out.println(documents);
        System.out.println(documents.size());

        client.close();
    }
}