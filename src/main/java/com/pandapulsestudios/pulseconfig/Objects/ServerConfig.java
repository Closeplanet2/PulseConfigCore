package com.pandapulsestudios.pulseconfig.Objects;

import com.pandapulsestudios.pulsecore.Files.FileAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ServerConfig {
    public final boolean saveFlag;
    private final File file;
    private final FileConfiguration fileConfiguration;

    public ServerConfig(String dirPath, String fileName){
        saveFlag = FileAPI.Exist(dirPath, fileName, ".yml");
        file = FileAPI.Create(dirPath, fileName, ".yml");
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    private void SaveConfig(){
        try { fileConfiguration.save(file); }
        catch (IOException e) { e.printStackTrace(); }
    }

    public void SetHeader(String... comments){
        fileConfiguration.options().setHeader(Arrays.stream(comments).toList());
        SaveConfig();
    }

    public void SetData(String path, Object value){
        fileConfiguration.set(path, value);
        SaveConfig();
    }

    public Object Get(String path){
        return fileConfiguration.get(path);
    }

    public List<Object> GetList(String path){
        if(fileConfiguration.getList(path) == null) return new ArrayList<>();
        return new ArrayList<>(fileConfiguration.getList(path));
    }

    public HashMap<Object, Object> GetHashMap(String path){
        var array = fileConfiguration.getMapList(path);
        if(array.size() == 0) return new HashMap<>();
        return (HashMap<Object, Object>) array.get(0);
    }

    public ConfigurationSection GetConfigurationSection(String path){
        return fileConfiguration.getConfigurationSection(path);
    }

    public boolean IsConfigurationSection(String path){
        return fileConfiguration.isConfigurationSection(path);
    }
}
