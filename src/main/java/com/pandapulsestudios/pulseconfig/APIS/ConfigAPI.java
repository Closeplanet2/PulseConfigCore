package com.pandapulsestudios.pulseconfig.APIS;

import com.pandapulsestudios.pulseconfig.Interfaces.ConfigPath;
import com.pandapulsestudios.pulseconfig.Interfaces.PulseConfig;
import com.pandapulsestudios.pulseconfig.Objects.ConfigObject;
import com.pandapulsestudios.pulseconfig.Serializers.ConfigDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializers.ConfigSerializer;
import com.pandapulsestudios.pulseconfig.Serializers.ConsoleSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ConfigAPI {
    public static String ReturnConfigPath(PulseConfig pulseConfig){
        if(pulseConfig.getClass().isAnnotationPresent(ConfigPath.class)){
            var value = pulseConfig.getClass().getAnnotation(ConfigPath.class).value();
            return String.format("plugins/%s", value);
        }

        if(pulseConfig.useSubFolder()){
            return String.format("plugins/%s/%s", pulseConfig.mainClass().getClass().getSimpleName(), pulseConfig.getClass().getSimpleName());
        }

        return String.format("plugins/%s", pulseConfig.mainClass().getClass().getSimpleName());
    }

    public static void Save(PulseConfig pulseConfig, boolean debugSave) throws Exception {
        var configPath = ReturnConfigPath(pulseConfig);
        var configObject = new ConfigObject(configPath, pulseConfig.documentID());
        if(!configObject.saveFlag) pulseConfig.FirstLoad();
        ConfigSerializer.SaveConfig(pulseConfig, configObject, debugSave);
    }

    public static void Load(PulseConfig pulseConfig, boolean debugLoad) throws Exception {
        var configPath = ReturnConfigPath(pulseConfig);
        var configObject = new ConfigObject(configPath, pulseConfig.documentID());
        if(!configObject.saveFlag){
            pulseConfig.FirstLoad();
            Save(pulseConfig, debugLoad);
        }else{
            ConfigDeSerializer.LoadConfig(pulseConfig, configObject, debugLoad);
        }
    }

    public static String ToString(PulseConfig pulseConfig) throws Exception {
        return ConsoleSerializer.ConsoleOutput(pulseConfig);
    }
}
