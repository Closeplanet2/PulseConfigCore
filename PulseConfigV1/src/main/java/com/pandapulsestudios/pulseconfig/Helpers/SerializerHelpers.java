package com.pandapulsestudios.pulseconfig.Helpers;

import com.pandapulsestudios.pulseconfig.Interface.*;
import com.pandapulsestudios.pulseconfig.Objects.MongoConnection;
import com.pandapulsestudios.pulseconfig.Objects.ServerConfig;
import com.pandapulsestudios.pulsecore.Console.ConsoleAPI;
import com.pandapulsestudios.pulsecore.StaticTests.StaticDate;
import com.pandapulsestudios.pulsecore.StaticTests.StaticHashMap;
import com.pandapulsestudios.pulsecore.StaticTests.StaticList;
import com.pandapulsestudios.pulsevariable.INTERFACE.CustomVariable;
import com.pandapulsestudios.pulsevariable.PulseVariable;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;

public class SerializerHelpers {
    public static LinkedHashMap<Field, Object> ReturnAllFields(PandaConfig pandaConfig, PandaMongo pandaMongo, PandaClass pandaClass) throws IllegalAccessException {
        var data = new LinkedHashMap<Field, Object>();

        Field[] fields;
        Object clazz;
        if (pandaConfig != null) {
            fields = pandaConfig.getClass().getDeclaredFields();
            clazz = pandaConfig;
        } else if (pandaMongo != null) {
            fields = pandaMongo.getClass().getDeclaredFields();
            clazz = pandaMongo;
        } else {
            fields = pandaClass.getClass().getDeclaredFields();
            clazz = pandaClass;
        }

        for (var field : fields) {
            Object variable;
            try {
                variable = field.get(clazz);
            } catch (Exception e) {
                continue;
            }
            if (variable == null) continue;
            if (field.isAnnotationPresent(IgnoreSave.class)) continue;
            data.put(field, variable);
        }

        return data;
    }

    public static String ReturnFieldPath(Field field, String currentPath) {
        var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
        return currentPath.equals("") ? fieldName : currentPath + "." + fieldName;
    }

    public static void TryAndSetConfigHeader(PandaConfig pandaConfig, ServerConfig serverConfig) {
        if (pandaConfig == null) return;
        if (!pandaConfig.getClass().isAnnotationPresent(ConfigHeader.class)) return;
        serverConfig.SetHeader(pandaConfig.getClass().getAnnotation(ConfigHeader.class).value());
    }

    public static HashMap<Object, Object> SerialiseHashMap(HashMap<Object, Object> currentData, ServerConfig serverConfig, MongoConnection mongoConnection, String currentPath, Class<?> classType) throws Exception {
        var returnData = new HashMap<>();
        for (var datakey : currentData.keySet()) {
            var data = currentData.get(datakey);
            var serialiseData = serverConfig != null ? SerialiseSingleData(data, serverConfig, currentPath, classType) : SerialiseSingleData(data, mongoConnection, currentPath, classType);
            if (serialiseData != null) returnData.put(datakey, serialiseData);
        }
        return returnData;
    }

    public static ArrayList<Object> SerialiseArrayList(ArrayList<Object> currentData, ServerConfig serverConfig, MongoConnection mongoConnection, String currentPath, Class<?> classType) throws Exception {
        var returnData = new ArrayList<>();
        for (var data : currentData) {
            var serialiseData = serverConfig != null ? SerialiseSingleData(data, serverConfig, currentPath, classType) : SerialiseSingleData(data, mongoConnection, currentPath, classType);
            if (serialiseData == null) continue;
            returnData.add(serialiseData);
        }
        return returnData;
    }

    public static Object SerialiseSingleData(Object data, ServerConfig serverConfig, String currentPath, Class<?> classType) throws Exception {
        if (data instanceof HashMap) {
            serverConfig.SetData(currentPath, null);
            return SerialiseHashMap((HashMap<Object, Object>) data, serverConfig, null, currentPath, classType);
        } else if (data instanceof ArrayList) {
            serverConfig.SetData(currentPath, null);
            return SerialiseArrayList((ArrayList<Object>) data, serverConfig, null, currentPath, classType);
        } else if (data instanceof PandaClass) {
            var pandaClass = (PandaClass) data;
            Serializer.SAVE(null, pandaClass, serverConfig, currentPath + "." + pandaClass.CLASS_ID());
            return null;
        }
        return SerialiseSingleData(data);
    }

    public static Object SerialiseSingleData(Object data) {
        var variableTest = PulseVariable.VAR_TESTS.get(data.getClass());
        if (data instanceof CustomVariable) {
            return ((CustomVariable) data).Return();
        } else if (ConfigurationSerializable.class.isAssignableFrom(data.getClass())) {
            return data;
        } else if (data instanceof Date) {
            return StaticDate.ToString(null, data);
        } else if (variableTest != null) {
            return variableTest.CONVERT_FOR_CONFIG(data);
        }
        return null;
    }

    public static Object SerialiseSingleData(Object data, MongoConnection mongoConnection, String currentPath, Class<?> classType) throws Exception {
        var variableTest = PulseVariable.VAR_TESTS.get(data.getClass());
        if (data instanceof HashMap) {
            return SerialiseHashMap((HashMap<Object, Object>) data, null, mongoConnection, currentPath, classType);
        } else if (data instanceof ArrayList) {
            return SerialiseArrayList((ArrayList<Object>) data, null, mongoConnection, currentPath, classType);
        } else if (data instanceof PandaClass) {
            return Serializer.SAVE(null, (PandaClass) data, mongoConnection, currentPath);
        } else if (data instanceof CustomVariable) {
            return ((CustomVariable) data).Return();
        } else if (ConfigurationSerializable.class.isAssignableFrom(data.getClass())) {
            return ((ConfigurationSerializable) data).serialize();
        } else if (data instanceof Date) {
            return StaticDate.ToString(null, data);
        } else if (variableTest != null) {
            return variableTest.CONVERT_FOR_CONFIG(data);
        }
        return null;
    }

