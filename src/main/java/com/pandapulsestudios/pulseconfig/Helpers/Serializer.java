package com.pandapulsestudios.pulseconfig.Helpers;

import com.pandapulsestudios.pulseconfig.Interface.PandaClass;
import com.pandapulsestudios.pulseconfig.Interface.PandaConfig;
import com.pandapulsestudios.pulseconfig.Interface.PandaMongo;
import com.pandapulsestudios.pulseconfig.Objects.MongoConnection;
import com.pandapulsestudios.pulseconfig.Objects.ServerConfig;
import com.pandapulsestudios.pulsecore.StaticTests.StaticList;
import com.sun.tools.jconsole.JConsoleContext;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class Serializer {
    public static void SAVE(PandaConfig pandaConfig, PandaClass pandaClass, ServerConfig serverConfig, String currentPath) throws Exception{
        SerializerHelpers.TryAndSetConfigHeader(pandaConfig, serverConfig);
        var fields = SerializerHelpers.ReturnAllFields(pandaConfig, null, pandaClass);
        for (var field : fields.keySet()) {
            var data = fields.get(field);
            if (data instanceof PandaClass) {
                SAVE(null, (PandaClass) data, serverConfig, SerializerHelpers.ReturnFieldPath(field, currentPath));
            }else{
                var classType = pandaConfig != null ? pandaConfig.CLASS_TYPE() : pandaClass.CLASS_TYPE();
                HANDLE_SAVE(data, serverConfig, SerializerHelpers.ReturnFieldPath(field, currentPath), classType);
            }
        }
    }

    public static Document SAVE(PandaMongo pandaMongo, PandaClass pandaClass, MongoConnection mongoConnection, String currentPath) throws Exception {
        Document convertedDocument;
        if(pandaMongo != null) convertedDocument = new Document("MongoID", pandaMongo.DOCUMENT_ID());
        else convertedDocument = new Document("SubID", pandaClass.CLASS_ID());

        var fields = SerializerHelpers.ReturnAllFields(null, pandaMongo, pandaClass);
        for (var field : fields.keySet()) {
            var data = fields.get(field);
            if(data instanceof PandaClass){
                convertedDocument.put(field.getName(), SAVE(null, (PandaClass) data, mongoConnection, currentPath));
            }else{
                var classType = pandaMongo != null ? pandaMongo.ReturnClassType() : pandaClass.CLASS_TYPE();
                var x = HANDLE_SAVE(data, mongoConnection, SerializerHelpers.ReturnFieldPath(field, currentPath), classType);
                convertedDocument.put(field.getName(), x);
            }
        }

        return convertedDocument;
    }

    private static void HANDLE_SAVE(Object data, ServerConfig serverConfig, String newPath, Class<?> classType) throws Exception {
        if (data.getClass() == HashMap.class) {
            serverConfig.SetData(newPath, null);
            var serialisedData = SerializerHelpers.SerialiseHashMap((HashMap<Object, Object>) data, serverConfig, null, newPath, classType);
            if(serialisedData.isEmpty()) return;
            var arrayList = new ArrayList<Object>();
            arrayList.add(serialisedData);
            serverConfig.SetData(newPath, arrayList);
        }else if (data.getClass() == ArrayList.class) {
            serverConfig.SetData(newPath, null);
            var serialisedData = SerializerHelpers.SerialiseArrayList((ArrayList<Object>) data, serverConfig, null, newPath, classType);
            if(serialisedData.isEmpty()) return;
            serverConfig.SetData(newPath, serialisedData);
        }else{
            var x = SerializerHelpers.SerialiseSingleData(data, serverConfig, newPath, classType);
            serverConfig.SetData(newPath, x);
        }
    }

    private static Object HANDLE_SAVE(Object data, MongoConnection mongoConnection, String newPath, Class<?> classType) throws Exception {
        if (data.getClass() == HashMap.class) {
            return SerializerHelpers.SerialiseHashMap((HashMap<Object, Object>) data, null, mongoConnection, newPath, classType);
        }else if (data.getClass() == ArrayList.class) {
            return SerializerHelpers.SerialiseArrayList((ArrayList<Object>) data, null, mongoConnection, newPath, classType);
        }else{
            return SerializerHelpers.SerialiseSingleData(data, mongoConnection, newPath, classType);
        }
    }

    public static Object LOAD(PandaConfig pandaConfig, PandaClass pandaClass, ServerConfig serverConfig, String currentPath) throws Exception {
        var fields = SerializerHelpers.ReturnAllFields(pandaConfig, null, pandaClass);
        for (var field : fields.keySet()) {
            var data = fields.get(field);
            if (data instanceof PandaClass) {
                var x = LOAD(null, (PandaClass) data, serverConfig, SerializerHelpers.ReturnFieldPath(field, currentPath));
                field.set(pandaConfig != null ? pandaConfig : pandaClass, x);
            }else{
                var returnData = HANDLE_Load(data, field, serverConfig, SerializerHelpers.ReturnFieldPath(field, currentPath));
                if (returnData == null) continue;
                field.set(pandaConfig != null ? pandaConfig : pandaClass, returnData);
            }
        }
        return pandaConfig != null ? pandaConfig : pandaClass;
    }

    private static Object HANDLE_Load(Object data, Field field, ServerConfig serverConfig, String currentPath) throws Exception {
        if (data.getClass() == HashMap.class) {
            return SerializerHelpers.DeSerialiseHashMap(field, serverConfig.GetHashMap(currentPath), serverConfig, currentPath, serverConfig.IsConfigurationSection(currentPath));
        }else if (data.getClass() == ArrayList.class) {
            var arrayType = StaticList.ReturnArrayType(field);
            return SerializerHelpers.DeSerialiseArrayList(field, serverConfig.GetList(currentPath), arrayType, serverConfig, currentPath);
        }else {
            return SerializerHelpers.DeSerialiseSingleData(field, data.getClass(), serverConfig.Get(currentPath), serverConfig, currentPath);
        }
    }

    public static Object LOAD(PandaMongo pandaMongo, PandaClass pandaClass, Document document) throws Exception {
        var fields = SerializerHelpers.ReturnAllFields(null, pandaMongo, pandaClass);
        for (var field : fields.keySet()) {
            var data = fields.get(field);
            if (data instanceof PandaClass) {
                var returnData = Serializer.LOAD(null, (PandaClass) data, (Document) document.get(field.getName()));
                field.set(pandaMongo != null ? pandaMongo : pandaClass, returnData);
            }else{
                var returnData = HANDLE_Load(data, field, document);
                if (returnData == null) continue;
                if (data.getClass() == Character.class) returnData = returnData.toString().charAt(0);
                if (data.getClass() == Float.class) {
                    try{
                        var sub = (double) returnData;
                        returnData = (float) sub;
                    }catch(Exception ignored){ }
                }
                if (data.getClass() == Long.class) {
                    try{
                        var sub = (int) returnData;
                        returnData = (long) sub;
                    }catch(Exception ignored){ }
                }
                field.set(pandaMongo != null ? pandaMongo : pandaClass, returnData);
            }
        }
        return pandaMongo != null ? pandaMongo : pandaClass;
    }

    private static Object HANDLE_Load(Object data, Field field, Document document) throws Exception {
        if (data.getClass() == HashMap.class) {
            return SerializerHelpers.DeSerialiseHashMap(field, null, (Document) document.get(field.getName()));
        }else if (data.getClass() == ArrayList.class) {
            var arrayType = StaticList.ReturnArrayType(field);
            return SerializerHelpers.DeSerialiseArrayList(field, (ArrayList<Object>) document.get(field.getName()), arrayType);
        }else {
            return SerializerHelpers.DeSerialiseSingleData(field, data.getClass(), document.get(field.getName()));
        }
    }
}
