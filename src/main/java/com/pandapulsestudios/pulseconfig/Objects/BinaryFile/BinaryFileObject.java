package com.pandapulsestudios.pulseconfig.Objects.BinaryFile;

import com.pandapulsestudios.pulsecore.FileSystem.FileAPI;

import java.io.*;

public class BinaryFileObject {
    public boolean saveFlag;
    private boolean outputFlag;
    private FileOutputStream fileOutputStream;
    private FileInputStream fileInputStream;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public BinaryFileObject(String dirPath, String fileName, String extension, boolean outputFlag) throws Exception {
        saveFlag = new File(String.format("%s/%s.%s", dirPath, fileName, extension)).exists();
        this.outputFlag = outputFlag;
        if(outputFlag){
            fileOutputStream = new FileOutputStream(String.format("%s/%s.%s", dirPath, fileName, extension));
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
        }else{
            fileInputStream = new FileInputStream(String.format("%s/%s.%s", dirPath, fileName, extension));
            objectInputStream = new ObjectInputStream(fileInputStream);
        }
    }

    public FileOutputStream FileOutputStream(){
        return fileOutputStream;
    }

    public FileInputStream FileInputStream(){
        return fileInputStream;
    }

    public ObjectOutputStream ObjectOutputStream(){
        return objectOutputStream;
    }

    public ObjectInputStream ObjectInputStream(){
        return objectInputStream;
    }
}
