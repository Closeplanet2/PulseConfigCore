package com.pandapulsestudios.pulseconfig.Object;

import com.pandapulsestudios.pulsecore.FileSystem.FileAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigObject {
    public final boolean saveFlag;
    private final File file;
    private final FileConfiguration fileConfiguration;

    public ConfigObject(String dirPath, String fileName){
        saveFlag = FileAPI.Exist(dirPath, fileName, ".yml");
        file = FileAPI.Create(dirPath, fileName, ".yml");
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    private void SaveConfig(){
        try { fileConfiguration.save(file); }
        catch (IOException e) { e.printStackTrace();
        }
    }

    public void ClearConfig(){
        for(var s : fileConfiguration.getKeys(false)) SetData(s, null, false);
    }

    public void SetHeader(String... comments){
        fileConfiguration.options().setHeader(Arrays.stream(comments).toList());
        SaveConfig();
    }

    public void SetData(String path, Object value, boolean debugSave){
        fileConfiguration.set(path, value);
        SaveConfig();
    }

    public HashMap<Object, Object> GetHashMap(String path){
        var array = fileConfiguration.getMapList(path);
        if(array.size() == 0) return new HashMap<>();
        return (HashMap<Object, Object>) array.get(0);
    }
}
