package org.mw.mongodb;

import com.google.common.base.Objects;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;

public class FindAndModifyTest {

    public static void main(String[] args) throws UnknownHostException {
        final MongoClient client = new MongoClient();
        final DB db = client.getDB("course");
        final DBCollection collection = db.getCollection("findAndModifyTest");
        collection.drop();

        final String counterId = "myCounter";

        System.out.println(getRange(counterId, 2, collection));
        System.out.println(getRange(counterId, 3, collection));
        System.out.println(getRange(counterId, 10, collection));

        client.close();
    }

    // findAndModify - allows in one atomic operation to find a document, update it, and return it
    private static Range getRange(String id, int range, DBCollection collection) {
        final BasicDBObject query = new BasicDBObject("_id", id);
        final DBObject fieldsToBeReturned = null;
        final DBObject sort = null;
        final boolean removeDocumentAfterModified = false;

        // final BasicDBObject updateToDocument = new BasicDBObject("$inc",
        //        new BasicDBObject("comments." + ordinal + ".num_likes", 1));

        final BasicDBObject updateToDocument = new BasicDBObject("$inc", new BasicDBObject("counter", range));
        final boolean returnValueAfterUpdate = true;
        final boolean createCounterIfNotPresent = true;

        final DBObject document = collection.findAndModify(query, fieldsToBeReturned, sort,
                removeDocumentAfterModified, updateToDocument, returnValueAfterUpdate, createCounterIfNotPresent);

        final int to = (Integer) document.get("counter");
        final int from = to - range + 1;

        return new Range(from, to);
    }

    private static class Range {

        private int from;
        private int to;

        private Range(int from, int to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(Range.class)
                    .add("From", from)
                    .add("To", to)
                    .toString();
        }
    }
}