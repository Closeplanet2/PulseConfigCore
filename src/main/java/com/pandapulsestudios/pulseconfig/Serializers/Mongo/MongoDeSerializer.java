package com.pandapulsestudios.pulseconfig.Serializers.Mongo;

import com.pandapulsestudios.pulseconfig.Interfaces.JSON.PulseJson;
import com.pandapulsestudios.pulseconfig.Interfaces.Mongo.PulseMongo;
import com.pandapulsestudios.pulseconfig.Interfaces.PulseClass;
import com.pandapulsestudios.pulseconfig.Interfaces.SaveName;
import com.pandapulsestudios.pulseconfig.Objects.JSON.JsonObject;
import com.pandapulsestudios.pulseconfig.Objects.Mogno.MongoConnection;
import com.pandapulsestudios.pulseconfig.Objects.Savable.SaveableArrayList;
import com.pandapulsestudios.pulseconfig.Objects.Savable.SaveableHashmap;
import com.pandapulsestudios.pulseconfig.Serializers.Config.ConfigDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializers.JSON.JSONDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializers.SerializerHelpers;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import com.pandapulsestudios.pulsecore.Data.Interface.CustomVariable;
import com.pandapulsestudios.pulsecore.Java.ClassAPI;
import com.pandapulsestudios.pulsecore.Java.JavaAPI;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MongoDeSerializer {
    public static void LoadMongo(PulseMongo pulseMongo, MongoConnection mongoConnection, boolean debugSave) throws Exception {
        pulseMongo.BeforeLoad();
        var storedDocument = mongoConnection.Find(pulseMongo.getClass().getSimpleName(), pulseMongo.documentID());
        if(storedDocument == null) return;
        var dataFields = SerializerHelpers.ReturnALlFields(pulseMongo);
        for(var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            if(!storedDocument.containsKey(fieldName)) field.set(pulseMongo, null);
            else field.set(pulseMongo, LoadConfig(dataFields.get(field), storedDocument.get(fieldName)));
        }
        pulseMongo.AfterLoad();
    }

    public static Object LoadConfigPulseClass(PulseClass pulseClass, Document configData) throws Exception {
        pulseClass.BeforeLoad();
        var dataFields = SerializerHelpers.ReturnALlFields(pulseClass);
        for (var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            if(!configData.containsKey(fieldName)) field.set(pulseClass, null);
            else field.set(pulseClass, LoadConfig(dataFields.get(field), configData.get(fieldName)));
        }
        pulseClass.AfterLoad();
        return pulseClass;
    }

    public static SaveableHashmap<Object, Object> LoadConfigHashMap(SaveableHashmap<Object, Object> saveableHashmap, Document configData) throws Exception {
        for(var dataKey : configData.keySet()){
            var dataValue = configData.get(dataKey);
            saveableHashmap.AddData(dataKey, dataValue);
        }
        return saveableHashmap;
    }

    public static SaveableArrayList<Object> LoadConfigArray(SaveableArrayList<Object> saveableArrayList, List<Object> configData) throws Exception {
        for(var data : configData) saveableArrayList.AddData(data);
        return saveableArrayList;
    }

    public static Object LoadConfig(Object classData, Object configData) throws Exception {
        if(classData == null || configData == null) return null;
        if(classData instanceof PulseClass pulseClass){
            return LoadConfigPulseClass(pulseClass, (Document) configData);
        }else if(classData instanceof SaveableHashmap saveableHashmap){
            return LoadConfigHashMap(saveableHashmap, (Document) configData);
        }else if(classData instanceof SaveableArrayList saveableArrayList){
            return LoadConfigArray(saveableArrayList, (List<Object>) configData);
        }else if(classData instanceof CustomVariable){
            var configHashMap = (HashMap<String, Object>) configData;
            if(configHashMap.isEmpty()) return new HashMap<String, Object>();
            if(!configHashMap.containsKey("CLASS_TYPE") || !configHashMap.containsKey("DATA")) return new HashMap<String, Object>();
            var serialisedClassName = (String) configHashMap.get("CLASS_TYPE");
            var serialisedData = (HashMap<String, Object>) configHashMap.get("DATA");
            return ((CustomVariable) Class.forName(serialisedClassName).getDeclaredConstructor().newInstance()).DeSerializeData(serialisedData);
        }else if(ConfigurationSerializable.class.isAssignableFrom(classData.getClass())){
            return configData;
        }else if(classData instanceof Date){
            return SerializerHelpers.SimpleDateFormat.parse(configData.toString());
        }else{
            var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(classData.getClass());
            return variableTest == null ? configData.toString() : variableTest.DeSerializeData(configData);
        }
    }
}
