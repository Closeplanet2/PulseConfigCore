package com.pandapulsestudios.pulseconfig.Serializer;

import com.pandapulsestudios.pulseconfig.Interface.*;
import com.pandapulsestudios.pulseconfig.Objects.*;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

public class BinarySerializer {
    public static void SaveBinary(PulseBinary pulseBinary, BinaryFileObject binaryFileObject) throws IllegalAccessException, IOException {
        pulseBinary.BeforeSaveBinary();
        binaryFileObject.ChangeFlag(pulseBinary, true);
        var storedData = new LinkedHashMap<>();
        storedData.put(pulseBinary.documentID(), ConfigSerializer.ReturnClassFields(pulseBinary.getClass(), pulseBinary));
        binaryFileObject.objectOutputStream.writeObject(storedData);
        binaryFileObject.fileOutputStream.close();
        binaryFileObject.objectOutputStream.close();
        pulseBinary.AfterSaveBinary();
    }
}
