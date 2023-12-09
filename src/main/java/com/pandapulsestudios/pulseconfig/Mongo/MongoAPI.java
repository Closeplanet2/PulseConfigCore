package com.pandapulsestudios.pulseconfig.Mongo;

import com.pandapulsestudios.pulseconfig.Objects.MongoConnection;
import com.pandapulsestudios.pulseconfig.PulseConfigMain;

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
}
