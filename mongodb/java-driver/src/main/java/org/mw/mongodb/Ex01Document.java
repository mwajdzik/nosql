package org.mw.mongodb;

import org.bson.Document;
import org.bson.json.JsonWriter;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.GregorianCalendar;

public class Ex01Document {

    public static void main(String[] args) throws UnknownHostException {
        final Document document = new Document()
                .append("name", "John")
                .append("birth_date", new GregorianCalendar(1980, 9, 29).getTime())
                .append("age", 23)
                .append("languages", Arrays.asList("English", "German"))
                .append("address", new Document("street", "20 Main")
                                .append("town", "Westfield")
                                .append("zip", "90210"));

        System.out.println(document);
    }
}