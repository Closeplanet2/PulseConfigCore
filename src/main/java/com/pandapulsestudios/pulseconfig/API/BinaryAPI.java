package com.pandapulsestudios.pulseconfig.API;

import com.pandapulsestudios.pulseconfig.Interface.StoragePath;
import com.pandapulsestudios.pulseconfig.Interface.PulseBinary;
import com.pandapulsestudios.pulseconfig.Interface.PulseConfig;
import com.pandapulsestudios.pulseconfig.Serializer.SerializerHelpers;
import com.pandapulsestudios.pulsecore.FileSystem.DirAPI;

import java.io.File;
import java.util.HashMap;

public class BinaryAPI {
    public static HashMap<String, PulseBinary> ReturnAllConfigDocuments(PulseBinary pulseBinary, boolean debug) {
        var data = new HashMap<String, PulseBinary>();
        for(var file : DirAPI.ReturnAllFilesFromDirectory(new File(ReturnBinaryPath(pulseBinary)), false)){
            if(!file.getName().contains(pulseBinary.fileExtension())) continue;
            var fileName = file.getName().replace(pulseBinary.fileExtension(), "");
            var newInstance = SerializerHelpers.CreateInstanceWithID(fileName, pulseBinary.getClass());
            if(newInstance == null) continue;
            var bc = (PulseBinary) newInstance;
            StorageAPI.Load(bc, debug);
            data.put(fileName, bc);
        }
        return data;
    }

    public static String ReturnBinaryPath(PulseBinary pulseBinary){
        if(pulseBinary.getClass().isAnnotationPresent(StoragePath.class)) return String.format("plugins/%s", pulseBinary.getClass().getAnnotation(StoragePath.class).value());
        if(pulseBinary.useSubFolder()) return String.format("plugins/%s/%s", pulseBinary.mainClass().getClass().getSimpleName(), pulseBinary.getClass().getSimpleName());
        return String.format("plugins/%s", pulseBinary.mainClass().getClass().getSimpleName());
    }
}
