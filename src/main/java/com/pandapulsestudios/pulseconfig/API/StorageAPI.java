package com.pandapulsestudios.pulseconfig.API;

import com.pandapulsestudios.pulseconfig.Interface.PulseBinary;
import com.pandapulsestudios.pulseconfig.Interface.PulseJSON;
import com.pandapulsestudios.pulseconfig.Objects.BinaryFileObject;
import com.pandapulsestudios.pulseconfig.Objects.ConfigObject;
import com.pandapulsestudios.pulseconfig.Interface.PulseConfig;
import com.pandapulsestudios.pulseconfig.Objects.JsonObject;
import com.pandapulsestudios.pulseconfig.Serializer.*;
import com.pandapulsestudios.pulseconfig.Interface.PulseMongo;
import com.pandapulsestudios.pulseconfig.Objects.MongoConnection;
import com.pandapulsestudios.pulseconfig.Enum.StorageType;
import com.pandapulsestudios.pulsecore.Chat.ChatAPI;
import com.pandapulsestudios.pulsecore.Java.JavaAPI;
import com.pandapulsestudios.pulsecore.Java.PulseAutoRegister;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Arrays;

public class StorageAPI {
    public static void RegisterStatic(JavaPlugin javaPlugin, boolean debugLoad){
        try {RegisterStaticRaw(javaPlugin, debugLoad);}
        catch (Exception e) {throw new RuntimeException(e);}
    }

    public static void RegisterStaticRaw(JavaPlugin javaPlugin, boolean debugLoad) throws Exception {
        for(var autoRegisterClass : JavaAPI.ReturnAllAutoRegisterClasses(javaPlugin)){
            if(PulseConfig.class.isAssignableFrom(autoRegisterClass)){
                LoadRaw(autoRegisterClass.getConstructor().newInstance(), debugLoad, StorageType.CONFIG);
                ChatAPI.chatBuilder().SendMessage(String.format("&8Registered Config Static: %s", autoRegisterClass.getSimpleName()));
            }
            if(PulseMongo.class.isAssignableFrom(autoRegisterClass)){
                LoadRaw(autoRegisterClass.getConstructor().newInstance(), debugLoad, StorageType.MONGO);
                ChatAPI.chatBuilder().SendMessage(String.format("&8Registered Mongo Static: %s", autoRegisterClass.getSimpleName()));
            }
            if(PulseJSON.class.isAssignableFrom(autoRegisterClass)){
                LoadRaw(autoRegisterClass.getConstructor().newInstance(), debugLoad, StorageType.JSON);
                ChatAPI.chatBuilder().SendMessage(String.format("&8Registered Json Static: %s", autoRegisterClass.getSimpleName()));
            }
            if(PulseBinary.class.isAssignableFrom(autoRegisterClass)){
                LoadRaw(autoRegisterClass.getConstructor().newInstance(), debugLoad, StorageType.BINARY);
                ChatAPI.chatBuilder().SendMessage(String.format("&8Registered Binary Static: %s", autoRegisterClass.getSimpleName()));
            }
        }
        ChatAPI.chatBuilder().SendMessage("&8Total Register STATIC_CONFIGS: " + com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_CONFIGS.size());
        ChatAPI.chatBuilder().SendMessage("&8Total Register STATIC_JSON: " + com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_JSON.size());
        ChatAPI.chatBuilder().SendMessage("&8Total Register STATIC_MONGO: " + com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_MONGO.size());
        ChatAPI.chatBuilder().SendMessage("&8Total Register STATIC_BINARY: " + com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_BINARY.size());
    }

    public static Object ReturnStatic(Class<?> autoRegisterClass, StorageType storageType, boolean createNewInstance){
        try {return ReturnStaticRaw(autoRegisterClass, storageType, createNewInstance);}
        catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {throw new RuntimeException(e);}
    }

