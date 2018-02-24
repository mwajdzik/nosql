package org.mw.mongodb;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class GridFsTest {

    public static void main(String[] args) throws IOException {
        final MongoClient client = new MongoClient();
        final DB db = client.getDB("course");

        final InputStream inputStream = new FileInputStream("c:/Users/sg0218817/Downloads/video/Imagine.mkv");
        final GridFS gridFs = new GridFS(db, "videos");
        final GridFSInputFile gridFsFile = gridFs.createFile(inputStream, "Imagine.mkv");
        final BasicDBObject meta = new BasicDBObject("description", "Movie")
                .append("tags", Lists.newArrayList("Drama"));

        gridFsFile.setMetaData(meta);
        gridFsFile.save();

        // ---

        final GridFSDBFile file = gridFs.findOne(new BasicDBObject("filename", "Imagine.mkv"));
        file.writeTo("c:/Users/sg0218817/Downloads/video/ImagineCopy.mkv");

        client.close();
    }
}