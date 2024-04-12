package com.pandapulsestudios.pulseconfig.Serializers.Config;

import com.pandapulsestudios.pulseconfig.Interfaces.PulseClass;
import com.pandapulsestudios.pulseconfig.Interfaces.Config.PulseConfig;
import com.pandapulsestudios.pulseconfig.Interfaces.SaveName;
import com.pandapulsestudios.pulseconfig.Objects.Config.ConfigObject;
import com.pandapulsestudios.pulseconfig.Objects.Savable.SaveableArrayList;
import com.pandapulsestudios.pulseconfig.Objects.Savable.SaveableHashmap;
import com.pandapulsestudios.pulseconfig.Serializers.SerializerHelpers;
import com.pandapulsestudios.pulsecore.Chat.ChatAPI;
import com.pandapulsestudios.pulsecore.Chat.MessageType;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import com.pandapulsestudios.pulsecore.Data.Interface.CustomVariable;
import com.pandapulsestudios.pulsecore.Java.JavaAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class ConfigDeSerializer {
    public static void LoadConfig(PulseConfig pulseConfig, ConfigObject configObject, boolean debugLoad) throws Exception {
        pulseConfig.BeforeLoad();

        var storedData = configObject.HashMap(pulseConfig.documentID(), true);
        var dataFields = SerializerHelpers.ReturnALlFields(pulseConfig);
        for(var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            if(!storedData.containsKey(fieldName)) field.set(pulseConfig, null);
            else field.set(pulseConfig, LoadConfig(dataFields.get(field), storedData.get(fieldName)));
        }

        pulseConfig.AfterLoad();
        if(debugLoad) ChatAPI.chatBuilder().SendMessage(ChatColor.RED + ConsoleSerializer.ConsoleOutput(pulseConfig));
    }

    public static Object LoadConfigPulseClass(PulseClass pulseClass, HashMap<Object, Object> configData) throws Exception {
        pulseClass.BeforeLoad();

        var dataFields = SerializerHelpers.ReturnALlFields(pulseClass);
        for (var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            if(!configData.containsKey(fieldName)) field.set(pulseClass, null);
            else field.set(pulseClass, LoadConfig(dataFields.get(field), configData.get(fieldName)));
        }

        pulseClass.AfterLoad();
        return pulseClass;
    }

    public static SaveableHashmap<Object, Object> LoadConfigHashMap(SaveableHashmap<Object, Object> saveableHashmap, HashMap<Object, Object> configData) throws Exception {
        for(var dataKey : configData.keySet()){
            var dataValue = configData.get(dataKey);
            saveableHashmap.AddData(dataKey, dataValue);
        }
        return saveableHashmap;
    }

    public static SaveableArrayList<Object> LoadConfigArray(SaveableArrayList<Object> saveableArrayList, List<Object> configData) throws Exception {
        for(var data : configData){

            saveableArrayList.AddData(data);
        }
        return saveableArrayList;
    }

    public static Object LoadConfig(Object classData, Object configData) throws Exception {
        if(classData == null || configData == null) return null;
        if(classData instanceof PulseClass pulseClass){
            return LoadConfigPulseClass(pulseClass, (HashMap<Object, Object>) configData);
        }else if(classData instanceof SaveableHashmap saveableHashmap){
            return LoadConfigHashMap(saveableHashmap, (HashMap<Object, Object>) configData);
        }else if(classData instanceof SaveableArrayList saveableArrayList){
            return LoadConfigArray(saveableArrayList, (List<Object>) configData);
        }else if(classData instanceof CustomVariable){
            var configHashMap = (HashMap<String, Object>) configData;
            if(configHashMap.isEmpty()) return new HashMap<String, Object>();
            if(!configHashMap.containsKey("CLASS_TYPE") || !configHashMap.containsKey("DATA")) return new HashMap<String, Object>();
            var serialisedClassName = (String) configHashMap.get("CLASS_TYPE");
            var serialisedData = (HashMap<String, Object>) configHashMap.get("DATA");
            return ((CustomVariable) Class.forName(serialisedClassName).getDeclaredConstructor().newInstance()).DeSerializeData(serialisedData);
        }else if(ConfigurationSerializable.class.isAssignableFrom(classData.getClass())){
            return configData;
        }else if(classData instanceof Date){
            return SerializerHelpers.SimpleDateFormat.parse(configData.toString());
        }else{
            var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(classData.getClass());
            return variableTest == null ? configData.toString() : variableTest.DeSerializeData(configData);
        }
    }
}
