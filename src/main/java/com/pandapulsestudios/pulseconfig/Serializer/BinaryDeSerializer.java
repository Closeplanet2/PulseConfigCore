package com.pandapulsestudios.pulseconfig.Serializer;

import com.pandapulsestudios.pulseconfig.API.BinaryAPI;
import com.pandapulsestudios.pulseconfig.Interface.PulseBinary;
import com.pandapulsestudios.pulseconfig.Objects.BinaryFileObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

public class BinaryDeSerializer {
    public static void LoadBinary(PulseBinary pulseBinary, BinaryFileObject binaryFileObject) throws IOException, ClassNotFoundException, ParseException, IllegalAccessException {
        binaryFileObject.ChangeFlag(pulseBinary, false);
        pulseBinary.BeforeLoadBinary();
        var storedData = (HashMap<Object, Object>) binaryFileObject.objectInputStream.readObject();
        var data = ConfigDeSerializer.ReturnClassFields((HashMap<Object, Object>) storedData.get(pulseBinary.documentID()), pulseBinary.getClass(), pulseBinary);
        binaryFileObject.fileInputStream.close();
        binaryFileObject.objectInputStream.close();
        pulseBinary.AfterLoadBinary();
        BinarySerializer.SaveBinary(pulseBinary, binaryFileObject);
    }

}
