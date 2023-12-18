package com.pandapulsestudios.pulseconfig.Serializers;

import com.pandapulsestudios.pulseconfig.Interface.PandaClass;
import com.pandapulsestudios.pulseconfig.Interface.PandaMongo;
import com.pandapulsestudios.pulseconfig.Interface.SaveName;
import com.pandapulsestudios.pulseconfig.Object.MongoObject;
import com.pandapulsestudios.pulsevariable.API.VariableAPI;
import com.pandapulsestudios.pulsevariable.Interface.CustomVariable;
import org.bson.Document;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MongoSerializer {
    public static void SaveMongo(PandaMongo pandaMongo, MongoObject mongoObject) throws Exception {
        var convertedDocument = mongoObject.DefaultDocument(pandaMongo);
        var dataFields = SerializerHelpers.ReturnAllFields(null, pandaMongo, null);
        for (var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            convertedDocument.put(fieldName, SaveMongo(dataFields.get(field)));
        }
        mongoObject.InsertOrReplace(pandaMongo.getClass().getSimpleName(), SerializerHelpers.MongoID, pandaMongo.documentID(), convertedDocument);
    }

    private static HashMap<String, Object> SaveMongo(PandaClass pandaClass) throws Exception {
        var storedData = new HashMap<String, Object>();
        var dataFields = SerializerHelpers.ReturnAllFields(null, null, pandaClass);
        for (var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            storedData.put(fieldName, SaveMongo(dataFields.get(field)));
        }
        return storedData;
    }

    public static HashMap<Object, Object> SaveMongo(HashMap<Object, Object> currentData) throws Exception {
        var returnData = new HashMap<>();
        for (var dataKey : currentData.keySet()) returnData.put(SaveMongo(dataKey), SaveMongo(currentData.get(dataKey)));
        return returnData;
    }

    public static ArrayList<Object> SaveMongo(ArrayList<Object> currentData) throws Exception {
        var returnData = new ArrayList<>();
        for (var datakey : currentData)  returnData.add(SaveMongo(datakey));
        return returnData;
    }

    private static Object SaveMongo(Object data) throws Exception {
        if(data instanceof PandaClass) return SaveMongo((PandaClass) data);
        else if(data instanceof HashMap) return SaveMongo((HashMap<Object, Object>) data);
        else if(data instanceof ArrayList) return SaveMongo((ArrayList<Object>) data);
        else if(data instanceof CustomVariable) return ((CustomVariable) data).SerializeData();
        else if(ConfigurationSerializable.class.isAssignableFrom(data.getClass())) return ((ConfigurationSerializable) data).serialize();
        else if(data instanceof Date) return SerializerHelpers.SimpleDateFormat.format((Date) data);
        else{
            var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(data.getClass(), false);
            return variableTest == null ? null : variableTest.SerializeData(data);
        }
    }
}
