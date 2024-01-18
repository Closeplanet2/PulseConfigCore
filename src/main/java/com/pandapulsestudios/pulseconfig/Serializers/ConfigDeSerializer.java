package com.pandapulsestudios.pulseconfig.Serializers;

import com.pandapulsestudios.pulseconfig.Interfaces.PulseClass;
import com.pandapulsestudios.pulseconfig.Interfaces.PulseConfig;
import com.pandapulsestudios.pulseconfig.Interfaces.SaveName;
import com.pandapulsestudios.pulseconfig.Objects.ConfigObject;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import com.pandapulsestudios.pulsecore.Data.Interface.CustomVariable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class ConfigDeSerializer {
    public static void LoadConfig(PulseConfig pulseConfig, ConfigObject configObject) throws Exception {
        var dataFields = SerializerHelpers.ReturnAllFields(pulseConfig, null, null);
        var storedData = configObject.HashMap(pulseConfig.documentID(), true);
        for (var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            if(!storedData.containsKey(fieldName)) storedData.put(fieldName, null);
            else field.set(pulseConfig, LoadConfig(dataFields.get(field), storedData.get(fieldName)));
        }
    }

    private static Object LoadConfig(PulseClass pulseClass, HashMap<String, Object> storedData) throws Exception {
        var dataFields = SerializerHelpers.ReturnAllFields(null, null, pulseClass);
        for (var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            if(!storedData.containsKey(fieldName)) storedData.put(fieldName, null);
            else field.set(pulseClass, LoadConfig(dataFields.get(field), storedData.get(fieldName)));
        }
        return pulseClass;
    }

    private static HashMap<Object, Object> LoadConfig(HashMap<Object, Object> storedData) throws Exception{
        var returnData = new HashMap<Object, Object>();
        for (var datakey : storedData.keySet()) returnData.put(LoadConfig(datakey, datakey), LoadConfig(storedData.get(datakey), storedData.get(datakey)));
        return returnData;
    }

    private static ArrayList<Object> LoadConfig(ArrayList<Object> storedData) throws Exception {
        var returnData = new ArrayList<Object>();
        for(var data: storedData) returnData.add(LoadConfig(data, data));
        return returnData;
    }

    private static Object LoadConfig(Object data, Object configObject) throws Exception {
        if(data instanceof PulseClass) return LoadConfig((PulseClass) data, (HashMap<String, Object>) configObject);
        else if(data instanceof HashMap) return LoadConfig((HashMap<Object, Object>) configObject);
        else if(data instanceof ArrayList) return LoadConfig((ArrayList<Object>) configObject);
        else if(data instanceof CustomVariable)  return ((CustomVariable) data.getClass().getDeclaredConstructor().newInstance()).DeSerializeData(configObject.toString());
        else if(ConfigurationSerializable.class.isAssignableFrom(data.getClass())) return configObject;
        else if(data instanceof Date) return SerializerHelpers.SimpleDateFormat.parse(configObject.toString());
        else{
            var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(data.getClass());
            if(variableTest == null) return null;

            if(data.getClass() == String.class){
                var UUIDVariableTest = VariableAPI.RETURN_TEST_FROM_TYPE(UUID.class);
                if(UUIDVariableTest.IsType(configObject)) return UUIDVariableTest.DeSerializeData(configObject);
            }

            return variableTest.DeSerializeData(configObject);
        }
    }
}
