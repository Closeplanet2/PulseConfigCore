package com.pandapulsestudios.pulseconfig.Serializer;

import com.pandapulsestudios.pulseconfig.Enum.StorageType;
import com.pandapulsestudios.pulseconfig.Interface.*;
import com.pandapulsestudios.pulseconfig.Objects.*;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import org.bson.Document;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class JSONDeSerializer {
    public static void LoadJSON(PulseJSON pulseJson, JsonObject jsonObject) throws Exception {
        pulseJson.BeforeLoadJSON();
        var storedData = jsonObject.Read(pulseJson);
        var data = ReturnClassFields(storedData, pulseJson.getClass(), pulseJson);
        pulseJson.AfterLoadJSON();
    }

    public static Object ReturnClassFields(HashMap<Object, Object> configData, Class<?> parentClass, Object object) throws IllegalAccessException, ParseException {
        var dataFields = SerializerHelpers.ReturnALlFields(parentClass, object);
        for(var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            if(!configData.containsKey(fieldName)) field.set(object, MongoSerializer.SaveMongoSingle(dataFields.get(field)));
            else {
                var deSerialisedData = LoadJSONSingle(dataFields.get(field), configData.get(fieldName));
                if(deSerialisedData != null){
                    try{field.set(object, deSerialisedData);}
                    catch(Exception ignored) {field.set(object, dataFields.get(field));}
                }
            }
        }
        return object;
    }

    public static Object LoadJSONSingle(Object classData, Object configData) throws IllegalAccessException, ParseException {
        if(classData == null || configData == null) return null;
        var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(classData.getClass());
        if(classData instanceof SaveAbleInventory){
            return null;
        }else if(ConfigurationSerialization.class.isAssignableFrom(classData.getClass())){
            return null;
        } else if(classData instanceof PulseClass pulseClass){
            pulseClass.BeforeLoadConfig();
            var data = ReturnClassFields((HashMap<Object, Object>) configData, pulseClass.getClass(), pulseClass);
            pulseClass.AfterLoadConfig();
            return data;
        } else if(classData instanceof SaveableHashmap saveableHashmap){
            saveableHashmap.DeSerialiseData(StorageType.MONGO, (HashMap<Object, Object>) configData);
            return saveableHashmap;
        } else if(classData instanceof SaveableLinkedHashMap saveableLinkedHashMap){
            saveableLinkedHashMap.DeSerialiseData(StorageType.MONGO, (HashMap<Object, Object>) configData);
            return saveableLinkedHashMap;
        } else if(classData instanceof SaveableArrayList saveableArrayList) {
            saveableArrayList.DeSerialiseData(StorageType.MONGO, (ArrayList<Object>) configData);
            return saveableArrayList;
        } else if(classData instanceof CustomVariable customVariable){
            var hashMap = (HashMap<Object, Object>) configData;
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
