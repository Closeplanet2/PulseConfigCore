package com.pandapulsestudios.pulseconfig.Mongo;

import com.mongodb.client.MongoCollection;
import com.pandapulsestudios.pulseconfig.Objects.MongoConnection;
import com.pandapulsestudios.pulseconfig.PulseConfigMain;
import com.pandapulsestudios.pulsecore.Console.ConsoleAPI;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoAPI {
    public static void SET_LOGGER_LEVEL(Level level){
        var mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(level);
    }

    public static void CREATE_CONNECTION(String mongoIP, Object databaseName){
        var mongoConnection = MongoConnection.CREATE(mongoIP, databaseName.toString());
        PulseConfigMain.MONGO_CONNECTIONS.put(databaseName.toString(), mongoConnection);
    }

    public static void REMOVE_CONNECTION(Object databaseName){
        PulseConfigMain.MONGO_CONNECTIONS.remove(databaseName.toString());
    }

    public static boolean CONNECTION_EXISTS(Object databaseName){
        return PulseConfigMain.MONGO_CONNECTIONS.containsKey(databaseName.toString());
    }

    public static MongoConnection GET_CONNECTION(Object databaseName){
        return PulseConfigMain.MONGO_CONNECTIONS.getOrDefault(databaseName.toString(), null);
    }

    public static Document RETURN_FIRST_DOCUMENT(Object databaseName, Object collectionName){
        var mongoConnection = MongoAPI.GET_CONNECTION(databaseName.toString());
        var mongoCollection = mongoConnection.ReturnCollection(collectionName.toString());
        return mongoCollection.find().first();
    }

    public static List<Document> RETURN_ALL_DOCUMENTS_FROM_COLLECTION(Object databaseName, Object collectionName){
        var mongoConnection = MongoAPI.GET_CONNECTION(databaseName.toString());
        var mongoCollection = mongoConnection.ReturnCollection(collectionName.toString());
        var findIterable = mongoCollection.find();
        var documentList = new ArrayList<Document>();
        for (Document document : findIterable) documentList.add(document);
        return documentList;
    }
}
