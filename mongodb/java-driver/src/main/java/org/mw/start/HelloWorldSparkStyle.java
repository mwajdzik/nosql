package org.mw.start;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.net.UnknownHostException;

public class HelloWorldSparkStyle {

    public static void main(String[] args) throws UnknownHostException {
        Spark.get(new Route("/") {
            @Override
            public Object handle(Request request, Response response) {
                return "Hello World From Spark";
            }
        });
    }
}