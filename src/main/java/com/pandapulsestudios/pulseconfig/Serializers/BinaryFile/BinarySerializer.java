package com.pandapulsestudios.pulseconfig.Serializers.BinaryFile;

import com.pandapulsestudios.pulseconfig.Interfaces.BinaryFile.PulseBinaryFile;
import com.pandapulsestudios.pulseconfig.Objects.BinaryFile.BinaryFileObject;
import com.pandapulsestudios.pulseconfig.Serializers.Config.ConfigSerializer;
import com.pandapulsestudios.pulseconfig.Serializers.SerializerHelpers;

import java.util.*;

public class BinarySerializer {
    public static void SaveBinary(PulseBinaryFile pulseBinaryFile, BinaryFileObject binaryFileObject, boolean debugSave) throws Exception {
        pulseBinaryFile.BeforeSave();
        var storedData = new LinkedHashMap<>();

        var dataFields = SerializerHelpers.ReturnALlFields(pulseBinaryFile);
        for(var field : dataFields.keySet()){
            var fieldName = SerializerHelpers.ReturnFieldName(field);
            storedData.put(fieldName, ConfigSerializer.SaveConfigSingle(dataFields.get(field)));
        }

        binaryFileObject.ObjectOutputStream().writeObject(storedData);
        binaryFileObject.FileOutputStream().close();
        binaryFileObject.ObjectOutputStream().close();
        pulseBinaryFile.AfterSave();
    }
}
