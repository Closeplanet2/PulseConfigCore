package com.pandapulsestudios.pulseconfig.APIS;

import com.pandapulsestudios.pulseconfig.Interfaces.BinaryFile.PulseBinaryFile;
import com.pandapulsestudios.pulseconfig.Interfaces.Config.PulseConfig;
import com.pandapulsestudios.pulseconfig.Interfaces.ConfigPath;
import com.pandapulsestudios.pulseconfig.Interfaces.JSON.PulseJson;
import com.pandapulsestudios.pulseconfig.Objects.Config.ConfigObject;
import com.pandapulsestudios.pulseconfig.Objects.JSON.JsonObject;
import com.pandapulsestudios.pulseconfig.Serializers.JSON.JSONDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializers.JSON.JSONSerializer;
import com.pandapulsestudios.pulsecore.FileSystem.DirAPI;

public class JSONAPI {
    public static String ReturnConfigPath(PulseJson pulseJson){
        if(pulseJson.getClass().isAnnotationPresent(ConfigPath.class)){
            var value = pulseJson.getClass().getAnnotation(ConfigPath.class);
            return String.format("plugins/%s", value);
        }

        if(pulseJson.useSubFolder()){
            return String.format("plugins/%s/%s", pulseJson.mainClass().getClass().getSimpleName(), pulseJson.getClass().getSimpleName());
        }

        return String.format("plugins/%s", pulseJson.mainClass().getClass().getSimpleName());
    }

    public static void Save(PulseJson pulseJson, boolean debugLoad) {
        try { SaveRaw(pulseJson, debugLoad); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    public static void SaveRaw(PulseJson pulseJson, boolean debugSave) throws Exception {
        var configPath = ReturnConfigPath(pulseJson);
        DirAPI.Create(configPath);
        var jsonObject = new JsonObject(pulseJson.documentID(), configPath);
        if(!jsonObject.saveFlag) pulseJson.FirstLoad();
        JSONSerializer.SaveJSON(pulseJson, jsonObject, debugSave);
    }

    public static void Load(PulseJson pulseJson, boolean debugLoad) {
        try { LoadRaw(pulseJson, debugLoad); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    public static void LoadRaw(PulseJson pulseJson, boolean debugSave) throws Exception {
        var configPath = ReturnConfigPath(pulseJson);
        DirAPI.Create(configPath);
        var jsonObject = new JsonObject(pulseJson.documentID(), configPath);
        if(!jsonObject.saveFlag){
            pulseJson.FirstLoad();
            SaveRaw(pulseJson, debugSave);
        }else{
            JSONDeSerializer.LoadJSON(pulseJson, jsonObject, debugSave);
        }
    }

    public static void Delete(PulseJson pulseJson) throws Exception {
        var configPath = ReturnConfigPath(pulseJson);
        var jsonObject = new JsonObject(pulseJson.documentID(), configPath);
        jsonObject.DeleteConfig();
    }
}