    public static Object DeSerialiseHashMap(Field field, HashMap<Object, Object> currentData, ServerConfig serverConfig, String currentPath, boolean isPandaClass) throws Exception {
        var returnData = new HashMap<>();
        if (!isPandaClass) {
            for (var datakey : currentData.keySet()) {
                var data = currentData.get(datakey);
                returnData.put(datakey, DeSerialiseSingleData(field, data.getClass(), data, serverConfig, currentPath));
            }
        } else {
            for (var s : serverConfig.GetConfigurationSection(currentPath).getKeys(false)) {
                var newPath = currentPath + "." + s;
                var returnType = StaticHashMap.ReturnHashMapRightType(field);
                var deserialiseKey = DeSerialiseSingleData(null, StaticList.ReturnArrayType(field), s, serverConfig, currentPath);
                var pandaClass = (PandaClass) returnType.getDeclaredConstructor().newInstance();
                returnData.put(deserialiseKey, Serializer.LOAD(null, pandaClass, serverConfig, newPath));
            }
        }
        return returnData;
    }

    public static Object DeSerialiseHashMap(Field field, HashMap<Object, Object> d1, Document d2) throws Exception {
        var returnData = new HashMap<>();
        if(d1 != null){
            for(var datakey : d1.keySet()) returnData.put(datakey, DeSerialiseSingleData(field, d1.get(datakey).getClass(), d1.get(datakey)));
        }else if(d2 != null){
            for(var datakey : d2.keySet()) returnData.put(datakey, DeSerialiseSingleData(field, d2.get(datakey).getClass(), d2.get(datakey)));
        }
        return returnData;
    }

    public static Object DeSerialiseArrayList(Field field, List<Object> currentData, Class<?> dataType, ServerConfig serverConfig, String currentPath) throws Exception {
        var returnData = new ArrayList<>();
        for (var data : currentData) {
            var deSerialiseSingleData = DeSerialiseSingleData(field, data.getClass(), data, serverConfig, currentPath);
            if (deSerialiseSingleData == null) continue;
            returnData.add(deSerialiseSingleData);
        }
        return returnData;
    }

    public static Object DeSerialiseArrayList(Field field, List<Object> currentData, Class<?> dataType) throws Exception {
        var returnData = new ArrayList<>();
        for (var data : currentData){
            var x = DeSerialiseSingleData(field, dataType, data);
            returnData.add(x);
        }
        return returnData;
    }

    public static Object DeSerialiseSingleData(Field field, Class<?> dataType, Object data, ServerConfig serverConfig, String currentPath) throws Exception {
        var variableTest = PulseVariable.VAR_TESTS.get(dataType);
        if (dataType == HashMap.class) {
            if (field == null) return new HashMap<>();
            return DeSerialiseHashMap(field, (HashMap<Object, Object>) data, serverConfig, currentPath, serverConfig.IsConfigurationSection(currentPath));
        } else if (dataType == ArrayList.class) {
            if (field == null) return new ArrayList<>();
            return DeSerialiseArrayList(field, (ArrayList<Object>) data, dataType, serverConfig, currentPath);
        } else if (PandaClass.class.isAssignableFrom(dataType)) {
            return Serializer.LOAD(null, (PandaClass) data, serverConfig, currentPath);
        } else if (CustomVariable.class.isAssignableFrom(dataType)) {
            var customVar = (CustomVariable) dataType.getDeclaredConstructor().newInstance();
            return customVar.Set(data.toString());
        } else if (ConfigurationSerializable.class.isAssignableFrom(dataType)) {
            return data;
        } else if (dataType == Date.class) {
            return StaticDate.ToDate(null, data);
        } else if (variableTest != null) {
            var convertedData = variableTest.DE_CONVERT_FOR_CONFIG(data);
            if (convertedData != null) return convertedData;
        }
        return null;
    }

    public static Object DeSerialiseSingleData(Field field, Class<?> dataType, Object data) throws Exception {
        var variableTest = PulseVariable.VAR_TESTS.get(dataType);
        if (dataType == HashMap.class) {
            if (field == null) return new HashMap<>();
            return DeSerialiseHashMap(field, (HashMap<Object, Object>) data, null);
        } else if (dataType == ArrayList.class) {
            if (field == null) return new ArrayList<>();
            return DeSerialiseArrayList(field, (ArrayList<Object>) data, dataType);
        } else if (PandaClass.class.isAssignableFrom(dataType)) {
            return Serializer.LOAD(null, (PandaClass) dataType.getDeclaredConstructor().newInstance(), (Document) data);
        } else if (CustomVariable.class.isAssignableFrom(dataType)) {
            var customVar = (CustomVariable) dataType.getDeclaredConstructor().newInstance();
            return customVar.Set(data.toString());
        } else if (ConfigurationSerializable.class.isAssignableFrom(dataType)) {
            var hashMap = new HashMap<String, Object>();
            for (var key : ((Document) data).keySet()) hashMap.put(key, ((Document) data).get(key));
            return ConfigurationSerialization.deserializeObject(hashMap, dataType.asSubclass(ConfigurationSerializable.class));
        } else if (dataType == Date.class) {
            return StaticDate.ToDate(null, data);
        } else if (variableTest != null) {
            return variableTest.DE_CONVERT_FOR_CONFIG(data);
        }
        return null;
    }
}
