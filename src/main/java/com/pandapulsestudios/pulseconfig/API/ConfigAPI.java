package com.pandapulsestudios.pulseconfig.API;

import com.pandapulsestudios.pulseconfig.Enum.StorageType;
import com.pandapulsestudios.pulseconfig.Interface.StoragePath;
import com.pandapulsestudios.pulseconfig.Interface.PulseConfig;
import com.pandapulsestudios.pulseconfig.Serializer.SerializerHelpers;
import com.pandapulsestudios.pulsecore.FileSystem.DirAPI;

import java.io.File;
import java.util.HashMap;

public class ConfigAPI {
    public static HashMap<String, PulseConfig> ReturnAllConfigDocuments(PulseConfig pulseConfig, boolean debug) {
        var data = new HashMap<String, PulseConfig>();
        for(var file : DirAPI.ReturnAllFilesFromDirectory(new File(ReturnConfigPath(pulseConfig)), false)){
            if(!file.getName().contains(".yml")) continue;
            var fileName = file.getName().replace(".yml", "");
            var newInstance = SerializerHelpers.CreateInstanceWithID(fileName, pulseConfig.getClass());
            if(newInstance == null) continue;
            var pc = (PulseConfig) newInstance;
            StorageAPI.Load(pc, debug, StorageType.CONFIG);
            data.put(fileName, pc);
        }
        return data;
    }

    public static String ReturnConfigPath(PulseConfig pulseConfig){
        if(pulseConfig.getClass().isAnnotationPresent(StoragePath.class)) return String.format("plugins/%s", pulseConfig.getClass().getAnnotation(StoragePath.class).value());
        if(pulseConfig.useSubFolder()) return String.format("plugins/%s/%s", pulseConfig.mainClass().getClass().getSimpleName(), pulseConfig.getClass().getSimpleName());
        return String.format("plugins/%s", pulseConfig.mainClass().getClass().getSimpleName());
    }
}
