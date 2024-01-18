package com.pandapulsestudios.pulseconfig.Object;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.pandapulsestudios.pulseconfig.Interface.PandaMongo;
import com.pandapulsestudios.pulseconfig.Serializers.SerializerHelpers;
import org.bson.Document;

import java.util.UUID;

public class MongoObject {
    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;

    public MongoObject(String mongoIP, String databaseName){
        var uri = new MongoClientURI(mongoIP);
        mongoClient = new MongoClient(uri);
        mongoDatabase = mongoClient.getDatabase(databaseName);
    }

    public MongoCollection<Document> ReturnCollection(String collectionName){
        return mongoDatabase.getCollection(collectionName);
    }

    public Document DefaultDocument(PandaMongo pandaMongo){
        return new Document(SerializerHelpers.MongoID, pandaMongo.documentID());
    }

    public Document FindFirst(String collectionName){
        var mongoCollection = ReturnCollection(collectionName);
        var key = SerializerHelpers.MongoID;
        return mongoCollection == null ? new Document(key, UUID.randomUUID().toString()) : mongoCollection.find().first();
    }

    public Document Find(String collectionName, String value){
        var mongoCollection = ReturnCollection(collectionName);
        var key = SerializerHelpers.MongoID;
        return mongoCollection == null ? new Document(key, value) : mongoCollection.find(new Document(key, value)).first();
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
