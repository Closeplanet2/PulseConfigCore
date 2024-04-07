package com.pandapulsestudios.pulseconfig.Objects.Config;

import com.pandapulsestudios.pulseconfig.Interfaces.ConfigComment;
import com.pandapulsestudios.pulseconfig.Interfaces.ConfigHeader;
import com.pandapulsestudios.pulsecore.Chat.ChatAPI;
import com.pandapulsestudios.pulsecore.Chat.MessageType;
import com.pandapulsestudios.pulsecore.FileSystem.FileAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class ConfigObject {
    public boolean saveFlag;
    private FileConfiguration fileConfiguration;
    private File file;

    public ConfigObject(String dirPath, String fileName){
        saveFlag = new File(String.format("%s/%s.yml", dirPath, fileName)).exists();
        file = new File(String.format("%s/%s.yml", dirPath, fileName));
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public void Set(String path, Object value, boolean debugSave){
        fileConfiguration.set(path, value);
        if(debugSave) ChatAPI.chatBuilder().SendMessage("Saving data @ " + path);
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

    public void SetHeader(ConfigHeader configHeader){
        fileConfiguration.options().setHeader(Arrays.asList(configHeader.value()));
        SaveConfig();
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
