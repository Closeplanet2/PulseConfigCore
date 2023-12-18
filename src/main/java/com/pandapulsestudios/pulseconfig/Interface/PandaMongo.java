package com.pandapulsestudios.pulseconfig.Interface;

import com.pandapulsestudios.pulseconfig.APIS.MongoAPI;
import com.pandapulsestudios.pulseconfig.Serializers.ConfigSerializer;
import com.pandapulsestudios.pulseconfig.Serializers.MongoDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializers.MongoSerializer;

public interface PandaMongo {
    String databaseName();
    String documentID();

    default void SaveRaw(Class<?> mainClass) throws Exception {
        var mongoObject = MongoAPI.Return(databaseName());
        if(mongoObject == null) return;
        MongoSerializer.SaveMongo(this, mongoObject);
    }

    default void LoadRaw(Class<?> mainClass) throws Exception {
        var mongoObject = MongoAPI.Return(databaseName());
        if(mongoObject == null) return;
        MongoDeSerializer.LoadMongo(this, mongoObject);
    }

    default void DeleteRaw(Class<?> mainClass){

    }

    default void ExistsRaw(Class<?> mainClass){

    }
}
