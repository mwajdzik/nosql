package org.mw.mongodb;

import com.google.common.collect.Lists;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.net.UnknownHostException;

public class Ex02InsertTest {

    public static void main(String[] args) throws UnknownHostException {
        final MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(100)
                .build();

        final MongoClient client = new MongoClient("localhost", options);

        final MongoDatabase db = client.getDatabase("course")
                .withReadPreference(ReadPreference.primary());

        final MongoCollection<Document> collection = db.getCollection("insertTest");
        collection.drop();

        final Document document = new Document().append("x", 1);
        System.out.println(document);   // no _id
        collection.insertOne(document);
        System.out.println(document);   // _id auto generated

        final Document doc1 = new Document().append("x", 2);
        final Document doc2 = new Document().append("x", 3);
        collection.insertMany(Lists.newArrayList(doc1, doc2));

        try {
            collection.insertOne(document);
        } catch (MongoException ex) {
            System.err.println(ex.getMessage());
        }

        document.remove("_id");
        collection.insertOne(document);
        System.out.println(document);

        client.close();
    }
}