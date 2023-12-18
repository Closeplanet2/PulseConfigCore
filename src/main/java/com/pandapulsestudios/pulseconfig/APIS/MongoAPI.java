package com.pandapulsestudios.pulseconfig.APIS;

import com.pandapulsestudios.pulseconfig.Object.MongoObject;
import com.pandapulsestudios.pulseconfig.PulseConfigMain;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoAPI {
    public static void setLevel(Level level){
        var mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(level);
    }

    public static void Create(String mongoIP, Object databaseName){
        var mongoConnection = new MongoObject(mongoIP, databaseName.toString());
        PulseConfigMain.MONGO_CONNECTIONS.put(databaseName.toString(), mongoConnection);
    }

    public static void Remove(Object databaseName){
        PulseConfigMain.MONGO_CONNECTIONS.remove(databaseName.toString());
    }

    public static boolean Exists(Object databaseName){
        return PulseConfigMain.MONGO_CONNECTIONS.containsKey(databaseName.toString());
    }

    public static MongoObject Return(Object databaseName){
        return PulseConfigMain.MONGO_CONNECTIONS.getOrDefault(databaseName.toString(), null);
    }
}
