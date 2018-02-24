package org.mw.replica_set;

import com.google.common.collect.Lists;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.net.UnknownHostException;

public class ReplicaSetTest {

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        // not all nodes must be here - the driver will establish the complete list of nodes
        // a background thread constantly pings nodes from the seeds list and updates the info about the primary node
        final MongoClient client = new MongoClient(
                Lists.newArrayList(
                        new ServerAddress("localhost", 27017),
                        new ServerAddress("localhost", 27018),
                        new ServerAddress("localhost", 27019)
                ),
                MongoClientOptions.builder()
                        .requiredReplicaSetName("replset")
                        .build()
        );

        final MongoDatabase db = client.getDatabase("course");
        final MongoCollection<Document> collection = db.getCollection("replicaSetTest");
        collection.drop();

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            for (int retry = 0; retry < 3; retry++) {
                try {
                    collection.insertOne(new Document("_id", i));
                    System.out.println(i + " inserted");
                    break;
                } catch (MongoException ex) {
                    // can be simulated with rs.stepDown()
                    System.out.println(ex.getCode());
                    if (ex.getCode() == 11000) {
                        System.err.println("Document already inserted: " + i);
                    } else {
                        System.err.println(ex.getMessage());
                        System.err.println("Retrying...");
                        Thread.sleep(5000);
                    }
                }
            }

            Thread.sleep(250);
        }

        // ---

        client.setWriteConcern(WriteConcern.JOURNALED);
        db.withWriteConcern(WriteConcern.MAJORITY);
        collection.withWriteConcern(WriteConcern.UNACKNOWLEDGED);
        collection.withWriteConcern(WriteConcern.FSYNC_SAFE).insertOne(new Document("_id", 1000));

        // ---

        client.setReadPreference(ReadPreference.primary());
        db.withReadPreference(ReadPreference.primaryPreferred());
        collection.withReadPreference(ReadPreference.secondaryPreferred());
        collection.withReadPreference(ReadPreference.nearest()).find(new Document("_id", 1000));
    }
}