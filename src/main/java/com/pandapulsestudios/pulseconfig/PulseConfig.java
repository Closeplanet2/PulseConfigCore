package com.pandapulsestudios.pulseconfig;

import com.pandapulsestudios.pulseconfig.Interface.PulseBinary;
import com.pandapulsestudios.pulseconfig.Interface.PulseJSON;
import com.pandapulsestudios.pulseconfig.Interface.PulseMongo;
import com.pandapulsestudios.pulseconfig.Objects.MongoConnection;
import com.pandapulsestudios.pulseconfig.API.StorageAPI;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.LinkedHashMap;

public final class PulseConfig extends JavaPlugin {
    public static PulseConfig PulseConfig;

    public static LinkedHashMap<Class<?>, com.pandapulsestudios.pulseconfig.Interface.PulseConfig> STATIC_CONFIGS = new LinkedHashMap<>();
    public static LinkedHashMap<Class<?>, PulseMongo> STATIC_MONGO = new LinkedHashMap<>();
    public static LinkedHashMap<Class<?>, PulseJSON> STATIC_JSON = new LinkedHashMap<>();
    public static LinkedHashMap<Class<?>, PulseBinary> STATIC_BINARY= new LinkedHashMap<>();
    public static HashMap<String, MongoConnection> mongoConnections = new HashMap<>();

    @Override
    public void onEnable() {
        PulseConfig = this;
    }
}
