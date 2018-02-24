package org.mw.mongodb;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.net.UnknownHostException;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;

public class Ex08UpdateRemoveTest {

    public static void main(String[] args) throws UnknownHostException {
        final MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(100)
                .build();

        final MongoClient client = new MongoClient("localhost", options);

        final MongoDatabase db = client.getDatabase("course")
                .withReadPreference(ReadPreference.primary());

        final MongoCollection<Document> collection = db.getCollection("updateRemoveTest");
        collection.drop();

        Lists.newArrayList("alice", "bobby", "cathy", "david", "ethan").forEach(name -> {
            collection.insertOne(new Document("_id", name));
        });

        // replacement the whole document
        collection.replaceOne(eq("_id", "alice"),
                new Document("_id", "alice").append("age", 23));

        // update
        collection.updateOne(eq("_id", "bobby"),
                new Document("$set", new Document("age", 34)));

        collection.updateMany(gte("_id", "david"),
                new Document("$set", new Document("age", 50)));

        // upsert
        collection.updateOne(eq("_id", "maggie"),
                new Document("$set", new Document("age", 34)),
                new UpdateOptions().upsert(true));

        System.out.println(collection.find().into(Lists.newArrayList()));

        // remove everything
        collection.deleteMany(new Document());

        client.close();
    }
}