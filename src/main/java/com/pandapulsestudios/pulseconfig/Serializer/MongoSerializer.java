package com.pandapulsestudios.pulseconfig.Serializer;

import com.pandapulsestudios.pulseconfig.Interface.PulseMongo;
import com.pandapulsestudios.pulseconfig.Objects.MongoConnection;
import com.pandapulsestudios.pulseconfig.Interface.CustomVariable;
import com.pandapulsestudios.pulseconfig.Interface.PulseClass;
import com.pandapulsestudios.pulseconfig.Interface.SaveName;
import com.pandapulsestudios.pulseconfig.Interface.StorageComment;
import com.pandapulsestudios.pulseconfig.Objects.SaveAbleInventory;
import com.pandapulsestudios.pulseconfig.Objects.SaveableArrayList;
import com.pandapulsestudios.pulseconfig.Objects.SaveableHashmap;
import com.pandapulsestudios.pulseconfig.Objects.SaveableLinkedHashMap;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

public class MongoSerializer {
    public static void SaveMongo(PulseMongo pulseMongo, MongoConnection mongoConnection) throws IllegalAccessException {
        pulseMongo.BeforeSaveMongo();
        var convertedDocument = mongoConnection.DefaultDocument(pulseMongo);
        var serialiseDocument = ReturnClassFields(pulseMongo.getClass(), pulseMongo);
        for(var key  : serialiseDocument.keySet()) convertedDocument.put(key, serialiseDocument.get(key));
        pulseMongo.AfterSaveMongo();
        mongoConnection.InsertOrReplace(pulseMongo.collectionName(), null, pulseMongo.documentID(), convertedDocument);
    }

    private static LinkedHashMap<String, Object> ReturnClassFields(Class<?> parentClass, Object object) throws IllegalAccessException {
        var storedData = new LinkedHashMap<String, Object>();
        var dataFields = SerializerHelpers.ReturnALlFields(parentClass, object);
        for(var field : dataFields.keySet()){
            var fieldComment = field.isAnnotationPresent(StorageComment.class) ? field.getAnnotation(StorageComment.class).value() : "";
            if(!fieldComment.isEmpty()) storedData.put(String.format("# +------------------%s", fieldComment), "------------------+ #");
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            if(fieldName.isEmpty()) fieldName = field.getName();
            storedData.put(fieldName, SaveMongoSingle(dataFields.get(field)));
        }
        return storedData;
    }

    public static Object SaveMongoSingle(Object storedData) throws IllegalAccessException {
        if(storedData == null) return null;
        var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(storedData.getClass());
        if(storedData instanceof SaveAbleInventory){
            return null;
        } else if(ConfigurationSerialization.class.isAssignableFrom(storedData.getClass())){
            return null;
        } else if(storedData instanceof PulseClass pulseClass){
            pulseClass.BeforeSaveConfig();
            var data = ReturnClassFields(pulseClass.getClass(), pulseClass);
            pulseClass.AfterSaveConfig();
            return data;
        } else if(storedData instanceof SaveableHashmap saveableHashmap){
            var returnData = new LinkedHashMap<>();
            for(var key : saveableHashmap.keySet()) returnData.put(SaveMongoSingle(key), SaveMongoSingle(saveableHashmap.get(key)));
            return returnData;
        } else if(storedData instanceof SaveableLinkedHashMap saveableLinkedHashMap){
            var returnData = new LinkedHashMap<>();
            for(var key : saveableLinkedHashMap.keySet()) returnData.put(SaveMongoSingle(key), SaveMongoSingle(saveableLinkedHashMap.get(key)));
            return returnData;
        } else if(storedData instanceof SaveableArrayList saveableArrayList){
            var returnData = new ArrayList<>();
            for(var key : saveableArrayList.ReturnArrayList()) returnData.add(SaveMongoSingle(key));
            return returnData;
        } else if(storedData instanceof CustomVariable customVariable){
            customVariable.BeforeSaveConfig();
            var data = customVariable.SerializeData();
            customVariable.AfterSaveConfig();
            var returnData = new LinkedHashMap<>();
            for(var key : data.keySet()) returnData.put(SaveMongoSingle(key), SaveMongoSingle(data.get(key)));
            return returnData;
        }
        else if(storedData instanceof Date date) return SerializerHelpers.SimpleDateFormat.format(date);
        else if(variableTest != null) return variableTest.SerializeData(storedData);
        return storedData;
    }
}
