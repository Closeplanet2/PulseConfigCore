package com.pandapulsestudios.pulseconfig.Serializers.Config;

import com.pandapulsestudios.pulseconfig.Interfaces.PulseClass;
import com.pandapulsestudios.pulseconfig.Interfaces.Config.PulseConfig;
import com.pandapulsestudios.pulseconfig.Objects.Config.ConfigObject;
import com.pandapulsestudios.pulseconfig.Objects.Savable.SaveableArrayList;
import com.pandapulsestudios.pulseconfig.Objects.Savable.SaveableHashmap;
import com.pandapulsestudios.pulseconfig.Serializers.SerializerHelpers;
import com.pandapulsestudios.pulsecore.Chat.ChatAPI;
import com.pandapulsestudios.pulsecore.Chat.MessageType;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import com.pandapulsestudios.pulsecore.Data.Interface.CustomVariable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import javax.swing.*;
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
            storedData.put(fieldName, SaveConfigSingle(dataFields.get(field)));
        }

        //Save the data to the config
        configObject.Set(pulseConfig.documentID(), storedData, debugSave);
        //Callback to be used after the config is saved
        pulseConfig.AfterSave();

        if(debugSave) ChatAPI.chatBuilder().SendMessage(ChatColor.RED + ConsoleSerializer.ConsoleOutput(pulseConfig));
    }

    public static HashMap<String, Object> SaveConfig(PulseClass pulseClass) throws Exception {
        pulseClass.BeforeSave();
        var storedData = new LinkedHashMap<String, Object>();
        storedData.put("CLASS_TYPE", pulseClass.getClass().getName());

        var dataFields = SerializerHelpers.ReturnALlFields(pulseClass);
        for(var field : dataFields.keySet()){
            var fieldName = SerializerHelpers.ReturnFieldName(field);
            storedData.put(fieldName, SaveConfigSingle(dataFields.get(field)));
        }

        pulseClass.AfterSave();
        return storedData;
    }

    private static HashMap<Object, Object> SaveConfig(SaveableHashmap saveableHashmap) throws Exception {
        var returnData = new LinkedHashMap<>();
        for(var sdKey : saveableHashmap.hashMap.keySet()) returnData.put(SaveConfigSingle(sdKey), SaveConfigSingle(saveableHashmap.hashMap.get(sdKey)));
        return returnData;
    }

    private static List<Object> SaveConfig(SaveableArrayList saveableArrayList) throws Exception {
        var returnData = new ArrayList<>();
        for(var sdValue : saveableArrayList.arrayList) returnData.add(SaveConfigSingle(sdValue));
        return returnData;
    }

    public static Object SaveConfigSingle(Object storedData) throws Exception {
        if(storedData == null) return null;
        if(storedData instanceof PulseClass pulseClass) return SaveConfig(pulseClass);
        else if(storedData instanceof SaveableHashmap saveableHashmap) return SaveConfig(saveableHashmap);
        else if(storedData instanceof SaveableArrayList saveableArrayList) return SaveConfig(saveableArrayList);
        else if(storedData instanceof CustomVariable customVariable){
            var data = new LinkedHashMap<String, Object>();
            data.put("CLASS_TYPE", customVariable.getClass().getName());
            data.put("DATA", customVariable.SerializeData());
            return data;
        }else if(ConfigurationSerializable.class.isAssignableFrom(storedData.getClass())) return storedData;
        else if(storedData instanceof Date date) return SerializerHelpers.SimpleDateFormat.format(date);
        else{
            var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(storedData.getClass());
            return variableTest == null ? storedData.toString() : variableTest.SerializeData(storedData);
        }
    }
}
