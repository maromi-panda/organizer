package notes.organizer;

import java.net.UnknownHostException;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) throws UnknownHostException {
		System.out.println("Hello World!");// connect to the local database server
        MongoClient mongoClient = new MongoClient();

        /*
        // Authenticate - optional
        MongoCredential credential = MongoCredential.createMongoCRCredential(userName, database, password);
        MongoClient mongoClient = new MongoClient(new ServerAddress(), Arrays.asList(credential));
        */

        // get handle to "mydb"
        DB db = mongoClient.getDB("mydb");

        // get a list of the collections in this database and print them out
        Set<String> collectionNames = db.getCollectionNames();
        for (final String s : collectionNames) {
            System.out.println(s);
        }

        // get a collection object to work with
        DBCollection coll = db.getCollection("testCollection");

        // drop all the data in it
        coll.drop();

        // make a document and insert it
        BasicDBObject doc = new BasicDBObject("name", "MongoDB")
                .append("type", "database")
                .append("count", 1)
                .append("info", new BasicDBObject("x", 203).append("y", 102));

        coll.insert(doc);

        // get it (since it's the only one in there since we dropped the rest earlier on)
        DBObject myDoc = coll.findOne();
        System.out.println(myDoc);

        // now, lets add lots of little documents to the collection so we can explore queries and cursors
        for (int i = 0; i < 10; i++) {
            coll.insert(new BasicDBObject().append("i", i));
        }
        System.out.println("total # of documents after inserting 100 small ones (should be 101) " + coll.getCount());

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
        BasicDBObject query = new BasicDBObject("i", 7);
        cursor = coll.find(query);

        try {
            while (cursor.hasNext()) {
                System.out.println("Finding 7 : "+cursor.next());
            }
        } finally {
            cursor.close();
        }

        // $ Operators are represented as strings
        query = new BasicDBObject("j", new BasicDBObject("$ne", 3))
                .append("k", new BasicDBObject("$gt", 10));

        cursor = coll.find(query);

        try {
            while(cursor.hasNext()) {
                System.out.println(cursor.next());
            }
        } finally {
            cursor.close();
        }

        // now use a range query to get a larger subset
        // find all where i > 50
        query = new BasicDBObject("i", new BasicDBObject("$gt", 50));
        cursor = coll.find(query);

        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next());
            }
        } finally {
            cursor.close();
        }
	}
}
