package com.pandapulsestudios.pulseconfig.Serializer;

import com.pandapulsestudios.pulseconfig.Interface.CustomVariable;
import com.pandapulsestudios.pulseconfig.Objects.*;
import com.pandapulsestudios.pulseconfig.Interface.PulseClass;
import com.pandapulsestudios.pulseconfig.Interface.SaveName;
import com.pandapulsestudios.pulseconfig.Interface.PulseConfig;
import com.pandapulsestudios.pulseconfig.Enum.StorageType;
import com.pandapulsestudios.pulsecore.VariableAPI.API.VariableAPI;
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
                var deSerialisedData = LoadConfigSingle(dataFields.get(field).getClass(), dataFields.get(field), configData.get(fieldName));
                try{field.set(object, deSerialisedData);}
                catch(Exception ignored) {field.set(object, dataFields.get(field));}
            }
        }
        return object;
    }

    public static Object LoadConfigSingle(Class<?> classDataType, Object classData, Object configData) throws Exception {
        if(classData == null || configData == null) return null;
        var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(classDataType);
        if(ConfigurationSerialization.class.isAssignableFrom(classDataType)){
            return configData;
        }else if(PulseClass.class.isAssignableFrom(classDataType)){
            var pulseClass = (PulseClass) classData;
            pulseClass.BeforeLoadConfig();
            var data = ReturnClassFields((HashMap<Object, Object>) configData, pulseClass.getClass(), pulseClass);
            pulseClass.AfterLoadConfig();
            return data;
        }else if(SaveableHashmap.class.isAssignableFrom(classDataType)){
            var saveableHashmap = (SaveableHashmap) classData;
            saveableHashmap.clear();
            saveableHashmap.DeSerialiseData(StorageType.CONFIG, (HashMap<Object, Object>) configData);
            return saveableHashmap;
        }else if(SaveableLinkedHashMap.class.isAssignableFrom(classDataType)){
            var saveableLinkedHashMap = (SaveableLinkedHashMap) classData;
            saveableLinkedHashMap.clear();
            saveableLinkedHashMap.DeSerialiseData(StorageType.CONFIG, (HashMap<Object, Object>) configData);
            return saveableLinkedHashMap;
        }else if(SaveableArrayList.class.isAssignableFrom(classDataType)) {
            var saveableArrayList = (SaveableArrayList) classData;
            saveableArrayList.clear();
            saveableArrayList.DeSerialiseData(StorageType.CONFIG, (ArrayList<Object>) configData);
            return saveableArrayList;
        }else if(CustomVariable.class.isAssignableFrom(classDataType)){
            var customVariable = (CustomVariable) classDataType.getConstructor().newInstance();
            var hashMap = (HashMap<Object, Object>) configData;
            customVariable.BeforeLoad();
            customVariable.DeSerializeData(hashMap);
            customVariable.AfterLoad();
            return customVariable;
        }else if(Date.class.isAssignableFrom(classDataType)){
            return SerializerHelpers.SimpleDateFormat.parse(configData.toString());
        }else if(variableTest != null){
            return variableTest.DeSerializeData(configData);
        }
        return configData;
    }
}
