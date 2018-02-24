package org.mw.start;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.net.UnknownHostException;

public class HelloWorldMongoDbStyle {

    public static void main(String[] args) throws UnknownHostException {
        final MongoClient client = new MongoClient("localhost", 27017);
        final MongoDatabase database = client.getDatabase("course");
        final MongoCollection<Document> collection = database.getCollection("hello");
        final Document document = collection.find().first();

        System.out.println(document);
    }
}