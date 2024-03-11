package com.pandapulsestudios.pulseconfig.Serializers.Mongo;

import com.pandapulsestudios.pulseconfig.Interfaces.JSON.PulseJson;
import com.pandapulsestudios.pulseconfig.Interfaces.Mongo.PulseMongo;
import com.pandapulsestudios.pulseconfig.Interfaces.PulseClass;
import com.pandapulsestudios.pulseconfig.Objects.JSON.JsonObject;
import com.pandapulsestudios.pulseconfig.Objects.Mogno.MongoConnection;
import com.pandapulsestudios.pulseconfig.Objects.Savable.SaveableArrayList;
import com.pandapulsestudios.pulseconfig.Objects.Savable.SaveableHashmap;
import com.pandapulsestudios.pulseconfig.Serializers.JSON.JSONSerializer;
import com.pandapulsestudios.pulseconfig.Serializers.SerializerHelpers;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import com.pandapulsestudios.pulsecore.Data.Interface.CustomVariable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class MongoSerializer {
    public static void SaveMongo(PulseMongo pulseMongo, MongoConnection mongoConnection, boolean debugSave) throws Exception {
        var convertedDocument = mongoConnection.DefaultDocument(pulseMongo);
        var dataFields = SerializerHelpers.ReturnALlFields(pulseMongo);
        for(var field : dataFields.keySet()){
            var fieldName = SerializerHelpers.ReturnFieldName(field);
            convertedDocument.put(fieldName, SaveConfigSingle(dataFields.get(field)));
        }
        mongoConnection.InsertOrReplace(pulseMongo.getClass().getSimpleName(), "MongoID", pulseMongo.documentID(), convertedDocument);
    }

    public static HashMap<String, Object> SaveConfig(PulseClass pulseClass) throws Exception {
        pulseClass.BeforeSave();
        var storedData = new LinkedHashMap<String, Object>();
        storedData.put("CLASS_TYPE", pulseClass.getClass().getName());

        var dataFields = SerializerHelpers.ReturnALlFields(pulseClass);
        for(var field : dataFields.keySet()){
            var fieldName = SerializerHelpers.ReturnFieldName(field);
            storedData.put(fieldName, SaveConfigSingle(dataFields.get(field)));
        }

        pulseClass.AfterSave();
        return storedData;
    }

    private static HashMap<Object, Object> SaveConfig(SaveableHashmap saveableHashmap) throws Exception {
        var returnData = new LinkedHashMap<>();
        for(var sdKey : saveableHashmap.hashMap.keySet()) returnData.put(SaveConfigSingle(sdKey), SaveConfigSingle(saveableHashmap.hashMap.get(sdKey)));
        return returnData;
    }

    private static List<Object> SaveConfig(SaveableArrayList saveableArrayList) throws Exception {
        var returnData = new ArrayList<>();
        for(var sdValue : saveableArrayList.arrayList) returnData.add(SaveConfigSingle(sdValue));
        return returnData;
    }

    public static Object SaveConfigSingle(Object storedData) throws Exception {
        if(storedData == null) return null;
        if(storedData instanceof PulseClass pulseClass) return SaveConfig(pulseClass);
        else if(storedData instanceof SaveableHashmap saveableHashmap) return SaveConfig(saveableHashmap);
        else if(storedData instanceof SaveableArrayList saveableArrayList) return SaveConfig(saveableArrayList);
        else if(storedData instanceof CustomVariable customVariable){
            var data = new LinkedHashMap<String, Object>();
            data.put("CLASS_TYPE", customVariable.getClass().getName());
            data.put("DATA", customVariable.SerializeData());
            return data;
        }else if(storedData instanceof Date date) return SerializerHelpers.SimpleDateFormat.format(date);
        else{
            var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(storedData.getClass());
            return variableTest == null ? storedData.toString() : variableTest.SerializeData(storedData);
        }
    }
}
