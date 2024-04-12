package com.pandapulsestudios.pulseconfig.Serializers.BinaryFile;

import com.pandapulsestudios.pulseconfig.Interfaces.BinaryFile.PulseBinaryFile;
import com.pandapulsestudios.pulseconfig.Interfaces.SaveName;
import com.pandapulsestudios.pulseconfig.Objects.BinaryFile.BinaryFileObject;
import com.pandapulsestudios.pulseconfig.Serializers.Config.ConfigDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializers.SerializerHelpers;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;

public class BinaryDeSerializer {
    public static void LoadBinary(PulseBinaryFile pulseBinaryFile, BinaryFileObject binaryFileObject, boolean debugSave) throws Exception {
        pulseBinaryFile.BeforeLoad();

        var storedData = (HashMap<Object, Object>) binaryFileObject.ObjectInputStream().readObject();
        var dataFields = SerializerHelpers.ReturnALlFields(pulseBinaryFile);
        for(var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            if(!storedData.containsKey(fieldName)) field.set(pulseBinaryFile, null);
            else field.set(pulseBinaryFile, ConfigDeSerializer.LoadConfig(dataFields.get(field), storedData.get(fieldName)));
        }

        binaryFileObject.FileInputStream().close();
        binaryFileObject.ObjectInputStream().close();
        pulseBinaryFile.AfterLoad();
    }
}
