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

        String uri = System.getenv("uri");
        MongoClientURI clientURI = new MongoClientURI(uri);
        MongoClient client = new MongoClient(clientURI);
        MongoDatabase database = client.getDatabase("count");
        collection = database.getCollection("count");
    }



    public static void set(String Id, String Key, Object value, boolean isAdd) throws InterruptedException {
        updateDB(Id,"serverId", Key, value, isAdd);
    }

    public static void setUser(String UserId, String Key, Object value, boolean isAdd) throws InterruptedException {
        updateDB(UserId, "userId", Key, value, isAdd );

    }


    public static Document get(String Id) throws InterruptedException {
        try{
            return (Document) collection.find(new Document("serverId", Id)).cursor().next();
        }catch (NoSuchElementException exception){
            System.out.println("No DB");
            createDB(Id);
            Thread.sleep(200);
            return (Document) collection.find(new Document("serverId", Id)).cursor().next();
        }

    }

    public static Object getUser(String userId, String key) throws InterruptedException {
        try{
            if(((Document) collection.find(new Document("userId", userId)).cursor().next()).get(key) != null){
                return ((Document) collection.find(new Document("userId", userId)).cursor().next()).get(key);
            }else{
                setUser(userId, "counted", 0.0, false);
                Thread.sleep(200);
                return ((Document) collection.find(new Document("userId", userId)).cursor().next()).get(key);
            }

        }catch (NoSuchElementException exception){
            setUser(userId, "counted", 0.0, false);
            Thread.sleep(200);
            return  ((Document) collection.find(new Document("userId", userId)).cursor().next()).get(key);
        }

    }

    public static Document getUserDoc(String userId) throws InterruptedException {
        try{
        return ((Document) collection.find(new Document("userId", userId)).cursor().next());
        }catch (NoSuchElementException exception){
        return null;
        }
    }

    private static void createDB(String Id){
        //server config
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
        //user config
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
                createDB(Id);
            }else{
                createUserDB(Id);
            }
            document = (Document) collection.find(new Document(field, Id)).cursor().next();
        }

        if(!isAdd){
            Document Updatedocument = new Document(key, value);
            Bson updateKey = new Document("$set", Updatedocument);
            collection.updateOne(document, updateKey);
        }else{
            Document Updatedocument;

            if(field.equalsIgnoreCase("serverId") ){
                try{

                    try{
                        Arrays.toString(document.get(key).toString().split(" "));
                        //it's so we can add extra dynamic fields to server settings document
                    }catch (NullPointerException exception){
                        Database.set(Id, key, "", false);
                    }
                    if(Arrays.stream(document.get(key).toString().split(" ")).anyMatch(users -> users.equalsIgnoreCase(((String) value).replace(" ", "")))) {
                        return;
                    }
                }catch (Exception exception){
                    Thread.sleep(250);
                }
            }

            try{
                try {
                    Updatedocument = new Document(key, Math.floor((Double)document.get(key)) + (Double) value) ;
                }catch (NullPointerException e){
                    Updatedocument = new Document(key, value);
                    if(field.equalsIgnoreCase("serverId")){
                        Database.set(Id, "channels", key + " ", true);
                        Database.set(Id, key,value, true);

                    }
                }

            }catch (Exception exception){
                Updatedocument = new Document(key, document.get(key) +  (String) value);
            }

            Bson updateKey = new Document("$set", Updatedocument);
            collection.updateOne(document, updateKey);
        }

    }//document.get(key)

}
