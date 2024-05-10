package com.pandapulsestudios.pulseconfig.API;

import com.pandapulsestudios.pulseconfig.Enum.StorageType;
import com.pandapulsestudios.pulseconfig.Interface.PulseBinary;
import com.pandapulsestudios.pulseconfig.Interface.PulseJSON;
import com.pandapulsestudios.pulseconfig.Interface.StoragePath;
import com.pandapulsestudios.pulseconfig.Serializer.SerializerHelpers;
import com.pandapulsestudios.pulsecore.FileAPI.DirAPI;

import java.io.File;
import java.util.HashMap;

public class JSONAPI {
    public static HashMap<String, PulseJSON> ReturnAllConfigDocuments(PulseJSON pulseJSON, boolean debug) {
        var data = new HashMap<String, PulseJSON>();
        for(var file : DirAPI.ReturnAllFilesFromDirectory(new File(ReturnBinaryPath(pulseJSON)), false)){
            if(!file.getName().contains(".json")) continue;
            var fileName = file.getName().replace(".json", "");
            var newInstance = SerializerHelpers.CreateInstanceWithID(fileName, pulseJSON.getClass());
            if(newInstance == null) continue;
            var pj = (PulseJSON) newInstance;
            StorageAPI.Load(pj, debug, StorageType.JSON);
            data.put(fileName, pj);
        }
        return data;
    }

    public static String ReturnBinaryPath(PulseJSON pulseJSON){
        if(pulseJSON.getClass().isAnnotationPresent(StoragePath.class)) return String.format("plugins/%s", pulseJSON.getClass().getAnnotation(StoragePath.class).value());
        if(pulseJSON.useSubFolder()) return String.format("plugins/%s/%s", pulseJSON.mainClass().getClass().getSimpleName(), pulseJSON.getClass().getSimpleName());
        return String.format("plugins/%s", pulseJSON.mainClass().getClass().getSimpleName());
    }
}
