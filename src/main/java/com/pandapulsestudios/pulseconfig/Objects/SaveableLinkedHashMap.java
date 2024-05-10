package com.pandapulsestudios.pulseconfig.Objects;

import com.pandapulsestudios.pulseconfig.Serializer.ConfigDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializer.JSONDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializer.MongoDeSerializer;
import com.pandapulsestudios.pulseconfig.Interface.PulseClass;
import com.pandapulsestudios.pulseconfig.Enum.StorageType;
import com.pandapulsestudios.pulseconfig.Serializer.SerializerHelpers;
import com.pandapulsestudios.pulsecore.VariableAPI.API.VariableAPI;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.simple.JSONObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.function.BiConsumer;

public class SaveableLinkedHashMap<K, V> {
    private LinkedHashMap<K, V> hashMap = new LinkedHashMap<>();
    private final Class<?> keyType;
    private final Class<?> dataType;

    public SaveableLinkedHashMap(Class<?> keyType, Class<?> dataType){
        this.keyType = keyType;
        this.dataType = dataType;
    }

    public void DeSerialiseData(StorageType saveableType, HashMap<Object, Object> configData) throws Exception {
        for(var key : configData.keySet()){
            hashMap.put(
                    (K) DeSerialiseKey(saveableType, key, keyType),
                    (V) DeSerialiseValue(saveableType, configData.get(key), dataType)
            );
        }
    }

    public void DeSerialiseData(StorageType saveableType, Document configData) throws Exception {
        for(var key : configData.keySet()){
            hashMap.put(
                    (K) DeSerialiseKey(saveableType, key, keyType),
                    (V) DeSerialiseValue(saveableType, configData.get(key), dataType)
            );
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
            if(saveableType == StorageType.CONFIG || saveableType == StorageType.BINARY) return (K) ConfigDeSerializer.LoadConfigSingle(keyType, configObject, configObject);
            if(saveableType == StorageType.MONGO) return (K) MongoDeSerializer.LoadMongoSingle(keyType, configObject, configObject);
            return (K) JSONDeSerializer.LoadJSONSingle(keyType, configObject, configObject);
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
            if(saveableType == StorageType.CONFIG || saveableType == StorageType.BINARY) return (V) ConfigDeSerializer.LoadConfigSingle(dataType, configObject, configObject);
            if(saveableType == StorageType.MONGO) return (V) MongoDeSerializer.LoadMongoSingle(dataType, configObject, configObject);
            return (V) JSONDeSerializer.LoadJSONSingle(dataType, configObject, configObject);
        }
    }

    public  LinkedHashMap<K, V> ReturnHashMap(){return hashMap;}
    public void SetHashMap(LinkedHashMap<K, V> data){hashMap = data;}
    public void clear(){hashMap.clear();}
    public boolean containsKey(K key){return hashMap.containsKey(key);}
    public boolean containsValue(V key){return hashMap.containsValue(key);}
    public void forEach(BiConsumer<? super K,? super V> action){hashMap.forEach(action);}
    public V get(K key){return hashMap.get(key);}
    public V getOrDefault(K key, V data){return hashMap.getOrDefault(key, data);}
    public boolean isEmpty(){return hashMap.isEmpty();}
    public int size(){return hashMap.size();}
    public Set<K> keySet(){return hashMap.keySet();}
}
