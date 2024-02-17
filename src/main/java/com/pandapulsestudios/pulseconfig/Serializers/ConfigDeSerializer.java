package com.pandapulsestudios.pulseconfig.Serializers;

import com.pandapulsestudios.pulseconfig.Interfaces.PulseClass;
import com.pandapulsestudios.pulseconfig.Interfaces.PulseConfig;
import com.pandapulsestudios.pulseconfig.Interfaces.SaveName;
import com.pandapulsestudios.pulseconfig.Objects.ConfigObject;
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
        if(debugLoad) ChatAPI.SendChat(ChatColor.RED + ConsoleSerializer.ConsoleOutput(pulseConfig), MessageType.ConsoleMessageNormal, false, null);
    }

    public static Object LoadConfig(PulseClass pulseClass, HashMap<Object, Object> configData) throws Exception {
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

    public static HashMap<Object, Object> LoadConfig(HashMap<Object, Object> configData) throws Exception {
        var returnData = new HashMap<Object, Object>();
        for (var datakey : configData.keySet()){
            var serialisedKey = LoadConfig(datakey, datakey);
            var serialisedValue = LoadConfig(configData.get(datakey), configData.get(datakey));
            var data_flag = false;

            if(serialisedValue instanceof HashMap hashMap){
                if(hashMap.containsKey("CLASS_TYPE")){
                    try{
                        var clazz = JavaAPI.ReturnClassFromPlugin((String) hashMap.get("CLASS_TYPE"));
                        var pulseClass = ((PulseClass) clazz.getDeclaredConstructor().newInstance());
                        returnData.put(serialisedKey, LoadConfig(pulseClass, hashMap));
                        data_flag = true;
                    }catch (Exception ignored){}
                }
            }

            if(!data_flag) returnData.put(serialisedKey, serialisedValue);
        }
        return returnData;
    }

    public static List<Object> LoadConfig(List<Object> configData) throws Exception {
        var returnData = new ArrayList<Object>();
        for(var data: configData){
            var serialisedValue = LoadConfig(data, data);
            var data_flag = false;

            if(serialisedValue instanceof HashMap hashMap){
                if(hashMap.containsKey("CLASS_TYPE")){
                    try{
                        var clazz = JavaAPI.ReturnClassFromPlugin((String) hashMap.get("CLASS_TYPE"));
                        var pulseClass = ((PulseClass) clazz.getDeclaredConstructor().newInstance());
                        returnData.add(LoadConfig(pulseClass, hashMap));
                        data_flag = true;
                    }catch (Exception ignored){}
                }
            }

            if(!data_flag) returnData.add(serialisedValue);
        }
        return returnData;
    }

    public static Object LoadConfig(Object classData, Object configData) throws Exception {
        if(classData == null || configData == null) return null;
        if(classData instanceof PulseClass pulseClass){
            return LoadConfig(pulseClass, (HashMap<Object, Object>) configData);
        }else if(classData instanceof HashMap){
            return LoadConfig((HashMap<Object, Object>) configData);
        }else if(classData instanceof List){
            return LoadConfig((List<Object>) configData);
        }else if(classData instanceof CustomVariable){
            var configHashMap = (HashMap<String, Object>) configData;
            if(configHashMap.isEmpty()) return new HashMap<String, Object>();
            if(!configHashMap.containsKey("CLASS_TYPE") || !configHashMap.containsKey("DATA")) return new HashMap<String, Object>();
            var serialisedClassName = (String) configHashMap.get("CLASS_TYPE");
            var serialisedData = (HashMap<String, Object>) configHashMap.get("DATA");
            return ((CustomVariable) JavaAPI.ReturnClassFromPlugin(serialisedClassName).getDeclaredConstructor().newInstance()).DeSerializeData(serialisedData);
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
