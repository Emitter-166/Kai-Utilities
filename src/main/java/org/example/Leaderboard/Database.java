package org.example.Leaderboard;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;


import java.util.Arrays;
import java.util.NoSuchElementException;

public class Database extends ListenerAdapter {
    public static MongoCollection collection;
    @Override
    public void onReady(ReadyEvent e){
        //it will retrieve necessary fields we need on bot start up
        String uri = System.getenv("uri"); //Mongo DB uri
        MongoClientURI clientURI = new MongoClientURI(uri);
        MongoClient client = new MongoClient(clientURI);
        MongoDatabase database = client.getDatabase("count");
        collection = database.getCollection("count"); //getting the collection, what we need in order to perform every database operations
    }



    public static void set(String Id, String Key, Object value, boolean isAdd) throws InterruptedException {
        //a method that will update/create/set server settings
        updateDB(Id,"serverId", Key, value, isAdd);
    }

    public static void setUser(String UserId, String Key, Object value, boolean isAdd) throws InterruptedException {
        //a method that will update/create/set user infos
        updateDB(UserId, "userId", Key, value, isAdd );

    }


    public static Document get(String Id) throws InterruptedException {
        //method to get server settings from db
        try{
            return (Document) collection.find(new Document("serverId", Id)).cursor().next();
        }catch (NoSuchElementException exception){
            System.out.println("No DB");
            createDB(Id); //it will create server settings document if needed
            Thread.sleep(200);
            return (Document) collection.find(new Document("serverId", Id)).cursor().next();
        }

    }

    public static Object getUser(String userId, String key) throws InterruptedException {
        //method to get user infos
        try{
            if(((Document) collection.find(new Document("userId", userId)).cursor().next()).get(key) != null){
                return ((Document) collection.find(new Document("userId", userId)).cursor().next()).get(key);
            }else{
                setUser(userId, "counted", 0.0, false); //it set user db according to the template incase it is corrupted
                Thread.sleep(200);
                return ((Document) collection.find(new Document("userId", userId)).cursor().next()).get(key);
            }

        }catch (NoSuchElementException exception){
            setUser(userId, "counted", 0.0, false); //it will create an userinfo document if there is none for that user
            Thread.sleep(200);
            return  ((Document) collection.find(new Document("userId", userId)).cursor().next()).get(key);
        }
    }

    public static Document getUserDoc(String userId) throws InterruptedException {
        //this will return whole user document instead of one field, it will come in use when we try to clear leaderboard
        try{
            return ((Document) collection.find(new Document("userId", userId)).cursor().next());
        }catch (NoSuchElementException exception){
            return null;
        }
    }

    private static void createDB(String Id){
        //server config template
        Document document = new Document("serverId", Id)
                .append("actionChannel", "0")
                .append("roleToMention", "0")
                .append("mainChat", "")
                .append("reset", true)
                .append("channels", "")
                .append("users", "");

        collection.insertOne(document);

    }


    private static void createUserDB(String userId){
        //user config template, for recent changes to structure, there is no extra fields required
        Document document = new Document("userId", userId); //counted amount
        collection.insertOne(document);
    }


    private static void updateDB(String Id, String field,  String key, Object value, boolean isAdd) throws InterruptedException {
        //for server
        Document document;
        try{
            document = (Document) collection.find(new Document(field, Id)).cursor().next();
        }catch (Exception exception){
            if(field.equalsIgnoreCase("serverId")){
                //creates db if there is none, according to the field
                createDB(Id);
            }else{
                createUserDB(Id);
            }
            document = (Document) collection.find(new Document(field, Id)).cursor().next();
        }

        if(!isAdd){
            //checks if it's an add operation or not
            Document Updatedocument = new Document(key, value);
            Bson updateKey = new Document("$set", Updatedocument);
            collection.updateOne(document, updateKey);
        }else{
            Document Updatedocument;

            if(field.equalsIgnoreCase("serverId") ){
                try{
                    try{
                        Arrays.toString(document.get(key).toString().split(" "));
                    }catch (NullPointerException exception){
                        //it's so we can add extra dynamic fields to server settings document
                        Database.set(Id, key, "", false);
                        System.out.println("Added channel: " +  key);
                    }

                    if(Arrays.stream(document.get(key).toString().split(" ")).anyMatch(users -> users.equalsIgnoreCase(((String) value).replace(" ", "")))) {
                        //checking if the value already exist
                        return;
                    }
                }catch (Exception exception){
                    Thread.sleep(250);
                }
            }

            try{
                try {
                    //removing salt from previous database value and adding the new one
                    Updatedocument = new Document(key, Math.floor((Double)document.get(key)) + (Double) value);
                }catch (NullPointerException e){
                    //if the previous value doesn't exist
                    Updatedocument = new Document(key, value);

                    if(field.equalsIgnoreCase("serverId")){
                        //in case of server settings, we will add key to channels field and make a new field with the key (which is used to store that channels info)
                        Database.set(Id, "channels", key + " ", true);
                        Database.set(Id, key,value, true);

                    }
                }

            }catch (Exception exception){
                //in case the value cannot be added with a double
                Updatedocument = new Document(key, document.get(key) +  (String) value);
            }
            //updating the document
            Bson updateKey = new Document("$set", Updatedocument);
            collection.updateOne(document, updateKey);
        }

    }

}
