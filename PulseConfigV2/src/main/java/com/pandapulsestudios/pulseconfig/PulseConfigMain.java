package com.pandapulsestudios.pulseconfig;

import com.pandapulsestudios.pulseconfig.APIS.MongoAPI;
import com.pandapulsestudios.pulseconfig.Object.MongoObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public final class PulseConfigMain extends JavaPlugin {
    public static HashMap<String, MongoObject> MONGO_CONNECTIONS = new HashMap<>();
}
