package com.pandapulsestudios.pulseconfig.Serializers;

import com.pandapulsestudios.pulseconfig.Interfaces.PulseClass;
import com.pandapulsestudios.pulseconfig.Interfaces.PulseConfig;
import com.pandapulsestudios.pulseconfig.Interfaces.SaveName;
import com.pandapulsestudios.pulseconfig.Objects.ConfigObject;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import com.pandapulsestudios.pulsecore.Data.Interface.CustomVariable;
import com.pandapulsestudios.pulsecore.Java.JavaAPI;
import com.pandapulsestudios.pulsecore._Common.VariableTests.JavaLang.StringTest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.regex.Pattern;

public class ConfigDeSerializer {
    public static void LoadConfig(JavaPlugin javaPlugin, PulseConfig pulseConfig, ConfigObject configObject, boolean debugSave) throws Exception {
        pulseConfig.BeforeLoad();

        if(debugSave) Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Loading Class: " + ChatColor.RED + pulseConfig.getClass().getSimpleName());
        var dataFields = SerializerHelpers.ReturnAllFields(pulseConfig, null, null);
        var storedData = configObject.HashMap(pulseConfig.documentID(), true);
        for (var field : dataFields.keySet()){
            if(debugSave) Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Field name: " + ChatColor.GREEN  + field.getName());
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            if(!storedData.containsKey(fieldName)) storedData.put(fieldName, null);
            else field.set(pulseConfig, LoadConfig(javaPlugin, dataFields.get(field), storedData.get(fieldName), debugSave));
        }

        pulseConfig.AfterLoad();
    }

    private static Object LoadConfig(JavaPlugin javaPlugin, PulseClass pulseClass, HashMap<String, Object> storedData, boolean debugSave) throws Exception {
        pulseClass.BeforeLoad();
        if(debugSave) Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Loading Class: " + ChatColor.RED + pulseClass.getClass().getSimpleName());
        var dataFields = SerializerHelpers.ReturnAllFields(null, null, pulseClass);
        for (var field : dataFields.keySet()){
            if(debugSave) Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Field name: " + ChatColor.GREEN  + field.getName());
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            if(!storedData.containsKey(fieldName)) storedData.put(fieldName, null);
            else field.set(pulseClass, LoadConfig(javaPlugin, dataFields.get(field), storedData.get(fieldName), debugSave));
        }

        pulseClass.AfterLoad();
        return pulseClass;
    }

    private static HashMap<Object, Object> LoadConfig(JavaPlugin javaPlugin, HashMap<Object, Object> storedData, boolean debugSave) throws Exception{
        var returnData = new HashMap<Object, Object>();
        for (var datakey : storedData.keySet()){
            var serialisedKey = LoadConfig(javaPlugin, datakey, datakey, debugSave);
            var serialisedValue = LoadConfig(javaPlugin, storedData.get(datakey), storedData.get(datakey), debugSave);
            if(debugSave) Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Serialised Into Hashmap as: " + ChatColor.RED + serialisedKey.getClass().getSimpleName() + ":" + serialisedValue.getClass().getSimpleName());
            returnData.put(serialisedKey, serialisedKey);
        }
        return returnData;
    }

    private static ArrayList<Object> LoadConfig(JavaPlugin javaPlugin, List<Object> classData, List<Object> storedData, boolean debugSave) throws Exception {
        var returnData = new ArrayList<Object>();
        if(classData.isEmpty()) return returnData;
        var testData = classData.get(0);
        for(var data: storedData){
            if(debugSave) Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Attempting to place into array as: " + ChatColor.RED + testData.getClass().getSimpleName());
            var serialisedValue = LoadConfig(javaPlugin, testData, data, debugSave);
            if(debugSave) Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Serialised Into Array As: " + ChatColor.RED + serialisedValue.getClass().getSimpleName());
            returnData.add(serialisedValue);
        }
        return returnData;
    }

    private static Object LoadConfig(JavaPlugin javaPlugin, Object data, Object configObject, boolean debugSave) throws Exception {
        if(data == null) return null;
        if(debugSave) Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + "Field Type: " + ChatColor.RED + data.getClass().getSimpleName());
        if(data instanceof PulseClass) return LoadConfig(javaPlugin, (PulseClass) data, (HashMap<String, Object>) configObject, debugSave);
        else if(data instanceof HashMap) return LoadConfig(javaPlugin, (HashMap<Object, Object>) configObject, debugSave);
        else if(data instanceof List) return LoadConfig(javaPlugin, (List<Object>) data, (List<Object>) configObject, debugSave);
        else if(Pattern.compile("\\{[a-zA-Z0-9.]+\\}##\\.\\.##\\{\\d+\\.\\d+:\\d+\\.\\d+\\}").matcher(configObject.toString()).matches()){
            var classNamePattern = Pattern.compile("\\{[a-zA-Z0-9.]+\\}").matcher(configObject.toString());
            var classDataPattern = Pattern.compile("\\{\\d+\\.\\d+:\\d+\\.\\d+\\}").matcher(configObject.toString());
            if(!classNamePattern.find() || !classDataPattern.find()) return configObject.toString();
            var className = classNamePattern.group(0).replace("{", "").replace("}", "");
            var classData = classDataPattern.group(0).replace("{", "").replace("}", "");
            return ((CustomVariable) JavaAPI.ReturnClassFromPlugin(className).getDeclaredConstructor().newInstance()).DeSerializeData(classData);
        }
        else if(ConfigurationSerializable.class.isAssignableFrom(data.getClass())) return configObject;
        else if(data instanceof Date) return SerializerHelpers.SimpleDateFormat.parse(configObject.toString());
        else {
            var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(data.getClass());
            if (variableTest != null) return variableTest.DeSerializeData(configObject);
            return configObject.toString();
        }
    }
}
