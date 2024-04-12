package com.pandapulsestudios.pulseconfig.Objects.Mogno;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.pandapulsestudios.pulseconfig.Interfaces.Mongo.PulseMongo;
import org.bson.Document;

import java.util.UUID;

public class MongoConnection {
    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;

    public MongoConnection(String mongoIP, String databaseName){
        var uri = new MongoClientURI(mongoIP);
        mongoClient = new MongoClient(uri);
        mongoDatabase = mongoClient.getDatabase(databaseName);
    }

    public MongoCollection<Document> ReturnCollection(String collectionName){
        return mongoDatabase.getCollection(collectionName);
    }

    public Document DefaultDocument(PulseMongo pulseMongo){
        return new Document("MongoID", pulseMongo.documentID());
    }

    public long CountDocuments(String collectionName, String value){
        var mongoCollection = ReturnCollection(collectionName);
        return mongoCollection.countDocuments(new Document("MongoID", value));
    }

    public Document FindFirst(String collectionName){
        var mongoCollection = ReturnCollection(collectionName);
        return mongoCollection == null ? new Document("MongoID", UUID.randomUUID().toString()) : mongoCollection.find().first();
    }

    public Document Find(String collectionName, String value){
        var mongoCollection = ReturnCollection(collectionName);
        return mongoCollection == null ? new Document("MongoID", value) : mongoCollection.find(new Document("MongoID", value)).first();
    }

    public Document Find(String collectionName, String key, String value){
        var mongoCollection = ReturnCollection(collectionName);
        return mongoCollection == null ? null : mongoCollection.find(new Document(key, value)).first();
    }

    public FindIterable<Document> FindAll(String collectionName, String value){
        var mongoCollection = ReturnCollection(collectionName);
        return mongoCollection == null ? null : mongoCollection.find(new Document("MongoID", value));
    }

    public FindIterable<Document> FindAll(String collectionName, String key, String value){
        var mongoCollection = ReturnCollection(collectionName);
        return mongoCollection == null ? null : mongoCollection.find(new Document(key, value));
    }

    public void InsertOrReplace(String collectionName, String key, String value, Document a){
        var mongoCollection = ReturnCollection(collectionName);
        if(mongoCollection == null) return;
        var storedDocument = mongoCollection.find(new Document(key, value)).first();
        if(storedDocument != null) mongoCollection.replaceOne(storedDocument, a);
        else mongoCollection.insertOne(a);
    }

    public void Insert(String collectionName, Document a){
        var mongoCollection = ReturnCollection(collectionName);
        if(mongoCollection != null) mongoCollection.insertOne(a);
    }

    public void Replace(String collectionName, Document a, Document b){
        var mongoCollection = ReturnCollection(collectionName);
        if(mongoCollection != null) mongoCollection.replaceOne(a, b);
    }
}
