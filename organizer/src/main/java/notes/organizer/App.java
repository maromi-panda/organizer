package notes.organizer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

/**
 * Hello world!
 *
 */
public class App {
  public static void main(String[] args) throws Exception {


    // Added a comment
    // connect to the local database server
    MongoClient mongoClient = new MongoClient();

    // get handle to "mydb"
    DB db = mongoClient.getDB("mydb");

    // get a list of the collections in this database and print them out
    Set<String> collectionNames = db.getCollectionNames();
    System.out.println("Printing collection names...");
    for (final String s : collectionNames) {
      System.out.println(s);
    }

    // get a collection object to work with
    DBCollection coll = db.getCollection("testCollection");

    System.out.println("Printing collection names again...");
    for (final String s : collectionNames) {
      System.out.println(s);
    }

    // drop all the data in it
    coll.drop();

    // now, lets add lots of little documents to the collection so we can explore queries and cursors
    char value = 'a';
    for (int i = 1; i <= 10; i++) {
      coll.insert(new BasicDBObject().append("name", i).append("value", value));
      value += 1;
    }
    System.out.println("total # of documents after inserting 10 small ones (should be 10): " + coll.getCount());

    // lets get all the documents in the collection and print them out
    DBCursor cursor = coll.find();
    try {
      while (cursor.hasNext()) {
        System.out.println(cursor.next());
      }
    } finally {
      cursor.close();
    }

    // now use a query to get 1 document out
    BasicDBObject basicDBObject = new BasicDBObject("name", 7);
    cursor = coll.find(basicDBObject);

    try {
      while (cursor.hasNext()) {
        System.out.println("Finding 7 : "+cursor.next());
      }
    } finally {
      cursor.close();
    }

    // $ Operators are represented as strings
    basicDBObject = new BasicDBObject("name", new BasicDBObject("$gt", 7)
    .append("$lte", 10));
    cursor = coll.find(basicDBObject);

    try {
      System.out.println("Printing documents greater than 7 and less than 10");
      while(cursor.hasNext()) {
        System.out.println(cursor.next());
      }
    } finally {
      cursor.close();
    }

    List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
    obj.add(new BasicDBObject("name", new BasicDBObject("$gt", 4)));
    obj.add(new BasicDBObject("name", new BasicDBObject("$ne", 7)));
    basicDBObject = new BasicDBObject();
    basicDBObject.put("$and", obj);
    cursor = coll.find(basicDBObject);

    try {
      System.out.println("Printing documents greater than 4 and not equal to 7");
      while(cursor.hasNext()) {
        System.out.println(cursor.next());
      }
    } finally {
      cursor.close();
    }

    // Fetch an object and update it
    basicDBObject = new BasicDBObject("name", new BasicDBObject("$eq", 6));
    cursor = coll.find(basicDBObject);
    while(cursor.hasNext()) {
      BasicDBObject updateBasicDBObject = new BasicDBObject().append("name", 100).append("value", "z");
      coll.update(cursor.next(), updateBasicDBObject);
    }

    System.out.println("Printing documents after updation...");
    cursor = coll.find();
    try {
      while (cursor.hasNext()) {
        System.out.println(cursor.next());
      }
    } finally {
      cursor.close();
    }

    // Something serious now

    //Create GridFS object
    GridFS fs = new GridFS( db );

    File file = new File("D:/Personal/Organizer/Untitled.png");

    //Save image into database
    GridFSInputFile in = fs.createFile( file );
    in.setFilename("my photo");
    in.save();

    //Find saved image
    GridFSDBFile out = fs.findOne("my photo");
    System.out.println("Image from db : " +out);

    //Save loaded image from database into new image file
    FileOutputStream outputImage = new FileOutputStream("D:/Personal/Organizer/Maromi.png");
    out.writeTo( outputImage );

    outputImage.close();
  }
}
