package com.pandapulsestudios.pulseconfig.Objects;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.pandapulsestudios.pulseconfig.Interface.PulseMongo;
import com.pandapulsestudios.pulseconfig.PulseConfig;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoConnection {
    private static final String DEFAULT_KEY = "MongoID";
    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;
    private final boolean debugSave;

    public MongoConnection(String mongoIP, String databaseName, boolean debugSave){
        var uri = new MongoClientURI(mongoIP);
        mongoClient = new MongoClient(uri);
        mongoDatabase = mongoClient.getDatabase(databaseName);
        PulseConfig.mongoConnections.put(databaseName, this);
        this.debugSave = debugSave;
    }

    public MongoCollection<Document> ReturnCollection(String collectionName){
        return mongoDatabase.getCollection(collectionName);
    }

    public List<Document> GetALL(String collectionName) {
        var collection = ReturnCollection(collectionName);
        var documents = new ArrayList<Document>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) documents.add(cursor.next());
        }
        return documents;
    }

    public long CountDocuments(String collectionName, String key, String value){
        if(key == null) key = DEFAULT_KEY;
        var mongoCollection = ReturnCollection(collectionName);
        return mongoCollection.countDocuments(new Document(DEFAULT_KEY, value));
    }

    public Document GetOne(String collectionName, String key, Object value) {
        var collection = ReturnCollection(collectionName);
        if(key == null) key = DEFAULT_KEY;
        return collection.find(Filters.eq(key, value)).first();
    }

    public void InsertOrReplace(String collectionName, String key, String value, Document document){
        if(key == null) key = DEFAULT_KEY;
        var storedDocument = GetOne(collectionName, key, value);
        if(storedDocument != null) Replace(collectionName, storedDocument, document);
        else Insert(collectionName, document);
    }

    public void Insert(String collectionName, Document a){
        var mongoCollection = ReturnCollection(collectionName);
        mongoCollection.insertOne(a);
    }

    public void Replace(String collectionName, Document a, Document b){
        var mongoCollection = ReturnCollection(collectionName);
        mongoCollection.replaceOne(a, b);
    }

    public void Delete(String collectionName, String key, Object value) {
        if(key == null) key = DEFAULT_KEY;
        var mongoCollection = ReturnCollection(collectionName);
        mongoCollection.deleteOne(Filters.eq(key, value));
    }

    public Document DefaultDocument(PulseMongo pulseMongo){
        return new Document(DEFAULT_KEY, pulseMongo.documentID());
    }
}
