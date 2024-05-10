package com.pandapulsestudios.pulseconfig.Objects;

import com.pandapulsestudios.pulseconfig.API.BinaryAPI;
import com.pandapulsestudios.pulseconfig.API.JSONAPI;
import com.pandapulsestudios.pulseconfig.Interface.PulseJSON;
import com.pandapulsestudios.pulsecore.FileAPI.DirAPI;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class JsonObject {
    private boolean saveFlag;
    private final String configPath;
    private File file;
    private boolean debug;

    public boolean FirstSave(){
        return !saveFlag;
    }

    public JsonObject(PulseJSON pulseJSON, boolean debug){
        configPath = JSONAPI.ReturnBinaryPath(pulseJSON);
        DirAPI.CreateDirectory(new File(configPath));
        var filePath = String.format("%s/%s.json", configPath, pulseJSON.documentID());
        file = new File(filePath);
        saveFlag = file.exists();
        this.debug = debug;
    }

    public void Write(LinkedHashMap<String, Object> data, PulseJSON pulseJSON) throws Exception {
        var fileWriter = new FileWriter(String.format("%s/%s.json", configPath, pulseJSON.documentID()));
        var jsonObject = new JSONObject();
        jsonObject.put(pulseJSON.documentID(), data);
        fileWriter.write(jsonObject.toJSONString());
        fileWriter.close();
    }

    public HashMap<Object, Object> Read(PulseJSON pulseJSON) throws Exception {
        var jsonParser = new JSONParser();
        var fileReader = new FileReader(String.format("%s/%s.json", configPath, pulseJSON.documentID()));
        var jsonObject = (JSONObject) jsonParser.parse(fileReader);
        return (HashMap<Object, Object>) jsonObject.get(pulseJSON.documentID());
    }

    public void DeleteConfig(PulseJSON pulseJSON){
        var file = new File(String.format("%s/%s.json", configPath, pulseJSON.documentID()));
        file.delete();
    }

}
