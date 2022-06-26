package org.example.roleLogging;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.NoSuchElementException;

public class Database extends ListenerAdapter {
    public static MongoCollection<Document> collection;
    @Override
    public void onReady(ReadyEvent e){

           String uri = "mongodb+srv://Emitter:yourmom69@cluster0.bvjr8.mongodb.net/?retryWrites=true&w=majority";
           MongoClientURI clientURI = new MongoClientURI(uri);
           MongoClient client = new MongoClient(clientURI);
           MongoDatabase database = client.getDatabase("roleLogging");
           collection = database.getCollection("roleLogging");

    }



    public static void set(String Id, String Key, Object value, boolean isAdd) throws InterruptedException {
        updateDB(Id, Key, value, isAdd);
    }
    public static Document get(String Id){
        return collection.find(new Document("loggingServerConfig", Id)).cursor().next();
    }

    private static void createDB(String Id){
        //server config
        Document document = new Document("loggingServerConfig", Id)
                .append("sensitiveRoles", "")
                .append("roleToPing", "")
                .append("loggingChannel", "")
                .append("ignoreBot", true);

        collection.insertOne(document);

    }


    private static void updateDB(String Id, String key, Object value, boolean isAdd) throws InterruptedException {
        //for server
        Document document = null;
        try{
            document = collection.find(new Document("loggingServerConfig", Id)).cursor().next();
        }catch (NoSuchElementException exception){
            createDB(Id);
            Thread.sleep(200);
            document = collection.find(new Document("loggingServerConfig", Id)).cursor().next();
        }

        if(!isAdd){
            Document Updatedocument = new Document(key, value);
            Bson updateKey = new Document("$set", Updatedocument);
            collection.updateOne(document, updateKey);
        }else{
            Document Updatedocument = new Document(key, (document.get(key) + (String)value));
            Bson updateKey = new Document("$set", Updatedocument);
            collection.updateOne(document, updateKey);
        }

    }

}
