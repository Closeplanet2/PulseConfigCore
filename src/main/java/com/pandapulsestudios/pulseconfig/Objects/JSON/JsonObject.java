package com.pandapulsestudios.pulseconfig.Objects.JSON;

import com.pandapulsestudios.pulsecore.FileSystem.FileAPI;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class JsonObject {

    public boolean saveFlag;
    private final String documentID;
    private final String dirPath;

    public JsonObject(String documentID, String dirPath) throws Exception {
        saveFlag = FileAPI.Exist(dirPath, String.format("%s.json", documentID));
        this.documentID = documentID;
        this.dirPath = dirPath;
    }

    public void Write(HashMap<Object, Object> data) throws Exception {
        var fileWriter = new FileWriter(String.format("%s/%s.json", dirPath, documentID));
        var jsonObject = new JSONObject();
        jsonObject.put(documentID, data);
        fileWriter.write(jsonObject.toJSONString());
        fileWriter.close();
    }

    public HashMap<Object, Object> Read() throws Exception {
        var jsonParser = new JSONParser();
        var fileReader = new FileReader(String.format("%s/%s.json", dirPath, documentID));
        var jsonObject = (JSONObject) jsonParser.parse(fileReader);
        return (HashMap<Object, Object>) jsonObject.get(documentID);
    }

    public void DeleteConfig(){
        var file = new File(String.format("%s/%s.json", dirPath, documentID));
        file.delete();
    }
}
