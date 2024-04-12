package com.pandapulsestudios.pulseconfig.API;

import com.pandapulsestudios.pulseconfig.Objects.ConfigObject;
import com.pandapulsestudios.pulseconfig.Events.PulseConfigDeleteEvent;
import com.pandapulsestudios.pulseconfig.Events.PulseConfigLoadEvent;
import com.pandapulsestudios.pulseconfig.Events.PulseConfigSaveEvent;
import com.pandapulsestudios.pulseconfig.Interface.PulseConfig;
import com.pandapulsestudios.pulseconfig.Serializer.ConfigConsole;
import com.pandapulsestudios.pulseconfig.Serializer.ConfigDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializer.ConfigSerializer;
import com.pandapulsestudios.pulseconfig.Events.PulseMongoDeleteEvent;
import com.pandapulsestudios.pulseconfig.Events.PulseMongoLoadEvent;
import com.pandapulsestudios.pulseconfig.Events.PulseMongoSaveEvent;
import com.pandapulsestudios.pulseconfig.Interface.PulseMongo;
import com.pandapulsestudios.pulseconfig.Objects.MongoConnection;
import com.pandapulsestudios.pulseconfig.Serializer.MongoConsole;
import com.pandapulsestudios.pulseconfig.Serializer.MongoDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializer.MongoSerializer;
import com.pandapulsestudios.pulseconfig.Enum.StorageType;
import com.pandapulsestudios.pulsecore.Chat.ChatAPI;
import com.pandapulsestudios.pulsecore.Java.JavaAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Arrays;

