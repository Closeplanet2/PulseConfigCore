package com.pandapulsestudios.pulseconfig.API;

import com.pandapulsestudios.pulseconfig.Interface.PulseMongo;
import com.pandapulsestudios.pulseconfig.Objects.MongoConnection;
import com.pandapulsestudios.pulseconfig.Serializer.SerializerHelpers;

import java.util.HashMap;

public class MongoAPI {
    public static HashMap<String, PulseMongo> ReturnAllMongoDocuments(PulseMongo pulseMongo, boolean debug){
        var data = new HashMap<String, PulseMongo>();
        var mongoConnection = com.pandapulsestudios.pulseconfig.PulseConfig.mongoConnections.getOrDefault(pulseMongo.databaseName(), new MongoConnection(pulseMongo.mongoIP(), pulseMongo.databaseName(), false));
        for(var document : mongoConnection.GetALL(pulseMongo.collectionName())){
            if(!document.containsKey("MongoID")) continue;
            var fileName = document.get("MongoID").toString();
            var newInstance = SerializerHelpers.CreateInstanceWithID(fileName, pulseMongo.getClass());
            if(newInstance == null) continue;
            var pm = (PulseMongo) newInstance;
            StorageAPI.Load(pm, debug);
            data.put(fileName, pm);
        }
        return data;
    }
}
