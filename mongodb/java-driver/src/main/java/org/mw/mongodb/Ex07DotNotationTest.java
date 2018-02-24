package org.mw.mongodb;

import com.google.common.collect.Lists;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

public class Ex07DotNotationTest {

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws UnknownHostException {
        final MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(100)
                .build();

        final MongoClient client = new MongoClient("localhost", options);

        final MongoDatabase db = client.getDatabase("course")
                .withReadPreference(ReadPreference.primary());

        final MongoCollection<Document> collection = db.getCollection("dotNotationTest");
        collection.drop();

        for (int i = 1; i <= 100; i++) {
            collection.insertOne(new Document("_id", i)
                            .append("start", new Document("x", RANDOM.nextInt(100)).append("y", RANDOM.nextInt(100)))
                            .append("end", new Document("x", RANDOM.nextInt(100)).append("y", RANDOM.nextInt(100)))
            );
        }

        final Bson criteria = and(gt("start.x", 25), lt("start.x", 75));
        final Bson keys = fields(include("start.y"), excludeId());

        final ArrayList<Document> documents = collection.find(criteria)
                .projection(keys)
                .into(Lists.newArrayList());

        System.out.println(documents);

        // the format of a single, returned document is: { "start" : { "y" : 80}}
        client.close();
    }
}