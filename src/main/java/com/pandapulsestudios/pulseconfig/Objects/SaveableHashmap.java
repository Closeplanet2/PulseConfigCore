package com.pandapulsestudios.pulseconfig.Objects;

import com.pandapulsestudios.pulseconfig.Serializer.ConfigDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializer.JSONDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializer.MongoDeSerializer;
import com.pandapulsestudios.pulseconfig.Interface.PulseClass;
import com.pandapulsestudios.pulseconfig.Enum.StorageType;
import com.pandapulsestudios.pulseconfig.Serializer.SerializerHelpers;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.simple.JSONObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Set;
import java.util.function.BiConsumer;

public class SaveableHashmap<K, V> {
    private HashMap<K, V> hashMap = new HashMap<>();
    private final Class<?> keyType;
    private final Class<?> dataType;

    public SaveableHashmap(Class<?> keyType, Class<?> dataType){
        this.keyType = keyType;
        this.dataType = dataType;
    }

    public void DeSerialiseData(StorageType saveableType, HashMap<Object, Object> configData) throws Exception {
        for(var key : configData.keySet()){
            var dx = DeSerialiseKey(saveableType, key, keyType);
            var dy = DeSerialiseValue(saveableType, configData.get(key), dataType);
            hashMap.put(dx, dy);
        }
    }

    public void DeSerialiseData(StorageType saveableType, Document document) throws Exception {
        for(var key : document.keySet()){
            var dx = DeSerialiseKey(saveableType, key, keyType);
            var dy = DeSerialiseValue(saveableType, document.get(key), dataType);
            hashMap.put(dx, dy);
        }
    }

    private K DeSerialiseKey(StorageType saveableType, Object configObject, Class<?> classType) throws Exception {
        if(PulseClass.class.isAssignableFrom(classType)){
            var pulseClas = (PulseClass) SerializerHelpers.CreateClassInstanceBlank(classType);
            pulseClas.BeforeLoadConfig();
            Object deSerialised;
            if(saveableType == StorageType.CONFIG || saveableType == StorageType.BINARY){
                deSerialised = ConfigDeSerializer.ReturnClassFields((HashMap<Object, Object>) configObject, pulseClas.getClass(), pulseClas);
            } else if(saveableType == StorageType.MONGO){
                deSerialised = MongoDeSerializer.ReturnClassFieldsMap((Document) configObject, pulseClas.getClass(), pulseClas);
            } else deSerialised = JSONDeSerializer.ReturnClassFields((HashMap<Object, Object>) configObject, pulseClas.getClass(), pulseClas);
            pulseClas.AfterLoadConfig();
            return (K) deSerialised;
        }else{
            var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(classType);
            if(variableTest != null) configObject = variableTest.DeSerializeData(configObject);
            if(saveableType == StorageType.CONFIG || saveableType == StorageType.BINARY) return (K) ConfigDeSerializer.LoadConfigSingle(configObject, configObject);
            if(saveableType == StorageType.MONGO) return (K) MongoDeSerializer.LoadMongoSingle(keyType, configObject, configObject);
            return (K) JSONDeSerializer.LoadJSONSingle(configObject, configObject);
        }
    }

    private V DeSerialiseValue(StorageType saveableType, Object configObject, Class<?> classType) throws Exception {
        if(PulseClass.class.isAssignableFrom(classType)){
            var pulseClas = (PulseClass) SerializerHelpers.CreateClassInstanceBlank(classType);
            pulseClas.BeforeLoadConfig();
            Object deSerialised;
            if(saveableType == StorageType.CONFIG || saveableType == StorageType.BINARY){
                deSerialised = ConfigDeSerializer.ReturnClassFields((HashMap<Object, Object>) configObject, pulseClas.getClass(), pulseClas);
            } else if(saveableType == StorageType.MONGO){
                deSerialised = MongoDeSerializer.ReturnClassFieldsMap((Document) configObject, pulseClas.getClass(), pulseClas);
            } else deSerialised = JSONDeSerializer.ReturnClassFields((HashMap<Object, Object>) configObject, pulseClas.getClass(), pulseClas);
            pulseClas.AfterLoadConfig();
            return (V) deSerialised;
        }else{
            var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(classType);
            if(variableTest != null) configObject = variableTest.DeSerializeData(configObject);
            if(saveableType == StorageType.CONFIG || saveableType == StorageType.BINARY) return (V) ConfigDeSerializer.LoadConfigSingle(configObject, configObject);
            if(saveableType == StorageType.MONGO) return (V) MongoDeSerializer.LoadMongoSingle(dataType, configObject, configObject);
            return (V) JSONDeSerializer.LoadJSONSingle(configObject, configObject);
        }
    }


    public  HashMap<K, V> ReturnHashMap(){return hashMap;}
    public void SetHashMap(HashMap<K, V> data){hashMap = data;}
    public void clear(){hashMap.clear();}
    public boolean containsKey(K key){return hashMap.containsKey(key);}
    public boolean containsValue(V key){return hashMap.containsValue(key);}
    public void forEach(BiConsumer<? super K,? super V> action){hashMap.forEach(action);}
    public V get(K key){return hashMap.get(key);}
    public V getOrDefault(K key, V data){return hashMap.getOrDefault(key, data);}
    public boolean isEmpty(){return hashMap.isEmpty();}
    public int size(){return hashMap.size();}
    public Set<K> keySet(){return hashMap.keySet();}
    public void put(K key, V data){hashMap.put(key, data);}


}
