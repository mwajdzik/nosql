package org.mw.mongodb;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Ex09AggregationTest {

    public static void main(String[] args) throws UnknownHostException {
        final MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(100)
                .build();

        final MongoClient client = new MongoClient("localhost", options);

        final MongoDatabase db = client.getDatabase("test")
                .withReadPreference(ReadPreference.primary());

        final MongoCollection<Document> collection = db.getCollection("zips");

        final List<Document> pipeline = Lists.newArrayList(
                new Document("$group", new Document("_id", "$state")
                        .append("totalPopulation", new Document("$sum", "$pop"))),
                new Document("$match", new Document("totalPopulation", new Document("$gte", 500_000)))
        );

        final ArrayList<Document> results = collection.aggregate(pipeline).into(Lists.newArrayList());

        System.out.println(results);

        client.close();
    }
}