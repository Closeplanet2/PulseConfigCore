package com.pandapulsestudios.pulseconfig.Objects;

import com.pandapulsestudios.pulseconfig.API.BinaryAPI;
import com.pandapulsestudios.pulseconfig.Interface.PulseBinary;
import com.pandapulsestudios.pulsecore.FileSystem.DirAPI;
import com.pandapulsestudios.pulsecore.FileSystem.FileAPI;

import java.io.*;

public class BinaryFileObject {
    private boolean saveFlag;
    private File file;
    private boolean debug;
    public FileOutputStream fileOutputStream;
    public FileInputStream fileInputStream;
    public ObjectOutputStream objectOutputStream;
    public ObjectInputStream objectInputStream;

    public boolean FirstSave(){
        return !saveFlag;
    }

    public BinaryFileObject(PulseBinary pulseBinary, boolean debug) throws IOException {
        var configPath = BinaryAPI.ReturnBinaryPath(pulseBinary);
        DirAPI.CreateDirectory(new File(configPath));
        var filePath = String.format("%s/%s.%s", configPath, pulseBinary.documentID(), pulseBinary.fileExtension());
        file = new File(filePath);
        saveFlag = file.exists();
        this.debug = debug;
    }

    public void ChangeFlag(PulseBinary pulseBinary, boolean outputFlag) throws IOException {
        var configPath = BinaryAPI.ReturnBinaryPath(pulseBinary);
        if(outputFlag){
            fileOutputStream = new FileOutputStream(String.format("%s/%s.%s", configPath, pulseBinary.documentID(), pulseBinary.fileExtension()));
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
        }else{
            fileInputStream = new FileInputStream(String.format("%s/%s.%s", configPath, pulseBinary.documentID(), pulseBinary.fileExtension()));
            objectInputStream = new ObjectInputStream(fileInputStream);
        }
    }

    public void Delete(PulseBinary pulseBinary){
        var file = new File(String.format("%s/%s.%s", BinaryAPI.ReturnBinaryPath(pulseBinary), pulseBinary.documentID(), pulseBinary.fileExtension()));
        file.delete();
    }
}
