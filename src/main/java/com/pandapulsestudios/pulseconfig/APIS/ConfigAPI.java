package com.pandapulsestudios.pulseconfig.APIS;

import org.bukkit.plugin.java.JavaPlugin;

public class ConfigAPI {
    public static String ReturnConfigPath(Class<?> mainClass, Class<?> subClass){
        return String.format("plugins/%s/Configs/%s", mainClass.getSimpleName(), subClass.getSimpleName());
    }
}
