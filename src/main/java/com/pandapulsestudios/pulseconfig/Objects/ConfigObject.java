package com.pandapulsestudios.pulseconfig.Objects;

import com.pandapulsestudios.pulsecore.Chat.ChatAPI;
import com.pandapulsestudios.pulsecore.Chat.MessageType;
import com.pandapulsestudios.pulsecore.FileSystem.FileAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ConfigObject {
    public boolean saveFlag;
    private FileConfiguration fileConfiguration;
    private File file;

    public ConfigObject(String dirPath, String fileName){
        saveFlag = FileAPI.Exist(dirPath, fileName, ".yml");
        file = FileAPI.Create(dirPath, fileName, ".yml");
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public void Set(String path, Object value, boolean debugSave){
        fileConfiguration.set(path, value);
        if(debugSave) ChatAPI.SendChat("Saving data @ " + path, MessageType.ConsoleMessageNormal, true, null);
        SaveConfig();
    }

    public Object Get(String path){
        return fileConfiguration.get(path);
    }

    public HashMap<Object, Object> HashMap(String path, boolean deepDive){
        var data = new HashMap<Object, Object>();
        if(!fileConfiguration.isConfigurationSection(path)) return data;
        for(var key : fileConfiguration.getConfigurationSection(path).getKeys(false)){
            var fullPath = String.format("%s.%s", path, key);
            if(!fileConfiguration.isConfigurationSection(fullPath)) data.put(key, Get(fullPath));
            else if(fileConfiguration.isConfigurationSection(fullPath) && deepDive) data.put(key, HashMap(fullPath, deepDive));
        }
        return data;
    }

    public void SaveConfig(){
        try { fileConfiguration.save(file); }
        catch (IOException e) { e.printStackTrace();}
    }

    public void DeleteConfig(){
        file.delete();
    }

    public void ClearConfig(){
        for(var s : fileConfiguration.getKeys(false)){
            Set(s, null, false);
        }
    }
}
