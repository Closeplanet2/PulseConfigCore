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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SaveableArrayList<E> {
    private List<E> arrayList = new ArrayList<E>();
    private final Class<?> classType;
    public SaveableArrayList(Class<?> classType){
        this.classType = classType;
    }

    public void DeSerialiseData(StorageType saveableType, List<Object> configData) throws Exception {
        for(var configObject : configData){
            if(PulseClass.class.isAssignableFrom(classType)){
                var pulseClas = (PulseClass) SerializerHelpers.CreateClassInstanceBlank(classType);
                pulseClas.BeforeLoadConfig();
                Object deSerialised;
                if(saveableType == StorageType.CONFIG || saveableType == StorageType.BINARY){
                    deSerialised = ConfigDeSerializer.ReturnClassFields((HashMap<Object, Object>) configObject, pulseClas.getClass(), pulseClas);
                } else if(saveableType == StorageType.MONGO){
                    deSerialised = MongoDeSerializer.ReturnClassFieldsMap((Document) configObject, pulseClas.getClass(), pulseClas);
                } else deSerialised = JSONDeSerializer.ReturnClassFields((HashMap<Object, Object>) configObject, pulseClas.getClass(), pulseClas);
                arrayList.add((E) deSerialised);
                pulseClas.AfterLoadConfig();
            }else{
                if(saveableType == StorageType.CONFIG || saveableType == StorageType.BINARY) arrayList.add((E) ConfigDeSerializer.LoadConfigSingle(configObject, configObject));
                if(saveableType == StorageType.MONGO) arrayList.add((E) MongoDeSerializer.LoadMongoSingle(classType, configObject, configObject));
                else arrayList.add((E) JSONDeSerializer.LoadJSONSingle(configObject, configObject));
            }
        }
    }

    public List<E> ReturnArrayList(){ return arrayList; }
    public void SetArrayList(List<E> data){ arrayList = data; }
    public boolean add(E data){ return arrayList.add(data); }
    public boolean addAll(Collection<? extends E> data){ return arrayList.addAll(data); }
    public void clear(){ arrayList.clear(); }
    public boolean contains(E data){ return arrayList.contains(data); }
    public void forEach(Consumer<? super E> action){arrayList.forEach(action);}
    public E get(int index){return arrayList.get(index);}
    public int indexOff(E data){return arrayList.indexOf(data);}
    public boolean isEmpty(){return arrayList.isEmpty();}
    public boolean remove(E data){return arrayList.remove(data);}
    public E remove(int index){return arrayList.remove(index);}
    public boolean removeIf(Predicate<? super E> filter){return arrayList.removeIf(filter);}
    public E set(int index, E element){ return arrayList.set(index, element); }
    public int size(){return arrayList.size();}
    public void sort(Comparator<? super E> c){arrayList.sort(c);}
}
