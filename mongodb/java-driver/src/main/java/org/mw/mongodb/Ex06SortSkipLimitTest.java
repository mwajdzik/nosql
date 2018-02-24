package org.mw.mongodb;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.*;

public class Ex06SortSkipLimitTest {

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws UnknownHostException {
        final MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(100)
                .build();

        final MongoClient client = new MongoClient("localhost", options);

        final MongoDatabase db = client.getDatabase("course")
                .withReadPreference(ReadPreference.primary());

        final MongoCollection<Document> collection = db.getCollection("sortSkipLimitTest");

        collection.drop();

        for (int i = 1; i <= 100; i++) {
            collection.insertOne(new Document("_id", i)
                            .append("start", new Document("x", RANDOM.nextInt(100)).append("y", RANDOM.nextInt(100)))
                            .append("end", new Document("x", RANDOM.nextInt(100)).append("y", RANDOM.nextInt(100)))
            );
        }

        final Bson projection = fields(include("start.x", "start.y"), excludeId());
        final Bson sorting = orderBy(descending("start.x"), ascending("start.y"));

        final ArrayList<Document> documents = collection.find().projection(projection)
                .sort(sorting)
                .skip(10)
                .limit(2)
                .into(Lists.newArrayList());

        System.out.println(documents);

        client.close();
    }
}