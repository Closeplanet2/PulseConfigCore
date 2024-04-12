package com.pandapulsestudios.pulseconfig.Objects;

import com.pandapulsestudios.pulseconfig.Serializer.ConfigDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializer.MongoDeSerializer;
import com.pandapulsestudios.pulseconfig.Interface.PulseClass;
import com.pandapulsestudios.pulseconfig.Enum.StorageType;
import com.pandapulsestudios.pulseconfig.Serializer.SerializerHelpers;
import org.bson.Document;

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

    public void DeSerialiseData(StorageType saveableType, HashMap<Object, Object> configData) throws ParseException, IllegalAccessException {
        for(var key : configData.keySet()){
            hashMap.put(
                    (K) DeSerialiseKey(saveableType, key, keyType),
                    (V) DeSerialiseValue(saveableType, configData.get(key), dataType)
            );
        }
    }

    public void DeSerialiseData(StorageType saveableType, Document configData) throws ParseException, IllegalAccessException {
        for(var key : configData.keySet()){
            hashMap.put(
                    (K) DeSerialiseKey(saveableType, key, keyType),
                    (V) DeSerialiseValue(saveableType, configData.get(key), dataType)
            );
        }
    }

    private K DeSerialiseKey(StorageType saveableType, Object configObject, Class<?> classType) throws ParseException, IllegalAccessException {
        if(PulseClass.class.isAssignableFrom(classType)){
            var pulseClas = (PulseClass) SerializerHelpers.CreateClassInstanceBlank(classType);
            pulseClas.BeforeLoadConfig();
            var deSerialised = saveableType == StorageType.MONGO ?
                    MongoDeSerializer.ReturnClassFields((Document) configObject, pulseClas.getClass(), pulseClas) :
                    ConfigDeSerializer.ReturnClassFields((HashMap<Object, Object>) configObject, pulseClas.getClass(), pulseClas);
            pulseClas.AfterLoadConfig();
            return (K) deSerialised;
        }else{
            if(saveableType == StorageType.CONFIG || saveableType == StorageType.BINARY) return (K) ConfigDeSerializer.LoadConfigSingle((K) configObject, configObject);
            if(saveableType == StorageType.MONGO) return (K) MongoDeSerializer.LoadMongoSingle((K) configObject, configObject);
        }
        return null;
    }

    private V DeSerialiseValue(StorageType saveableType, Object configObject, Class<?> classType) throws ParseException, IllegalAccessException {
        if(PulseClass.class.isAssignableFrom(classType)){
            var pulseClas = (PulseClass) SerializerHelpers.CreateClassInstanceBlank(classType);
            pulseClas.BeforeLoadConfig();
            var deSerialised = saveableType == StorageType.MONGO ?
                    MongoDeSerializer.ReturnClassFields((Document) configObject, pulseClas.getClass(), pulseClas) :
                    ConfigDeSerializer.ReturnClassFields((HashMap<Object, Object>) configObject, pulseClas.getClass(), pulseClas);
            pulseClas.AfterLoadConfig();
            return (V) deSerialised;
        }else{
            if(saveableType == StorageType.CONFIG || saveableType == StorageType.BINARY) return (V) ConfigDeSerializer.LoadConfigSingle((V) configObject, configObject);
            if(saveableType == StorageType.MONGO) return (V) MongoDeSerializer.LoadMongoSingle((K) configObject, configObject);
        }
        return null;
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
