package com.pandapulsestudios.pulseconfig.Objects.Savable;

import com.pandapulsestudios.pulseconfig.Enums.SaveableType;
import com.pandapulsestudios.pulseconfig.Interfaces.PulseClass;
import com.pandapulsestudios.pulseconfig.Serializers.Config.ConfigDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializers.JSON.JSONDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializers.Mongo.MongoDeSerializer;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import com.pandapulsestudios.pulsecore.Java.JavaAPI;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;

public class SaveableHashmap<E, F> {
    public HashMap<E, F> hashMap = new HashMap<>();
    private final SaveableType saveableType;
    private final Class<?> keyType;
    private final Class<?> dataType;

    public SaveableHashmap(SaveableType saveableType, Class<?> keyType, Class<?> dataType){
        this.saveableType = saveableType;
        this.keyType = keyType;
        this.dataType = dataType;
    }

    public void AddData(Object dataKey, Object dataValue) throws Exception {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[" + dataKey + "]");
        var sDataKey = (E) SerialiseDataKey(dataKey);
        var sDataValue = (F) SerialiseDataValue(dataValue);
        hashMap.put(sDataKey, sDataValue);
    }

    private Object SerialiseDataKey(Object dataKey) throws Exception {
        var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(keyType);
        if(variableTest == null) return ConfigDeSerializer.LoadConfig((E) dataKey, dataKey);
        var sData = variableTest.DeSerializeData(dataKey);
        return ConfigDeSerializer.LoadConfig(sData, dataKey);
    }

    private Object SerialiseDataValue(Object dataValue) throws Exception {
        if(PulseClass.class.isAssignableFrom(dataType)){
            if(saveableType == SaveableType.MONGO){
                var document = (Document) dataValue;
                if(document.containsKey("CLASS_TYPE")){
                    try{
                        var clazz =  Class.forName((String) document.get("CLASS_TYPE"));
                        var pulseClass = ((PulseClass) clazz.getDeclaredConstructor().newInstance());
                        return MongoDeSerializer.LoadConfigPulseClass(pulseClass, document);
                    }catch (Exception ignored){}
                }
            }else{
                var hashMap = (HashMap<Object, Object>) dataValue;
                if(hashMap.containsKey("CLASS_TYPE")){
                    try{
                        var clazz =  Class.forName((String) hashMap.get("CLASS_TYPE"));
                        var pulseClass = ((PulseClass) clazz.getDeclaredConstructor().newInstance());
                        return ConfigDeSerializer.LoadConfigPulseClass(pulseClass, hashMap);
                    }catch (Exception ignored){}
                }
            }
        }

        var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(dataType);
        if(variableTest == null) return ConfigDeSerializer.LoadConfig((F) dataValue, dataValue);
        var sData = variableTest.DeSerializeData(dataValue);
        if(saveableType == SaveableType.MONGO) return MongoDeSerializer.LoadConfig(sData, dataValue);
        else return ConfigDeSerializer.LoadConfig(sData, dataValue);
    }
}