    public static Object ReturnStaticRaw(Class<?> autoRegisterClass, StorageType storageType, boolean createNewInstance) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if(storageType == StorageType.CONFIG) return com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_CONFIGS.getOrDefault(autoRegisterClass, null);
        if(storageType == StorageType.MONGO) return com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_MONGO.getOrDefault(autoRegisterClass, null);
        if(storageType == StorageType.JSON) return com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_JSON.getOrDefault(autoRegisterClass, null);
        if(storageType == StorageType.BINARY) return com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_BINARY.getOrDefault(autoRegisterClass, null);
        return createNewInstance ? autoRegisterClass.getConstructor().newInstance() : null;
    }

    public static void ReloadStatic(Class<?> autoRegisterClass, StorageType storageType, boolean createNewInstance, boolean debugLoad){
        try {ReloadStaticRaw(autoRegisterClass, storageType, createNewInstance, debugLoad);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public static void ReloadStaticRaw(Class<?> autoRegisterClass, StorageType storageType, boolean createNewInstance, boolean debugLoad) throws Exception {
        var storageObject = ReturnStaticRaw(autoRegisterClass, storageType, createNewInstance);
        StorageAPI.LoadRaw(storageObject, debugLoad, storageType);
    }

    public static void ResetStatic(Class<?> autoRegisterClass, StorageType storageType, boolean createNewInstance, boolean debugLoad){
        try {ResetStaticRaw(autoRegisterClass, storageType, createNewInstance, debugLoad);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    public static void ResetStaticRaw(Class<?> autoRegisterClass, StorageType storageType, boolean createNewInstance, boolean debugLoad) throws Exception {
        var storageObject = ReturnStaticRaw(autoRegisterClass, storageType, createNewInstance);
        DeleteRaw(storageObject, debugLoad);
        LoadRaw(autoRegisterClass.getConstructor().newInstance(), debugLoad, storageType);
    }

    public static void Display(Object storageObject, StorageType... storageTypes){
        try {DisplayRaw(storageObject, storageTypes);}
        catch (Exception e) {throw new RuntimeException(e);}
    }

    public static void DisplayRaw(Object storageObject, StorageType... storageTypes) throws Exception {
        if(PulseConfig.class.isAssignableFrom(storageObject.getClass()) && Arrays.asList(storageTypes).contains(StorageType.CONFIG)){
            ChatAPI.chatBuilder().SendMessage(ConfigConsole.ConsoleOutput((PulseConfig) storageObject));
        }

        if(PulseMongo.class.isAssignableFrom(storageObject.getClass()) && Arrays.asList(storageTypes).contains(StorageType.MONGO)){
            ChatAPI.chatBuilder().SendMessage(MongoConsole.ConsoleOutput((PulseMongo) storageObject));
        }

        if(PulseBinary.class.isAssignableFrom(storageObject.getClass()) && Arrays.asList(storageTypes).contains(StorageType.BINARY)){
            ChatAPI.chatBuilder().SendMessage(BinaryConsole.ConsoleOutput((PulseBinary) storageObject));
        }

        if(PulseJSON.class.isAssignableFrom(storageObject.getClass()) && Arrays.asList(storageTypes).contains(StorageType.JSON)){
            ChatAPI.chatBuilder().SendMessage(JSONConsole.ConsoleOutput((PulseJSON) storageObject));
        }
    }

    public static void Delete(Object storageObject, boolean debugSave){
        try {DeleteRaw(storageObject, debugSave);}
        catch (IOException e) {throw new RuntimeException(e);}
    }

    public static void DeleteRaw(Object storageObject, boolean debugSave) throws IOException {
        if(PulseConfig.class.isAssignableFrom(storageObject.getClass())){
            var pulseConfig = (PulseConfig) storageObject;
            var configObject = new ConfigObject(ConfigAPI.ReturnConfigPath(pulseConfig), pulseConfig.documentID(), debugSave);
            configObject.DeleteConfig();
            com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_CONFIGS.remove(storageObject.getClass());
        }
        if(PulseMongo.class.isAssignableFrom(storageObject.getClass())){
            var pulseMongo = (PulseMongo) storageObject;
            var mongoConnection = com.pandapulsestudios.pulseconfig.PulseConfig.mongoConnections.getOrDefault(pulseMongo.databaseName(), new MongoConnection(pulseMongo.mongoIP(), pulseMongo.databaseName(), debugSave));
            mongoConnection.Delete(pulseMongo.collectionName(), null, pulseMongo.documentID());
            com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_MONGO.remove(storageObject.getClass());
        }
        if(PulseBinary.class.isAssignableFrom(storageObject.getClass())){
            var pulseBinary = (PulseBinary) storageObject;
            var binaryFileObject = new BinaryFileObject(pulseBinary, debugSave);
            binaryFileObject.Delete(pulseBinary);
            com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_BINARY.remove(storageObject.getClass());
        }
        if(PulseJSON.class.isAssignableFrom(storageObject.getClass())) {
            var pulseJSON = (PulseJSON) storageObject;
            var jsonObject = new JsonObject(pulseJSON, debugSave);
            jsonObject.DeleteConfig(pulseJSON);
            com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_JSON.remove(storageObject.getClass());
        }
        ChatAPI.chatBuilder().SendMessage("&8Total Register STATIC_CONFIGS: " + com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_CONFIGS.size());
        ChatAPI.chatBuilder().SendMessage("&8Total Register STATIC_JSON: " + com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_JSON.size());
        ChatAPI.chatBuilder().SendMessage("&8Total Register STATIC_MONGO: " + com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_MONGO.size());
        ChatAPI.chatBuilder().SendMessage("&8Total Register STATIC_BINARY: " + com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_BINARY.size());
    }

    public static void Load(Object storageObject, boolean debugSave, StorageType... storageTypes){
        try {LoadRaw(storageObject, debugSave, storageTypes);}
        catch (Exception e) {throw new RuntimeException(e);}
    }

    public static void LoadRaw(Object storageObject, boolean debugSave, StorageType... storageTypes) throws Exception {
        if(PulseConfig.class.isAssignableFrom(storageObject.getClass()) && Arrays.asList(storageTypes).contains(StorageType.CONFIG)){
            var pulseConfig = (PulseConfig) storageObject;
            var configObject = new ConfigObject(ConfigAPI.ReturnConfigPath(pulseConfig), pulseConfig.documentID(), debugSave);
            if(configObject.FirstSave()){
                pulseConfig.FirstLoadConfig();
                SaveRaw(pulseConfig, debugSave);
            }else{
                ConfigDeSerializer.LoadConfig(pulseConfig, configObject);
            }
            com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_CONFIGS.put(storageObject.getClass(), pulseConfig);
        }

        if(PulseMongo.class.isAssignableFrom(storageObject.getClass()) && Arrays.asList(storageTypes).contains(StorageType.MONGO)){
            var pulseMongo = (PulseMongo) storageObject;
            var mongoConnection = com.pandapulsestudios.pulseconfig.PulseConfig.mongoConnections.getOrDefault(pulseMongo.databaseName(), new MongoConnection(pulseMongo.mongoIP(), pulseMongo.databaseName(), debugSave));
            if(mongoConnection.CountDocuments(pulseMongo.collectionName(), null, pulseMongo.documentID()) == 0){
                pulseMongo.FirstLoadMongo();
                SaveRaw(pulseMongo, debugSave);
            }else{
                MongoDeSerializer.LoadMongo(pulseMongo, mongoConnection);
            }
            com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_MONGO.put(storageObject.getClass(), pulseMongo);
        }

        if(PulseBinary.class.isAssignableFrom(storageObject.getClass()) && Arrays.asList(storageTypes).contains(StorageType.BINARY)){
            var pulseBinary = (PulseBinary) storageObject;
            var binaryFileObject = new BinaryFileObject(pulseBinary, debugSave);
            if(binaryFileObject.FirstSave()){
                pulseBinary.FirstLoadBinary();
                SaveRaw(pulseBinary, debugSave);
            }else{
                BinaryDeSerializer.LoadBinary(pulseBinary, binaryFileObject);
            }
            com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_BINARY.put(storageObject.getClass(), pulseBinary);
        }

        if(PulseJSON.class.isAssignableFrom(storageObject.getClass()) && Arrays.asList(storageTypes).contains(StorageType.JSON)){
            var pulseJSON = (PulseJSON) storageObject;
            var jsonObject = new JsonObject(pulseJSON, debugSave);
            if(jsonObject.FirstSave()){
                pulseJSON.FirstLoadJSON();
                SaveRaw(pulseJSON, debugSave);
            }else{
                JSONDeSerializer.LoadJSON(pulseJSON, jsonObject);
            }
            com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_JSON.put(storageObject.getClass(), pulseJSON);
        }
    }

    public static void Save(Object storageObject, boolean debugSave) {
        try {SaveRaw(storageObject, debugSave);}
        catch (Exception e) {throw new RuntimeException(e);}
    }

    public static void SaveRaw(Object storageObject, boolean debugSave) throws Exception {
        if(PulseConfig.class.isAssignableFrom(storageObject.getClass())){
            var pulseConfig = (PulseConfig) storageObject;
            var configObject = new ConfigObject(ConfigAPI.ReturnConfigPath(pulseConfig), pulseConfig.documentID(), debugSave);
            if(configObject.FirstSave()) pulseConfig.FirstLoadConfig();
            ConfigSerializer.SaveConfig(pulseConfig, configObject);
            com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_CONFIGS.put(storageObject.getClass(), pulseConfig);
        }
        if(PulseMongo.class.isAssignableFrom(storageObject.getClass())){
            var pulseMongo = (PulseMongo) storageObject;
            var mongoConnection = com.pandapulsestudios.pulseconfig.PulseConfig.mongoConnections.getOrDefault(pulseMongo.databaseName(), new MongoConnection(pulseMongo.mongoIP(), pulseMongo.databaseName(), debugSave));
            if(mongoConnection.CountDocuments(pulseMongo.collectionName(), null, pulseMongo.documentID()) == 0) pulseMongo.FirstLoadMongo();
            MongoSerializer.SaveMongo(pulseMongo, mongoConnection);
            com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_MONGO.put(storageObject.getClass(), pulseMongo);
        }
        if(PulseBinary.class.isAssignableFrom(storageObject.getClass())){
            var pulseBinary = (PulseBinary) storageObject;
            var binaryFileObject = new BinaryFileObject(pulseBinary, debugSave);
            if(binaryFileObject.FirstSave()) pulseBinary.FirstLoadBinary();
            BinarySerializer.SaveBinary(pulseBinary, binaryFileObject);
            com.pandapulsestudios.pulseconfig.PulseConfig.STATIC_BINARY.put(storageObject.getClass(), pulseBinary);
        }
        if(PulseJSON.class.isAssignableFrom(storageObject.getClass())){
            var pulseJSON = (PulseJSON) storageObject;
            var jsonObject = new JsonObject(pulseJSON, debugSave);
            if(jsonObject.FirstSave()) pulseJSON.FirstLoadJSON();
            JSONSerializer.SaveJSON(pulseJSON, jsonObject);
        }
    }
}
