package com.pandapulsestudios.pulseconfig.Serializers.JSON;

import com.pandapulsestudios.pulseconfig.Interfaces.JSON.PulseJson;
import com.pandapulsestudios.pulseconfig.Interfaces.SaveName;
import com.pandapulsestudios.pulseconfig.Objects.JSON.JsonObject;
import com.pandapulsestudios.pulseconfig.Serializers.Config.ConfigDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializers.Config.ConfigSerializer;
import com.pandapulsestudios.pulseconfig.Serializers.SerializerHelpers;

import java.util.LinkedHashMap;

public class JSONDeSerializer {
    public static void LoadJSON(PulseJson pulseJson, JsonObject jsonObject, boolean debugSave) throws Exception {
        pulseJson.BeforeLoad();

        var storedData = jsonObject.Read();
        var dataFields = SerializerHelpers.ReturnALlFields(pulseJson);
        for(var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            if(!storedData.containsKey(fieldName)) field.set(pulseJson, null);
            else field.set(pulseJson, ConfigDeSerializer.LoadConfig(dataFields.get(field), storedData.get(fieldName)));
        }

        pulseJson.AfterLoad();
    }
}
