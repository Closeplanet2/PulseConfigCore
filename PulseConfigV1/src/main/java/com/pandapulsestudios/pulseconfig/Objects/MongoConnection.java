package com.pandapulsestudios.pulseconfig.Objects;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.pandapulsestudios.pulsecore.Console.ConsoleAPI;
import org.bson.Document;
import org.bukkit.ChatColor;

public class MongoConnection {
    public static MongoConnection CREATE(String mongoIP, String databaseName){return new MongoConnection(mongoIP, databaseName);}

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    public MongoConnection(String mongoIP, String databaseName){
        var clientURI = new MongoClientURI(mongoIP);
        mongoClient = new MongoClient(clientURI);
        mongoDatabase = mongoClient.getDatabase(databaseName);
    }

    public MongoCollection<Document> ReturnCollection(String collectionName){
        return mongoDatabase.getCollection(collectionName);
    }
}
