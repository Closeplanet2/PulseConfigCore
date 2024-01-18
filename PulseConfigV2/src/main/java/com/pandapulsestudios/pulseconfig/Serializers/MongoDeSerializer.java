package com.pandapulsestudios.pulseconfig.Serializers;

import com.pandapulsestudios.pulseconfig.APIS.MongoAPI;
import com.pandapulsestudios.pulseconfig.Interface.PandaClass;
import com.pandapulsestudios.pulseconfig.Interface.PandaMongo;
import com.pandapulsestudios.pulseconfig.Interface.SaveName;
import com.pandapulsestudios.pulseconfig.Object.MongoObject;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import com.pandapulsestudios.pulsecore.Data.Interface.CustomVariable;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MongoDeSerializer {
    public static void LoadMongo(PandaMongo pandaMongo, MongoObject mongoObject) throws Exception{
        var storedDocument = mongoObject.Find(pandaMongo.getClass().getSimpleName(), pandaMongo.documentID());
        var dataFields = SerializerHelpers.ReturnAllFields(null, pandaMongo, null);
        for (var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            field.set(pandaMongo, LoadMongo(dataFields.get(field), storedDocument.get(fieldName)));
        }
    }

    private static Object LoadMongo(PandaClass pandaClass, Document storedDocument) throws Exception{
        var dataFields = SerializerHelpers.ReturnAllFields(null, null, pandaClass);
        for (var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            field.set(pandaClass, LoadMongo(dataFields.get(field), storedDocument.get(fieldName)));
        }
        return pandaClass;
    }

    private static HashMap<Object, Object> LoadMongo(Document storedData) throws Exception{
        var returnData = new HashMap<Object, Object>();
        for (var datakey : storedData.keySet()) returnData.put(LoadMongo(datakey, datakey), LoadMongo(storedData.get(datakey), storedData.get(datakey)));
        return returnData;
    }

    private static ArrayList<Object> LoadMongo(ArrayList<Object> storedData) throws Exception {
        var deSerialisedData = new ArrayList<>();
        for(var data: storedData) deSerialisedData.add(LoadMongo(data, data));
        return deSerialisedData;
    }

    private static Object LoadMongo(Object classObject, Object configObject) throws Exception {
        if(classObject instanceof PandaClass){
            return LoadMongo((PandaClass) classObject, (Document) configObject);
        } else if(classObject instanceof HashMap) {
            return LoadMongo((Document) configObject);
        } else if(classObject instanceof ArrayList) {
            return LoadMongo((ArrayList<Object>) configObject);
        } else if(classObject instanceof CustomVariable) {
            var customVar = (CustomVariable) classObject.getClass().getDeclaredConstructor().newInstance();
            return customVar.DeSerializeData(configObject.toString());
        } else if(ConfigurationSerializable.class.isAssignableFrom(classObject.getClass())) {
            var hashMap = new HashMap<String, Object>();
            for (var key : ((Document) configObject).keySet()) hashMap.put(key, ((Document) configObject).get(key));
            return ConfigurationSerialization.deserializeObject(hashMap, classObject.getClass().asSubclass(ConfigurationSerializable.class));
        } else if(classObject instanceof Date) {
            return SerializerHelpers.SimpleDateFormat.parse(configObject.toString());
        } else{
            var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(classObject.getClass());
            return variableTest == null ? null : variableTest.DeSerializeData(configObject);
        }
    }
}
