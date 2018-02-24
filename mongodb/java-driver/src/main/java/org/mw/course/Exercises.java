package org.mw.course;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

@SuppressWarnings("unused")
public class Exercises {

    public static void main(String[] args) throws UnknownHostException {
        finalExamQ7();
    }

    // remove the grade of type "homework" with the lowest score for each student from the dataset
    private static void week2ex3() {
        final MongoClient client = new MongoClient();
        final MongoDatabase db = client.getDatabase("students");
        final MongoCollection collection = db.getCollection("grades");

        final Bson criteria = eq("type", "homework");

        final MongoCursor cursor = collection.find(criteria)
                .sort(Sorts.ascending("student_id", "score"))
                .iterator();

        Integer lastId = null;

        while (cursor.hasNext()) {
            final Document document = (Document) cursor.next();
            final Integer id = document.getInteger("student_id");

            if (!id.equals(lastId)) {
                lastId = id;
                collection.deleteOne(document);
            }
        }

        client.close();
    }

    // remove the lowest homework score for each student
    private static void week3ex1() throws UnknownHostException {
        final MongoClient client = new MongoClient();
        final MongoDatabase db = client.getDatabase("school");
        final MongoCollection<Document> collection = db.getCollection("students");

        for (Document document : collection.find()) {
            final Integer id = document.getInteger("_id");
            final List<Document> scores = (List<Document>) document.get("scores", List.class);
            final Optional<Document> docWithMinScore = getDocWithMinHomeworkScore(scores);

            docWithMinScore.ifPresent(document1 ->
                    collection.updateOne(eq("_id", id), new Document("$pull", new Document("scores", document1))));
        }

        client.close();
    }

    private static Optional<Document> getDocWithMinHomeworkScore(List<Document> scores) {
        return scores.stream()
                .filter(score -> "homework".equals(score.getString("type")))
                .min(Comparator.comparingDouble(o -> o.getDouble("score")));
    }

    private static void finalExamQ7() throws UnknownHostException {
        final MongoClient client = new MongoClient();
        final MongoDatabase db = client.getDatabase("exam");
        final MongoCollection<Document> images = db.getCollection("images");
        final MongoCollection<Document> albums = db.getCollection("albums");

        for (Document image : images.find()) {
            final Integer id = image.getInteger("_id");

            if (albums.count(eq("images", id)) == 0) {
                images.deleteOne(image);
            }
        }

        client.close();
    }
}