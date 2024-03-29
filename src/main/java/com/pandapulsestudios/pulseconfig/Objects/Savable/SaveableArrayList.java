package com.pandapulsestudios.pulseconfig.Objects.Savable;

import com.pandapulsestudios.pulseconfig.Enums.SaveableType;
import com.pandapulsestudios.pulseconfig.Interfaces.PulseClass;
import com.pandapulsestudios.pulseconfig.Serializers.Config.ConfigDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializers.Mongo.MongoDeSerializer;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import com.pandapulsestudios.pulsecore.Java.JavaAPI;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SaveableArrayList<E> {
    public List<E> arrayList = new ArrayList<E>();
    private final Class<?> classType;
    private final SaveableType saveableType;
    public SaveableArrayList(SaveableType saveableType, Class<?> classType){
        this.classType = classType;
        this.saveableType = saveableType;
    }

    public void AddData(Object configObject) throws Exception {
        if(PulseClass.class.isAssignableFrom(classType)){
            if(saveableType == SaveableType.MONGO){
                var document = (Document) configObject;
                if(document.containsKey("CLASS_TYPE")){
                    try{
                        var clazz = Class.forName((String) document.get("CLASS_TYPE"));
                        var pulseClass = ((PulseClass) clazz.getDeclaredConstructor().newInstance());
                        arrayList.add((E) MongoDeSerializer.LoadConfigPulseClass(pulseClass, document));
                    }catch (Exception ignored){}
                }
            }else{
                var hashMap = (HashMap<Object, Object>) configObject;
                if(hashMap.containsKey("CLASS_TYPE")){
                    try{
                        var clazz = Class.forName((String) hashMap.get("CLASS_TYPE"));
                        var pulseClass = ((PulseClass) clazz.getDeclaredConstructor().newInstance());
                        arrayList.add((E) ConfigDeSerializer.LoadConfigPulseClass(pulseClass, hashMap));
                    }catch (Exception ignored){}
                }
            }
        }else{
            var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(classType);
            if(variableTest == null){
                if(saveableType == SaveableType.MONGO) arrayList.add((E) MongoDeSerializer.LoadConfig((E) configObject, configObject));
                else arrayList.add((E) ConfigDeSerializer.LoadConfig((E) configObject, configObject));
            }
            else{
                var sData = (E) variableTest.DeSerializeData(configObject);
                if(saveableType == SaveableType.MONGO) arrayList.add((E) MongoDeSerializer.LoadConfig(sData, configObject));
                else arrayList.add((E) ConfigDeSerializer.LoadConfig(sData, configObject));
            }
        }
    }
}
