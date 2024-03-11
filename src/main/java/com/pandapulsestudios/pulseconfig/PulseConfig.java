package com.pandapulsestudios.pulseconfig;

import com.pandapulsestudios.pulseconfig.APIS.BinaryFileAPI;
import com.pandapulsestudios.pulseconfig.APIS.ConfigAPI;
import com.pandapulsestudios.pulseconfig.APIS.JSONAPI;
import com.pandapulsestudios.pulseconfig.Objects.Mogno.MongoConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class PulseConfig extends JavaPlugin {
    public static PulseConfig Instance;
    public static HashMap<String, MongoConnection> MongoConnections = new HashMap<>();

    @Override
    public void onEnable() {
        Instance = this;
    }
}
