package org.mw.mongodb;

import com.google.common.collect.Lists;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

public class Ex03FindTest {

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws UnknownHostException {
        final MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(100)
                .build();

        final MongoClient client = new MongoClient("localhost", options);

        final MongoDatabase db = client.getDatabase("course")
                .withReadPreference(ReadPreference.primary());

        final MongoCollection<Document> collection = db.getCollection("findTest");

        collection.drop();

        for (int i = 1; i <= 100; i++) {
            collection.insertOne(new Document("x", RANDOM.nextInt(100)));
        }

        final Document document = collection.find().first();
        System.out.println(document);

        final ArrayList<Document> documents = collection.find().into(Lists.newArrayList());
        System.out.println(documents);

        try (final MongoCursor<Document> iterator = collection.find().iterator()) {
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
        }

        final long count = collection.count();
        System.out.println(count);

        client.close();
    }
}