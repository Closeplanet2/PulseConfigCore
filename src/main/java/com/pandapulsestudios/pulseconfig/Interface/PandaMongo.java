package com.pandapulsestudios.pulseconfig.Interface;

import com.mongodb.client.MongoCollection;
import com.pandapulsestudios.pulseconfig.Helpers.Serializer;
import com.pandapulsestudios.pulseconfig.Mongo.MongoAPI;
import com.pandapulsestudios.pulseconfig.Objects.MongoConnection;
import org.bson.Document;

public interface PandaMongo {
    String DATABASE_NAME();

    String DOCUMENT_ID();

    Class<?> ReturnClassType();

    default String COLLECTION_NAME() {
        return this.getClass().getSimpleName();
    }

    default void SAVE() {
        try {
            this.SAVE_RAW();
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    default void LOAD() {
        try {
            this.LOAD_RAW();
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    default void SAVE_RAW() throws Exception {
        MongoConnection mongoConnection = this.ReturnConnection();
        MongoCollection<Document> mongoCollection = mongoConnection.ReturnCollection(this.COLLECTION_NAME());
        Document found = (Document) mongoCollection.find(new Document("MongoID", this.DOCUMENT_ID())).first();
        Document document = Serializer.SAVE(this, (PandaClass)null, mongoConnection, "");
        if (found != null) {
            mongoCollection.replaceOne(found, document);
        } else {
            mongoCollection.insertOne(document);
        }

    }

    default void LOAD_RAW() throws Exception {
        MongoConnection mongoConnection = this.ReturnConnection();
        MongoCollection<Document> mongoCollection = mongoConnection.ReturnCollection(this.COLLECTION_NAME());
        Document found = (Document)mongoCollection.find(new Document("MongoID", this.DOCUMENT_ID())).first();
        if (found == null) {
            this.SAVE();
        } else {
            Serializer.LOAD(this, (PandaClass)null, found);
        }

    }

    default void LOAD_FROM_DOCUMENT(Document found) {
        try {
            Serializer.LOAD(this, (PandaClass)null, found);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    default void DELETE_DOCUMENT() {
        MongoConnection mongoConnection = this.ReturnConnection();
        MongoCollection<Document> mongoCollection = mongoConnection.ReturnCollection(this.COLLECTION_NAME());
        mongoCollection.findOneAndDelete(new Document("MongoID", this.DOCUMENT_ID()));
    }

    default MongoConnection ReturnConnection() {
        return MongoAPI.GET_CONNECTION(this.DATABASE_NAME());
    }
}
