package com.pandapulsestudios.pulseconfig.Serializer;

import com.pandapulsestudios.pulseconfig.Interface.CustomVariable;
import com.pandapulsestudios.pulseconfig.Objects.ConfigObject;
import com.pandapulsestudios.pulseconfig.Interface.PulseClass;
import com.pandapulsestudios.pulseconfig.Interface.SaveName;
import com.pandapulsestudios.pulseconfig.Interface.PulseConfig;
import com.pandapulsestudios.pulseconfig.Objects.SaveableArrayList;
import com.pandapulsestudios.pulseconfig.Objects.SaveableHashmap;
import com.pandapulsestudios.pulseconfig.Objects.SaveableLinkedHashMap;
import com.pandapulsestudios.pulseconfig.Enum.StorageType;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.text.ParseException;
import java.util.*;

public class ConfigDeSerializer {
    public static void LoadConfig(PulseConfig pulseConfig, ConfigObject configObject) throws Exception {
        pulseConfig.BeforeLoadConfig();
        var storedData = configObject.HashMap(pulseConfig.documentID(), true);
        var data = ReturnClassFields(storedData, pulseConfig.getClass(), pulseConfig);
        pulseConfig.AfterLoadConfig();
    }

    public static Object ReturnClassFields(HashMap<Object, Object> configData, Class<?> parentClass, Object object) throws Exception {
        var dataFields = SerializerHelpers.ReturnALlFields(parentClass, object);
        for(var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            if(!configData.containsKey(fieldName)){
                field.set(object, ConfigSerializer.SaveConfigSingle(dataFields.get(field)));
            }
            else {
                var deSerialisedData = LoadConfigSingle(dataFields.get(field), configData.get(fieldName));
                try{field.set(object, deSerialisedData);}
                catch(Exception ignored) {field.set(object, dataFields.get(field));}
            }
        }
        return object;
    }

    public static Object LoadConfigSingle(Object classData, Object configData) throws Exception {
        if(classData == null || configData == null) return null;
        var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(classData.getClass());
        if(classData instanceof PulseClass pulseClass){
            pulseClass.BeforeLoadConfig();
            var data = ReturnClassFields((HashMap<Object, Object>) configData, pulseClass.getClass(), pulseClass);
            pulseClass.AfterLoadConfig();
            return data;
        }
        else if(classData instanceof SaveableHashmap saveableHashmap){
            saveableHashmap.clear();
            saveableHashmap.DeSerialiseData(StorageType.CONFIG, (HashMap<Object, Object>) configData);
            return saveableHashmap;
        }
        else if(classData instanceof SaveableLinkedHashMap saveableLinkedHashMap){
            saveableLinkedHashMap.clear();
            saveableLinkedHashMap.DeSerialiseData(StorageType.CONFIG, (HashMap<Object, Object>) configData);
            return saveableLinkedHashMap;
        }
        else if(classData instanceof SaveableArrayList saveableArrayList) {
            saveableArrayList.clear();
            saveableArrayList.DeSerialiseData(StorageType.CONFIG, (ArrayList<Object>) configData);
            return saveableArrayList;
        }
        else if(classData instanceof CustomVariable customVariable){
            var hashMap = (HashMap<Object, Object>) configData;
            customVariable.BeforeLoad();
            customVariable.DeSerializeData(hashMap);
            customVariable.AfterLoad();
            return customVariable;
        }
        else if(ConfigurationSerialization.class.isAssignableFrom(classData.getClass())) return configData;
        else if(classData instanceof Date) return SerializerHelpers.SimpleDateFormat.parse(configData.toString());
        else if(variableTest != null) return variableTest.DeSerializeData(configData);
        return configData;
    }
}
