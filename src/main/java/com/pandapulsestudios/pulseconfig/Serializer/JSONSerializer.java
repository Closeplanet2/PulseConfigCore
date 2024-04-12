package com.pandapulsestudios.pulseconfig.Serializer;

import com.pandapulsestudios.pulseconfig.Interface.PulseJSON;
import com.pandapulsestudios.pulseconfig.Interface.SaveName;
import com.pandapulsestudios.pulseconfig.Interface.StorageComment;
import com.pandapulsestudios.pulseconfig.Objects.JsonObject;

import java.util.LinkedHashMap;

public class JSONSerializer {
    public static void SaveJSON(PulseJSON pulseJson, JsonObject jsonObject) throws Exception {
        pulseJson.BeforeSaveJSON();
        jsonObject.Write(MongoSerializer.ReturnClassFields(pulseJson.getClass(), pulseJson), pulseJson);
        pulseJson.AfterSaveJSON();
    }
}
