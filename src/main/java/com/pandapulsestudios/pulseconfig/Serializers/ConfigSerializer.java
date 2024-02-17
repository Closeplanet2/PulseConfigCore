package com.pandapulsestudios.pulseconfig.Serializers;

import com.pandapulsestudios.pulseconfig.Interfaces.PulseClass;
import com.pandapulsestudios.pulseconfig.Interfaces.PulseConfig;
import com.pandapulsestudios.pulseconfig.Interfaces.SaveName;
import com.pandapulsestudios.pulseconfig.Objects.ConfigObject;
import com.pandapulsestudios.pulsecore.Chat.ChatAPI;
import com.pandapulsestudios.pulsecore.Chat.MessageType;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import com.pandapulsestudios.pulsecore.Data.Interface.CustomVariable;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class ConfigSerializer {
    public static void SaveConfig(PulseConfig pulseConfig, ConfigObject configObject, boolean debugSave) throws Exception {
        //Callback to be used before the config is saved
        pulseConfig.BeforeSave();
        var storedData = new LinkedHashMap<>();

        //Return the fields from the class
        var dataFields = SerializerHelpers.ReturnALlFields(pulseConfig);
        for(var field : dataFields.keySet()){
            //Check if the field has a different name to be saved with
            var fieldName = SerializerHelpers.ReturnFieldName(field);
            //Serialise the data and save it into the hashmap to be saved in the config
            storedData.put(fieldName, SaveConfig(dataFields.get(field)));
        }

        //Save the data to the config
        configObject.Set(pulseConfig.documentID(), storedData, debugSave);
        //Callback to be used after the config is saved
        pulseConfig.AfterSave();

        if(debugSave) ChatAPI.SendChat(ChatColor.RED + ConsoleSerializer.ConsoleOutput(pulseConfig), MessageType.ConsoleMessageNormal, false, null);
    }

    public static HashMap<String, Object> SaveConfig(PulseClass pulseClass) throws Exception {
        pulseClass.BeforeSave();
        var storedData = new LinkedHashMap<String, Object>();
        storedData.put("CLASS_TYPE", pulseClass.getClass().getName());

        var dataFields = SerializerHelpers.ReturnALlFields(pulseClass);
        for(var field : dataFields.keySet()){
            var fieldName = SerializerHelpers.ReturnFieldName(field);
            storedData.put(fieldName, SaveConfig(dataFields.get(field)));
        }

        pulseClass.AfterSave();
        return storedData;
    }

    private static HashMap<Object, Object> SaveConfig(HashMap<Object, Object> storedData) throws Exception {
        var returnData = new LinkedHashMap<>();
        for(var sdKey : storedData.keySet()) returnData.put(SaveConfig(sdKey), SaveConfig(storedData.get(sdKey)));
        return returnData;
    }

    private static List<Object> SaveConfig(List<Object> storedData) throws Exception {
        var returnData = new ArrayList<>();
        for(var sdValue : storedData) returnData.add(SaveConfig(sdValue));
        return returnData;
    }

    private static Object SaveConfig(Object storedData) throws Exception {
        if(storedData == null) return null;
        if(storedData instanceof PulseClass pulseClass) return SaveConfig(pulseClass);
        else if(storedData instanceof HashMap hashMap) return SaveConfig(hashMap);
        else if(storedData instanceof List list) return SaveConfig(list);
        else if(storedData instanceof CustomVariable customVariable){
            var data = new LinkedHashMap<String, Object>();
            data.put("CLASS_TYPE", customVariable.getClass().getName());
            data.put("DATA", customVariable.SerializeData());
            return data;
        }else if(ConfigurationSerializable.class.isAssignableFrom(storedData.getClass())) return storedData;
        else if(storedData instanceof Date date) return SerializerHelpers.SimpleDateFormat.format(date);
        else{
            var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(storedData.getClass());
            return variableTest == null ? null : variableTest.SerializeData(storedData);
        }
    }
}
