package com.pandapulsestudios.pulseconfig.Interfaces;

import com.pandapulsestudios.pulseconfig.APIS.ConfigAPI;
import com.pandapulsestudios.pulseconfig.Objects.ConfigObject;
import com.pandapulsestudios.pulseconfig.Serializers.ConfigDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializers.ConfigSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public interface PulseConfig {
    String documentID();
    default void FirstLoad(){}

    default void Save(Class<?> mainClass, boolean clearConfig, boolean debugSave){
        try {SaveRaw(mainClass, clearConfig, debugSave, false);}
        catch (Exception e) {throw new RuntimeException(e);}
    }

    default void Load(Class<?> mainClass, boolean clearConfig, boolean debugSave){
        try {LoadRaw(mainClass, clearConfig, debugSave);}
        catch (Exception e) {throw new RuntimeException(e);}
    }

    default void SaveRaw(Class<?> mainClass, boolean clearConfig, boolean debugSave, boolean firstLoad) throws Exception {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Trying to save file!");
        var configPath = ConfigAPI.ReturnConfigPath(mainClass, getClass());
        var configObject = new ConfigObject(configPath, documentID());
        if(clearConfig) configObject.ClearConfig();
        if(firstLoad) FirstLoad();
        ConfigSerializer.SaveConfig(this, configObject, debugSave);
    }

    default void LoadRaw(Class<?> mainClass, boolean clearConfig, boolean debugSave) throws Exception {
        var configPath = ConfigAPI.ReturnConfigPath(mainClass, getClass());
        var configObject = new ConfigObject(configPath, documentID());
        if(!configObject.saveFlag) SaveRaw(mainClass, clearConfig, debugSave, true);
        else ConfigDeSerializer.LoadConfig(this, configObject);
    }

    default String ToString(Class<?> mainClass){
        var configPath = ConfigAPI.ReturnConfigPath(mainClass, getClass());
        var configObject = new ConfigObject(configPath, documentID());
        var storedData = configObject.HashMap(documentID(), true);
        return storedData.toString();
    }
}
