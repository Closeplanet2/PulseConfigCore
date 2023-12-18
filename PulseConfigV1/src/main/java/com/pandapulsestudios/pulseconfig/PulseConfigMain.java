package com.pandapulsestudios.pulseconfig;

import com.pandapulsestudios.pulseconfig.Objects.MongoConnection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class PulseConfigMain extends JavaPlugin {
    public static HashMap<String, MongoConnection> MONGO_CONNECTIONS = new HashMap<>();
}
