package org.example.captionMe;

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

    public static void set(String Id, String field, String Key, Object value, boolean isAdd) throws InterruptedException {
        //for ease of understanding while coding, this method is added, there is no real use of it
        updateDB(Id, field, Key, value, isAdd);
    }

    public static Document get(String Id, String field) throws InterruptedException {
        //it will return server settings from database collection
        try {
            return collection.find(new Document(field, Id)).cursor().next();
        } catch (NoSuchElementException exception) {
            if (field.equalsIgnoreCase("serverId")) {
                createDB(Id);
            }else if(field.equalsIgnoreCase("adId")){
                createAdDB(Id);
            }
            return collection.find(new Document(field, Id)).cursor().next();
        }

    }

    private static void createDB(String Id) {
        //server config, here is the template used to make new settings document on db collection
        Document document = new Document("serverId", Id)
                .append("serverId", "")
                .append("adIds", "");

        collection.insertOne(document);

    }

    private static void createAdDB(String Id) {
        //server config, here is the template used to make new settings document on db collection
        Document document = new Document("serverId", Id)
                .append("adId", Id) //ad_name
                .append("adType", "") //image, text, text_image, API
                .append("channels", "")
                .append("text", "")
                .append("embed_urls", "")
                .append("repeat_every", 0L)
                .append("last_sent_on", 0L);

        collection.insertOne(document);

    }


    private static void updateDB(String Id, String field, String key, Object value, boolean isAdd) throws InterruptedException {
        //for server
        Document document = null;
        try {
            //it will try to assign value to document, if there is no server settings for role logging, it will create one
            document = collection.find(new Document(field, Id)).cursor().next();
        } catch (NoSuchElementException exception) {
            if (field.equalsIgnoreCase("serverId")) {
                createDB(Id);
            }else if(field.equalsIgnoreCase("adId")){
                createAdDB(Id);
            }
            document = collection.find(new Document(field, Id)).cursor().next();
        }

        if (!isAdd) {
                //it will check if the values should be added with the previous value
                Document Updatedocument = new Document(key, value);
                Bson updateKey = new Document("$set", Updatedocument);
                collection.updateOne(document, updateKey);
            } else {
                Document Updatedocument = null;
                if (value.getClass().getSimpleName().equalsIgnoreCase("Integer")) {
                    Updatedocument = new Document(key, ((int) document.get(key) + (int) value));
                } else if(value.getClass().getSimpleName().equalsIgnoreCase("String")){
                    Updatedocument = new Document(key, (document.get(key) + (String) value));
                }else if(value.getClass().getSimpleName().equalsIgnoreCase("Long")){
                    Updatedocument = new Document(key, ((long)document.get(key) + (long) value));
                }
                Bson updateKey = new Document("$set", Updatedocument);
                collection.updateOne(document, updateKey);
            }
    }
    @Override
    public void onReady (ReadyEvent e){
        //it will update collection everytime bot starts up
        String uri = token.getUri(); //Mongo DB uri
        MongoClientURI clientURI = new MongoClientURI(uri);
        MongoClient client = new MongoClient(clientURI);
        MongoDatabase database = client.getDatabase("advertisements"); //getting database and collection
        collection = database.getCollection("advertisements");

    }
}
