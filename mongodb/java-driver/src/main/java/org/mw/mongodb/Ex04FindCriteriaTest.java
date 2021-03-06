package org.mw.mongodb;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import static com.mongodb.client.model.Filters.*;

public class Ex04FindCriteriaTest {

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws UnknownHostException {
        final MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(100)
                .build();

        final MongoClient client = new MongoClient("localhost", options);

        final MongoDatabase db = client.getDatabase("course")
                .withReadPreference(ReadPreference.primary());

        final MongoCollection<Document> collection = db.getCollection("findCriteriaTest");

        collection.drop();

        for (int i = 1; i <= 100; i++) {
            collection.insertOne(new Document()
                    .append("x", RANDOM.nextInt(2))
                    .append("y", RANDOM.nextInt(100)));
        }

        System.out.println(collection.count());

        // 1. Basic query by example:
        final Document criteria1 = new Document("x", 0);
        final ArrayList<Document> documents = collection.find(criteria1).into(Lists.newArrayList());
        System.out.println(documents.size());

        // 2. Compound query (x == 0 && y > 25 && y < 75):
        final Document criteria2 = new Document("x", 0).append("y", new Document("$gt", 25).append("$lt", 75));
        System.out.println(collection.count(criteria2));

        // 3. Compound query using Filters:
        final Bson criteria3 = and(eq("x", 0), gt("y", 25), lt("y", 75));
        System.out.println(collection.count(criteria3));

        client.close();
    }
}