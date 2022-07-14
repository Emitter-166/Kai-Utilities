package org.example.twenty_eighty;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.NoSuchElementException;

public class Database extends ListenerAdapter {
    public static MongoCollection<Document> collection;

    public static void set(String Id, String Key, Object value, boolean isAdd) throws InterruptedException {
        //for ease of understanding while coding, this method is added, there is no real use of it
        updateDB(Id, Key, value, isAdd);
    }

    public static Document get(String Id, String field) {
        //it will return server settings from database collection
       return collection.find(new Document(field, Id)).cursor().next();
    }

    private static void createDB(String Id) {
        //server config, here is the template used to make new settings document on db collection
        Document document = new Document("serverId", Id)
                .append("users", "")
                .append("totalUsers", 0)
                .append("messages", 0)
                .append("roleToMention", "")
                .append("actionChannel", "");

        collection.insertOne(document);

    }

    private static void updateDB(String Id, String key, Object value, boolean isAdd) throws InterruptedException {
        //for server
        Document document = null;
        try {
            //it will try to assign value to document, if there is no server settings for role logging, it will create one
            document = collection.find(new Document("serverId", Id)).cursor().next();
        } catch (NoSuchElementException exception) {
            createDB(Id);
            Thread.sleep(200);
            document = collection.find(new Document("serverId", Id)).cursor().next();
        }

        if (!isAdd) {
            //it will check if the values should be added with the previous value
            Document Updatedocument = new Document(key, value);
            Bson updateKey = new Document("$set", Updatedocument);
            collection.updateOne(document, updateKey);
        } else {
            int Integer = 0;
            Document Updatedocument;
            if(value.getClass().isInstance(Integer)){
                Updatedocument = new Document(key, ((int)document.get(key) + (int) value));
            }else{
                Updatedocument = new Document(key, (document.get(key) + (String) value));
            }

            Bson updateKey = new Document("$set", Updatedocument);
            collection.updateOne(document, updateKey);
        }

    }

    @Override
    public void onReady(ReadyEvent e) {
        //it will update collection everytime bot starts up
        String uri = System.getenv("uri"); //Mongo DB uri
        MongoClientURI clientURI = new MongoClientURI(uri);
        MongoClient client = new MongoClient(clientURI);
        MongoDatabase database = client.getDatabase("2080"); //getting database and collection
        collection = database.getCollection("2080");

    }

}