public class StorageAPI {
    public static void RegisterStatic(JavaPlugin javaPlugin, boolean debugLoad){
        try { RegisterStaticRaw(javaPlugin, debugLoad);}
        catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ignored) { }
    }

    public static void RegisterStaticRaw(JavaPlugin javaPlugin, boolean debugLoad) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        for(var autoRegisterClass : JavaAPI.ReturnAllAutoRegisterClasses(javaPlugin)){
            if(PulseConfig.class.isAssignableFrom(autoRegisterClass)){
                var pulseConfig = (PulseConfig) ReturnStaticRaw(autoRegisterClass);
                Load(pulseConfig, debugLoad, StorageType.CONFIG);
                com.pandapulsestudios.pulseconfig.PulseConfig.staticConfigs.put(autoRegisterClass, pulseConfig);
                ChatAPI.chatBuilder().SendMessage(String.format("&8Registered Pulse Config: %s", pulseConfig.documentID()));
            }

            if(PulseMongo.class.isAssignableFrom(autoRegisterClass)){
                var pulseMongo = (PulseMongo) ReturnStaticRaw(autoRegisterClass);
                Load(pulseMongo, debugLoad, StorageType.MONGO);
                com.pandapulsestudios.pulseconfig.PulseConfig.staticConfigs.put(autoRegisterClass, pulseMongo);
                ChatAPI.chatBuilder().SendMessage(String.format("&8Registered Pulse Mongo: %s", pulseMongo.documentID()));
            }
        }
    }

    public static Object ReturnStatic(Class<?> classType){
        try { return ReturnStaticRaw(classType); }
        catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) { return null; }
    }

    public static Object ReturnStaticRaw(Class<?> classType) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return com.pandapulsestudios.pulseconfig.PulseConfig.staticConfigs.getOrDefault(classType, classType.getConstructor().newInstance());
    }

    public static Object ReloadStatic(Class<?> classType, boolean debug, StorageType... storageTypes){
        try { return ReloadStaticRaw(classType, debug, storageTypes); }
        catch (ParseException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) { return null; }
    }

    public static Object ReloadStaticRaw(Class<?> classType, boolean debug, StorageType... storageTypes) throws ParseException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        var document = ReturnStaticRaw(classType);
        if(document != null){
            LoadRaw(document, debug, storageTypes);
            com.pandapulsestudios.pulseconfig.PulseConfig.staticConfigs.put(classType, document);
        }
        return document;
    }

    public static Object ResetStatic(Class<?> classType, boolean debug, StorageType... storageTypes){
        try {return ResetStaticRaw(classType, debug, storageTypes);}
        catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException | ParseException e) {return null;}
    }

    public static Object ResetStaticRaw(Class<?> classType, boolean debug, StorageType... storageTypes) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ParseException {
        DeleteStaticConfigRaw(classType, debug);
        return ReloadStaticRaw(classType, debug, storageTypes);
    }

    public static void DeleteStaticConfigRaw(Class<?> classType, boolean debug) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        var document = ReturnStaticRaw(classType);
        if(document != null){
            Delete(document, debug);
            com.pandapulsestudios.pulseconfig.PulseConfig.staticConfigs.remove(classType);
        }
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
    }

    public static void Delete(Object storageObject, boolean debugSave){
        if(PulseConfig.class.isAssignableFrom(storageObject.getClass())) DeleteConfig((PulseConfig) storageObject, debugSave);
        if(PulseMongo.class.isAssignableFrom(storageObject.getClass())) DeleteMongo((PulseMongo) storageObject, debugSave);
    }

    public static void Save(Object storageObject, boolean debugSave) {
        try {SaveRaw(storageObject, debugSave);}
        catch (IllegalAccessException e) {throw new RuntimeException(e);}
    }

    public static void SaveRaw(Object storageObject, boolean debugSave) throws IllegalAccessException {
        if(PulseConfig.class.isAssignableFrom(storageObject.getClass())) SaveConfig((PulseConfig) storageObject, debugSave);
        if(PulseMongo.class.isAssignableFrom(storageObject.getClass())) SaveMongo((PulseMongo) storageObject, debugSave);
    }

    public static void Load(Object storageObject, boolean debugSave, StorageType... storageTypes){
        try {LoadRaw(storageObject, debugSave, storageTypes);}
        catch (IllegalAccessException | ParseException e) {throw new RuntimeException(e);}
    }

    public static void LoadRaw(Object storageObject, boolean debugSave, StorageType... storageTypes) throws ParseException, IllegalAccessException {
        if(PulseConfig.class.isAssignableFrom(storageObject.getClass()) && Arrays.asList(storageTypes).contains(StorageType.CONFIG)){
            LoadConfig((PulseConfig) storageObject, debugSave);
        }
        if(PulseMongo.class.isAssignableFrom(storageObject.getClass()) && Arrays.asList(storageTypes).contains(StorageType.MONGO)){
            LoadMongo((PulseMongo) storageObject, debugSave);
        }
    }

    private static void DeleteConfig(PulseConfig pulseConfig, boolean debugSave){
        var configObject = new ConfigObject(ConfigAPI.ReturnConfigPath(pulseConfig), pulseConfig.documentID(), debugSave);
        var configDeleteEvent = new PulseConfigDeleteEvent(pulseConfig);
        Bukkit.getServer().getPluginManager().callEvent(configDeleteEvent);
        if(!configDeleteEvent.isCancelled()) configObject.DeleteConfig();
    }

    private static void DeleteMongo(PulseMongo pulseMongo, boolean debugSave){
        var mongoConnection = com.pandapulsestudios.pulseconfig.PulseConfig.mongoConnections.getOrDefault(pulseMongo.databaseName(), new MongoConnection(pulseMongo.mongoIP(), pulseMongo.databaseName(), debugSave));
        var configSaveEvent = new PulseMongoDeleteEvent(pulseMongo);
        Bukkit.getServer().getPluginManager().callEvent(configSaveEvent);
        if(!configSaveEvent.isCancelled()) mongoConnection.Delete(pulseMongo.collectionName(), null, pulseMongo.documentID());
    }

    private static void SaveConfig(PulseConfig pulseConfig, boolean debugSave) throws IllegalAccessException{
        var configObject = new ConfigObject(ConfigAPI.ReturnConfigPath(pulseConfig), pulseConfig.documentID(), debugSave);
        if(configObject.FirstSave()) pulseConfig.FirstLoadConfig();
        var configSaveEvent = new PulseConfigSaveEvent(pulseConfig);
        Bukkit.getServer().getPluginManager().callEvent(configSaveEvent);
        if(!configSaveEvent.isCancelled()) ConfigSerializer.SaveConfig(pulseConfig, configObject);
    }

    private static void SaveMongo(PulseMongo pulseMongo, boolean debugSave) throws IllegalAccessException {
        var mongoConnection = com.pandapulsestudios.pulseconfig.PulseConfig.mongoConnections.getOrDefault(pulseMongo.databaseName(), new MongoConnection(pulseMongo.mongoIP(), pulseMongo.databaseName(), debugSave));
        if(mongoConnection.CountDocuments(pulseMongo.collectionName(), null, pulseMongo.documentID()) == 0) pulseMongo.FirstLoadMongo();
        var configSaveEvent = new PulseMongoSaveEvent(pulseMongo);
        Bukkit.getServer().getPluginManager().callEvent(configSaveEvent);
        if(!configSaveEvent.isCancelled()) MongoSerializer.SaveMongo(pulseMongo, mongoConnection);
    }

    private static void LoadConfig(PulseConfig pulseConfig, boolean debugSave) throws IllegalAccessException, ParseException {
        var configObject = new ConfigObject(ConfigAPI.ReturnConfigPath(pulseConfig), pulseConfig.documentID(), debugSave);
        if(configObject.FirstSave()){
            pulseConfig.FirstLoadConfig();
            Save(pulseConfig, debugSave);
        }else{
            var configLoadEvent = new PulseConfigLoadEvent(pulseConfig);
            Bukkit.getServer().getPluginManager().callEvent(configLoadEvent);
            if(!configLoadEvent.isCancelled()) ConfigDeSerializer.LoadConfig(pulseConfig, configObject);
        }
    }

    private static void LoadMongo(PulseMongo pulseMongo, boolean debugSave) throws ParseException, IllegalAccessException {
        var mongoConnection = com.pandapulsestudios.pulseconfig.PulseConfig.mongoConnections.getOrDefault(pulseMongo.databaseName(), new MongoConnection(pulseMongo.mongoIP(), pulseMongo.databaseName(), debugSave));
        if(mongoConnection.CountDocuments(pulseMongo.collectionName(), null, pulseMongo.documentID()) == 0){
            pulseMongo.FirstLoadMongo();
            SaveRaw(pulseMongo, debugSave);
        }else{
            var configSaveEvent = new PulseMongoLoadEvent(pulseMongo);
            Bukkit.getServer().getPluginManager().callEvent(configSaveEvent);
            if(!configSaveEvent.isCancelled()) MongoDeSerializer.LoadMongo(pulseMongo, mongoConnection);
        }
    }
}
