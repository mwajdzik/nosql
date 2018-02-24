package org.mw.mongodb;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.io.ByteStreams;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ImportTest {
    public static void main(String[] args) throws IOException {
        final MongoClient client = new MongoClient();
        final DB db = client.getDB("course");
        final DBCollection collection = db.getCollection("importTest");
        collection.drop();

        final URL url = new URL("https://education.mongodb.com/static/m101j-october-2013/handouts/students.432aefc2cf4e.json");
        final byte[] bytes = ByteStreams.toByteArray(url.openStream());
        final String temp = new String(bytes);
        final Iterable<String> parts = Splitter.on("\n").omitEmptyStrings().split(temp);
        final String content = "[" + Joiner.on(',').join(parts) + ']';

        @SuppressWarnings("unchecked")
        final List<BasicDBObject> list = (List<BasicDBObject>) JSON.parse(content);

        for (BasicDBObject student : list) {
            collection.insert(student);
        }

        System.out.println(collection.count());

        client.close();
    }
}
