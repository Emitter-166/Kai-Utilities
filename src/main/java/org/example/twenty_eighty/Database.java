package org.example.twenty_eighty;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.example.token;

import java.util.NoSuchElementException;

public class Database extends ListenerAdapter {
    public static MongoCollection<Document> collection;

    //sync variables
    public static boolean clearRunning = false;


    public static void set(String Id, String field,  String Key, Object value, boolean isAdd) throws InterruptedException {
        //for ease of understanding while coding, this method is added, there is no real use of it
        updateDB(Id, field, Key, value, isAdd);
    }

    public static Document get(String Id, String field) throws InterruptedException {
        //it will return server settings from database collection
       try{
           return collection.find(new Document(field, Id)).cursor().next();
       }catch (NoSuchElementException exception){
           if(field.equalsIgnoreCase("serverId")){
               createDB(Id);
           }else if(field.equalsIgnoreCase("userId")){
               createUserDB(Id);
           }else if(field.equalsIgnoreCase("channelId")){
               createChannelDB(Id);
           }else if(field.equalsIgnoreCase("eventChannelId")){
               createChannelEventDB(Id);
           }
           Thread.sleep(200);
           return collection.find(new Document(field, Id)).cursor().next();
       }

    }
    public static Document normalGet(String Id, String field){
        return collection.find(new Document(field, Id)).cursor().next();
    }

    private static void createDB(String Id) {
        //server config, here is the template used to make new settings document on db collection
        Document document = new Document("serverId", Id)
                .append("users", "")
                .append("eventMonitoringChannels", "");

        collection.insertOne(document);

    }
    private static void createUserDB(String Id) {
        //user config, here is the template used to make new settings document on db collection
        Document document = new Document("userId", Id)
                .append("total", 0);
        collection.insertOne(document);

    }

    private static void createChannelDB(String Id) {
        //user config, here is the template used to make new settings document on db collection
        Document document = new Document("channelId", Id)
                .append("tempTotal", 0)
                .append("tempUserCount", 0);
        collection.insertOne(document);

    }
    private static void createChannelEventDB(String Id) {
        //user config, here is the template used to make new settings document on db collection
        Document document = new Document("eventChannelId", Id)
                .append("Users", "")
                .append("HostId", "")
                .append("totalMessages", 0)
                .append("timeStarted", 0L);
        collection.insertOne(document);

    }

    private static void updateDB(String Id, String field, String key, Object value, boolean isAdd) throws InterruptedException {
        //for server
        Document document = null;
        try {
            //it will try to assign value to document, if there is no server settings for role logging, it will create one
            document = collection.find(new Document(field, Id)).cursor().next();
        } catch (NoSuchElementException exception) {
            if(field.equalsIgnoreCase("serverId")){
                createDB(Id);
            }else if(field.equalsIgnoreCase("userId")){
                createUserDB(Id);
            }else if(field.equalsIgnoreCase("channelId")){
                createChannelDB(Id);
            }else if(field.equalsIgnoreCase("eventChannelId")){
                createChannelEventDB(Id);
            }
            document = collection.find(new Document(field, Id)).cursor().next();
        }

        if (!isAdd) {
            //it will check if the values should be added with the previous value
            Document Updatedocument = new Document(key, value);
            Bson updateKey = new Document("$set", Updatedocument);
            collection.updateOne(document, updateKey);
        } else {
            Document Updatedocument;
            if(value.getClass().getSimpleName().equalsIgnoreCase("Integer")){
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
        String uri = token.getUri(); //Mongo DB uri
        MongoClientURI clientURI = new MongoClientURI(uri);
        MongoClient client = new MongoClient(clientURI);
        MongoDatabase database = client.getDatabase("20-80"); //getting database and collection
        collection = database.getCollection("20-80");

    }

}

