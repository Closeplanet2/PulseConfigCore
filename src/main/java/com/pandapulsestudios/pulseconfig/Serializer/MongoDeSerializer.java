package com.pandapulsestudios.pulseconfig.Serializer;

import com.pandapulsestudios.pulseconfig.Interface.PulseMongo;
import com.pandapulsestudios.pulseconfig.Objects.MongoConnection;
import com.pandapulsestudios.pulseconfig.Interface.CustomVariable;
import com.pandapulsestudios.pulseconfig.Interface.PulseClass;
import com.pandapulsestudios.pulseconfig.Interface.SaveName;
import com.pandapulsestudios.pulseconfig.Objects.SaveAbleInventory;
import com.pandapulsestudios.pulseconfig.Objects.SaveableArrayList;
import com.pandapulsestudios.pulseconfig.Objects.SaveableHashmap;
import com.pandapulsestudios.pulseconfig.Objects.SaveableLinkedHashMap;
import com.pandapulsestudios.pulseconfig.Enum.StorageType;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MongoDeSerializer {
    public static void LoadMongo(PulseMongo pulseMongo, MongoConnection mongoConnection) throws ParseException, IllegalAccessException {
        pulseMongo.BeforeLoadMongo();
        var storedData = mongoConnection.GetOne(pulseMongo.collectionName(), null, pulseMongo.documentID());
        var data = ReturnClassFields(storedData, pulseMongo.getClass(), pulseMongo);
        pulseMongo.AfterLoadMongo();
    }

    public static Object ReturnClassFields(Document configData, Class<?> parentClass, Object object) throws IllegalAccessException, ParseException {
        var dataFields = SerializerHelpers.ReturnALlFields(parentClass, object);
        for(var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            if(!configData.containsKey(fieldName)) field.set(object, MongoSerializer.SaveMongoSingle(dataFields.get(field)));
            else {
                var deSerialisedData = LoadMongoSingle(dataFields.get(field), configData.get(fieldName));
                if(deSerialisedData != null){
                    try{field.set(object, deSerialisedData);}
                    catch(Exception ignored) {field.set(object, dataFields.get(field));}
                }
            }
        }
        return object;
    }

    public static Object LoadMongoSingle(Object classData, Object configData) throws IllegalAccessException, ParseException {
        if(classData == null || configData == null) return null;
        var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(classData.getClass());
        if(classData instanceof SaveAbleInventory){
            return null;
        }else if(ConfigurationSerialization.class.isAssignableFrom(classData.getClass())){
            return null;
        } else if(classData instanceof PulseClass pulseClass){
            pulseClass.BeforeLoadConfig();
            var data = ReturnClassFields((Document) configData, pulseClass.getClass(), pulseClass);
            pulseClass.AfterLoadConfig();
            return data;
        } else if(classData instanceof SaveableHashmap saveableHashmap){
            saveableHashmap.DeSerialiseData(StorageType.MONGO, (Document) configData);
            return saveableHashmap;
        } else if(classData instanceof SaveableLinkedHashMap saveableLinkedHashMap){
            saveableLinkedHashMap.DeSerialiseData(StorageType.MONGO, (Document) configData);
            return saveableLinkedHashMap;
        } else if(classData instanceof SaveableArrayList saveableArrayList) {
            saveableArrayList.DeSerialiseData(StorageType.MONGO, (ArrayList<Object>) configData);
            return saveableArrayList;
        } else if(classData instanceof CustomVariable customVariable){
            if(((Document) configData).isEmpty()) return customVariable;
            var hashMap = new HashMap<Object, Object>();
            var document = (Document) configData;
            for(var x : document.keySet()) hashMap.put(x, document.get(x));
            customVariable.BeforeLoad();
            customVariable.DeSerializeData(hashMap);
            customVariable.AfterLoad();
            return customVariable;
        } else if(classData instanceof Date){
            return SerializerHelpers.SimpleDateFormat.parse(configData.toString());
        } else if(variableTest != null){
            return variableTest.DeSerializeData(configData);
        }
        return configData;
    }
}
