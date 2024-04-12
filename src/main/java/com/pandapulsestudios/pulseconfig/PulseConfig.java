package com.pandapulsestudios.pulseconfig;

import com.pandapulsestudios.pulseconfig.Objects.MongoConnection;
import com.pandapulsestudios.pulseconfig.API.StorageAPI;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.LinkedHashMap;

public final class PulseConfig extends JavaPlugin {
    public static PulseConfig PulseConfig;

    public static LinkedHashMap<Class<?>, Object> staticConfigs = new LinkedHashMap<>();
    public static HashMap<String, MongoConnection> mongoConnections = new HashMap<>();

    @Override
    public void onEnable() {
        PulseConfig = this;
    }
}
