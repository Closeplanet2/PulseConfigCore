package com.pandapulsestudios.pulseconfig.Serializer;

import com.pandapulsestudios.pulseconfig.Interface.PulseMongo;
import com.pandapulsestudios.pulseconfig.Objects.*;
import com.pandapulsestudios.pulseconfig.Interface.CustomVariable;
import com.pandapulsestudios.pulseconfig.Interface.PulseClass;
import com.pandapulsestudios.pulseconfig.Interface.SaveName;
import com.pandapulsestudios.pulseconfig.Enum.StorageType;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.json.simple.JSONObject;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;

public class MongoDeSerializer {
    public static void LoadMongo(PulseMongo pulseMongo, MongoConnection mongoConnection) throws Exception {
        pulseMongo.BeforeLoadMongo();
        var storedData = mongoConnection.GetOne(pulseMongo.collectionName(), null, pulseMongo.documentID());
        var data = ReturnClassFieldsMap(storedData, pulseMongo.getClass(), pulseMongo);
        pulseMongo.AfterLoadMongo();
    }

    public static Object ReturnClassFieldsMap(Map configData, Class<?> parentClass, Object object) throws Exception {
        var dataFields = SerializerHelpers.ReturnALlFields(parentClass, object);
        for(var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            if(!configData.containsKey(fieldName)){
                field.set(object, null);
            }
            else{
                field.set(object, LoadMongoSingle(dataFields.get(field).getClass(), dataFields.get(field), configData.get(fieldName)));
            }
        }
        return object;
    }

    public static Object LoadMongoSingle(Class<?> classDataType, Object classData, Object configData) throws Exception {
        if(classData == null || configData == null){ return null; }
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + classData.getClass().getSimpleName() + ":" + classDataType.getSimpleName());

        var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(classDataType);
        if(SaveAbleInventory.class.isAssignableFrom(classDataType)){
            return null;
        }else if(ConfigurationSerialization.class.isAssignableFrom(classDataType)){
            return null;
        } else if(PulseClass.class.isAssignableFrom(classDataType)){
            var pulseClass = (PulseClass) classData;
            pulseClass.BeforeLoadConfig();
            var data = ReturnClassFieldsMap((Document) configData, pulseClass.getClass(), pulseClass);
            pulseClass.AfterLoadConfig();
            return data;
        } else if(SaveableHashmap.class.isAssignableFrom(classDataType)){
            var saveableHashmap = (SaveableHashmap) classData;
            saveableHashmap.DeSerialiseData(StorageType.MONGO, (Document) configData);
            return saveableHashmap;
        } else if(SaveableLinkedHashMap.class.isAssignableFrom(classDataType)){
            var saveableLinkedHashMap = (SaveableLinkedHashMap) classData;
            saveableLinkedHashMap.DeSerialiseData(StorageType.MONGO, (Document) configData);
            return saveableLinkedHashMap;
        } else if(SaveableArrayList.class.isAssignableFrom(classDataType)) {
            var saveableArrayList = (SaveableArrayList) classData;
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "SaveableArrayList");
            saveableArrayList.DeSerialiseData(StorageType.MONGO, (List<Object>) configData);
            return saveableArrayList;
        } else if(CustomVariable.class.isAssignableFrom(classDataType)){
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "CustomVariable");
            var customVariable = (CustomVariable) classDataType.getConstructor().newInstance();
            var hashMap = new HashMap<Object, Object>();
            var document = (Document) configData;
            for(var x : document.keySet()) hashMap.put(x, document.get(x));
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + hashMap.toString());
            customVariable.BeforeLoad();
            customVariable.DeSerializeData(hashMap);
            customVariable.AfterLoad();
            return customVariable;
        } else if(Date.class.isAssignableFrom(classDataType)){
            return SerializerHelpers.SimpleDateFormat.parse(configData.toString());
        } else if(variableTest != null){
            return variableTest.DeSerializeData(configData);
        }
        return configData;
    }
}
