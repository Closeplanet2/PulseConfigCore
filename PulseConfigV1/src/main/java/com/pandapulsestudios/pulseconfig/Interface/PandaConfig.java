package com.pandapulsestudios.pulseconfig.Interface;

import com.mongodb.client.MongoCollection;
import com.pandapulsestudios.pulseconfig.Helpers.Serializer;
import com.pandapulsestudios.pulseconfig.Mongo.MongoAPI;
import com.pandapulsestudios.pulseconfig.Objects.MongoConnection;
import com.pandapulsestudios.pulseconfig.Objects.ServerConfig;
import com.pandapulsestudios.pulseconfig.PulseConfigMain;
import com.pandapulsestudios.pulsecore.Console.ConsoleAPI;
import org.bson.Document;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public interface PandaConfig {
    String CONFIG_ID();
    Class<?> CLASS_TYPE();
    default Class<?> MAIN_CLASS(){ return PulseConfigMain.class; }
    default String CONFIG_PATH(){ return String.format("plugins/__CONFIGS__/%s/%s/", MAIN_CLASS().getSimpleName(), CLASS_TYPE() == null ? "" : CLASS_TYPE().getSimpleName()); }

    default ServerConfig RETURN_CONFIG() throws Exception {
        var serverConfig = new ServerConfig(CONFIG_PATH(), CONFIG_ID());
        if(!serverConfig.saveFlag) SAVE();
        return serverConfig;
    }

    default void SAVE(){
        try { SAVE_RAW(); }
        catch (Exception exception) { exception.printStackTrace(); }
    }

    default void LOAD(){
        try { LOAD_RAW(); }
        catch (Exception exception) { exception.printStackTrace(); }
    }

    default void SAVE_RAW() throws Exception {
        Serializer.SAVE(this, null, RETURN_CONFIG(), "");

    }
    default void LOAD_RAW() throws Exception {
        if(!RETURN_CONFIG().saveFlag) SAVE_RAW();
        var data = Serializer.LOAD(this, null, RETURN_CONFIG(), "");
    }
}
