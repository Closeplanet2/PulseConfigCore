package com.pandapulsestudios.pulseconfig.Serializer;

import com.pandapulsestudios.pulseconfig.Objects.ConfigObject;
import com.pandapulsestudios.pulseconfig.Interface.StorageComment;
import com.pandapulsestudios.pulseconfig.Interface.ConfigFooter;
import com.pandapulsestudios.pulseconfig.Interface.ConfigHeader;
import com.pandapulsestudios.pulseconfig.Interface.PulseConfig;
import com.pandapulsestudios.pulseconfig.Interface.CustomVariable;
import com.pandapulsestudios.pulseconfig.Interface.PulseClass;
import com.pandapulsestudios.pulseconfig.Interface.SaveName;
import com.pandapulsestudios.pulseconfig.Objects.SaveableArrayList;
import com.pandapulsestudios.pulseconfig.Objects.SaveableHashmap;
import com.pandapulsestudios.pulseconfig.Objects.SaveableLinkedHashMap;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

public class ConfigSerializer {
    public static void SaveConfig(PulseConfig pulseConfig, ConfigObject configObject) throws IllegalAccessException {
        pulseConfig.BeforeSaveConfig();
        if(pulseConfig.getClass().isAnnotationPresent(ConfigHeader.class)) configObject.SetHeader(pulseConfig.getClass().getAnnotation(ConfigHeader.class));
        if(pulseConfig.getClass().isAnnotationPresent(ConfigFooter.class)) configObject.SetFooter(pulseConfig.getClass().getAnnotation(ConfigFooter.class));
        configObject.Set(pulseConfig.documentID(), ReturnClassFields(pulseConfig.getClass(), pulseConfig));
        pulseConfig.AfterSaveConfig();
    }

    public static LinkedHashMap<String, Object> ReturnClassFields(Class<?> parentClass, Object object) throws IllegalAccessException {
        var storedData = new LinkedHashMap<String, Object>();
        var dataFields = SerializerHelpers.ReturnALlFields(parentClass, object);
        for(var field : dataFields.keySet()){
            var fieldComment = field.isAnnotationPresent(StorageComment.class) ? field.getAnnotation(StorageComment.class).value() : "";
            if(!fieldComment.isEmpty()) storedData.put(String.format("# +------------------%s", fieldComment), "------------------+ #");
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            if(fieldName.isEmpty()) fieldName = field.getName();
            storedData.put(fieldName, SaveConfigSingle(dataFields.get(field)));
        }
        return storedData;
    }

    public static Object SaveConfigSingle(Object storedData) throws IllegalAccessException {
        if(storedData == null) return null;
        var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(storedData.getClass());
        if(storedData instanceof PulseClass pulseClass){
            pulseClass.BeforeSaveConfig();
            var data = ReturnClassFields(pulseClass.getClass(), pulseClass);
            pulseClass.AfterSaveConfig();
            return data;
        }
        else if(storedData instanceof SaveableHashmap saveableHashmap){
            var returnData = new LinkedHashMap<>();
            for(var key : saveableHashmap.keySet()) returnData.put(SaveConfigSingle(key), SaveConfigSingle(saveableHashmap.get(key)));
            return returnData;
        }
        else if(storedData instanceof SaveableLinkedHashMap saveableLinkedHashMap){
            var returnData = new LinkedHashMap<>();
            for(var key : saveableLinkedHashMap.keySet()) returnData.put(SaveConfigSingle(key), SaveConfigSingle(saveableLinkedHashMap.get(key)));
            return returnData;
        }
        else if(storedData instanceof SaveableArrayList saveableArrayList){
            var returnData = new ArrayList<>();
            for(var key : saveableArrayList.ReturnArrayList()) returnData.add(SaveConfigSingle(key));
            return returnData;
        }
        else if(storedData instanceof CustomVariable customVariable){
            customVariable.BeforeSave();
            var data = customVariable.SerializeData();
            customVariable.AfterSave();
            var returnData = new LinkedHashMap<>();
            for(var key : data.keySet()) returnData.put(SaveConfigSingle(key), SaveConfigSingle(data.get(key)));
            return returnData;
        }
        else if(ConfigurationSerialization.class.isAssignableFrom(storedData.getClass())) return storedData;
        else if(storedData instanceof Date date) return SerializerHelpers.SimpleDateFormat.format(date);
        else if(variableTest != null) return variableTest.SerializeData(storedData);
        return storedData;
    }

}
