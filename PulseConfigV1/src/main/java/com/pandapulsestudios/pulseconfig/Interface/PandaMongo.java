package com.pandapulsestudios.pulseconfig.Interface;

import com.mongodb.client.MongoCollection;
import com.pandapulsestudios.pulseconfig.Helpers.Serializer;
import com.pandapulsestudios.pulseconfig.Mongo.MongoAPI;
import com.pandapulsestudios.pulseconfig.Objects.MongoConnection;
import com.pandapulsestudios.pulsecore.Console.ConsoleAPI;
import org.bson.BsonDocument;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface PandaMongo {
    String DATABASE_NAME();

    String DOCUMENT_ID();

    Class<?> ReturnClassType();

    default String COLLECTION_NAME() {
        return this.getClass().getSimpleName();
    }

    default void SAVE() {
        try { this.SAVE_RAW();}
        catch (Exception exception) {exception.printStackTrace();}
    }

    default void LOAD() {
        try { this.LOAD_RAW();}
        catch (Exception exception) {exception.printStackTrace();}
    }

    default void SAVE_RAW() throws Exception {
        var mongoConnection = this.ReturnConnection();
        var mongoCollection = mongoConnection.ReturnCollection(this.COLLECTION_NAME());
        var found = mongoCollection.find(new Document("MongoID", this.DOCUMENT_ID())).first();
        var document = Serializer.SAVE(this, null, mongoConnection, "");
        if (found != null) {mongoCollection.replaceOne(found, document);}
        else {mongoCollection.insertOne(document);}
    }

    default void LOAD_RAW() throws Exception {
        var mongoConnection = this.ReturnConnection();
        var mongoCollection = mongoConnection.ReturnCollection(this.COLLECTION_NAME());
        var found = mongoCollection.find(new Document("MongoID", this.DOCUMENT_ID())).first();
        if (found == null) {this.SAVE();}
        else {Serializer.LOAD(this, null, found);}
    }

    default void LOAD_FROM_DOCUMENT(Document found) {
        try {Serializer.LOAD(this, null, found);}
        catch (Exception var3) {var3.printStackTrace();}
    }

    default void DELETE_DOCUMENT() {
        MongoConnection mongoConnection = this.ReturnConnection();
        MongoCollection<Document> mongoCollection = mongoConnection.ReturnCollection(this.COLLECTION_NAME());
        mongoCollection.findOneAndDelete(new Document("MongoID", this.DOCUMENT_ID()));
    }

    default boolean ExistsInCollection(){
        MongoConnection mongoConnection = this.ReturnConnection();
        MongoCollection<Document> mongoCollection = mongoConnection.ReturnCollection(this.COLLECTION_NAME());
        return mongoCollection.countDocuments(new Document("MongoID", this.DOCUMENT_ID())) > 0;
    }

    default MongoConnection ReturnConnection() {
        return MongoAPI.GET_CONNECTION(this.DATABASE_NAME());
    }
}
