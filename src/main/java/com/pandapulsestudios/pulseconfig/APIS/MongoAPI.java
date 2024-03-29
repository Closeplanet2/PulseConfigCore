package com.pandapulsestudios.pulseconfig.APIS;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.pandapulsestudios.pulseconfig.Interfaces.ConfigPath;
import com.pandapulsestudios.pulseconfig.Interfaces.JSON.PulseJson;
import com.pandapulsestudios.pulseconfig.Interfaces.Mongo.PulseMongo;
import com.pandapulsestudios.pulseconfig.Objects.Mogno.MongoConnection;
import com.pandapulsestudios.pulseconfig.PulseConfig;
import com.pandapulsestudios.pulseconfig.Serializers.Mongo.MongoDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializers.Mongo.MongoSerializer;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoAPI {
    public static void setLevel(Level level){
        var mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(level);
    }

    public static MongoCollection<Document> ConvertToMongoCollection(FindIterable<Document> findIterable) {
        var mongoIterable = findIterable;
        var cursor = mongoIterable.iterator();
        if (cursor.hasNext()) {
            var firstDocument = cursor.next();
            return firstDocument.entrySet().stream().filter(entry -> entry.getValue() instanceof MongoCollection).map(entry -> (MongoCollection<Document>) entry.getValue()).findFirst().orElse(null);
        }
        return null;
    }

    public static MongoConnection CreateConnection(String mongoIP, Object databaseName){
        var mongoConnection = new MongoConnection(mongoIP, databaseName.toString());
        PulseConfig.MongoConnections.put(databaseName.toString(), mongoConnection);
        return mongoConnection;
    }

    public static MongoConnection ReturnConnection(Object databaseName){
        return PulseConfig.MongoConnections.getOrDefault(databaseName.toString(), null);
    }

    public static void Save(PulseMongo pulseMongo, boolean debugSave) {
        try { SaveRaw(pulseMongo, debugSave); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    public static void SaveRaw(PulseMongo pulseMongo, boolean debugSave) throws Exception {
        var mongoObject = PulseConfig.MongoConnections.getOrDefault(pulseMongo.databaseName(), null);
        if(mongoObject == null) return;
        if(mongoObject.CountDocuments(pulseMongo.getClass().getSimpleName(), pulseMongo.documentID()) == 0) pulseMongo.FirstLoad();
        MongoSerializer.SaveMongo(pulseMongo, mongoObject, debugSave);
    }

    public static void Load(PulseMongo pulseMongo, boolean debugSave) {
        try { LoadRaw(pulseMongo, debugSave); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    public static void LoadRaw(PulseMongo pulseMongo, boolean debugSave) throws Exception {
        var mongoObject = PulseConfig.MongoConnections.getOrDefault(pulseMongo.databaseName(), null);
        if(mongoObject == null) return;
        var foundDocuments = mongoObject.CountDocuments(pulseMongo.getClass().getSimpleName(), pulseMongo.documentID());
        if(foundDocuments == 0){
            pulseMongo.FirstLoad();
            SaveRaw(pulseMongo, debugSave);
        }else{
            MongoDeSerializer.LoadMongo(pulseMongo, mongoObject, debugSave);
        }
    }
}
