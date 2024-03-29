package com.pandapulsestudios.pulseconfig.APIS;

import com.pandapulsestudios.pulseconfig.Interfaces.BinaryFile.PulseBinaryFile;
import com.pandapulsestudios.pulseconfig.Interfaces.Config.PulseConfig;
import com.pandapulsestudios.pulseconfig.Interfaces.ConfigPath;
import com.pandapulsestudios.pulseconfig.Objects.BinaryFile.BinaryFileObject;
import com.pandapulsestudios.pulseconfig.Serializers.BinaryFile.BinaryDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializers.BinaryFile.BinarySerializer;
import com.pandapulsestudios.pulsecore.FileSystem.DirAPI;
import com.pandapulsestudios.pulsecore.FileSystem.FileAPI;

import java.io.*;

public class BinaryFileAPI {
    public static String ReturnConfigPath(PulseBinaryFile pulseBinaryFile){
        if(pulseBinaryFile.getClass().isAnnotationPresent(ConfigPath.class)){
            var value = pulseBinaryFile.getClass().getAnnotation(ConfigPath.class);
            return String.format("plugins/%s", value);
        }

        if(pulseBinaryFile.useSubFolder()){
            return String.format("plugins/%s/%s", pulseBinaryFile.mainClass().getClass().getSimpleName(), pulseBinaryFile.getClass().getSimpleName());
        }

        return String.format("plugins/%s", pulseBinaryFile.mainClass().getClass().getSimpleName());
    }

    public static void Save(PulseBinaryFile pulseBinaryFile, boolean debugSave){
        try {SaveRaw(pulseBinaryFile, debugSave);}
        catch (Exception e) {throw new RuntimeException(e);}
    }

    public static void SaveRaw(PulseBinaryFile pulseBinaryFile, boolean debugSave) throws Exception {
        var configPath = ReturnConfigPath(pulseBinaryFile);
        DirAPI.CreateDirectory(new File(configPath));
        var binaryFileObject = new BinaryFileObject(configPath, pulseBinaryFile.documentID(), pulseBinaryFile.fileExtension(), true);
        if(!binaryFileObject.saveFlag) pulseBinaryFile.FirstLoad();
        BinarySerializer.SaveBinary(pulseBinaryFile, binaryFileObject, debugSave);
    }

    public static void Load(PulseBinaryFile pulseBinaryFile, boolean debugSave){
        try {LoadRaw(pulseBinaryFile, debugSave);}
        catch (Exception e) {throw new RuntimeException(e);}
    }

    public static void LoadRaw(PulseBinaryFile pulseBinaryFile, boolean debugSave) throws Exception {
        var configPath = ReturnConfigPath(pulseBinaryFile);
        DirAPI.CreateDirectory(new File(configPath));
        var binaryFileObject = new BinaryFileObject(configPath, pulseBinaryFile.documentID(), pulseBinaryFile.fileExtension(), false);
        if(!binaryFileObject.saveFlag){
            pulseBinaryFile.FirstLoad();
            SaveRaw(pulseBinaryFile, debugSave);
        }else{
            BinaryDeSerializer.LoadBinary(pulseBinaryFile, binaryFileObject, debugSave);
        }
    }

    public static void Delete(PulseBinaryFile pulseBinaryFile) {
        var configPath = ReturnConfigPath(pulseBinaryFile);
        FileAPI.DeleteFile(new File(String.format("%s/%s", configPath, pulseBinaryFile.documentID())));
    }
}
