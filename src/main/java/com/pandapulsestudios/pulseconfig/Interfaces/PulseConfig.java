package com.pandapulsestudios.pulseconfig.Interfaces;

import com.pandapulsestudios.pulseconfig.APIS.ConfigAPI;
import com.pandapulsestudios.pulseconfig.Objects.ConfigObject;
import org.bukkit.plugin.java.JavaPlugin;

public interface PulseConfig {
    String documentID();
    JavaPlugin mainClass();
    boolean useSubFolder();
    default void FirstLoad(){}
    default void BeforeLoad(){}
    default void AfterLoad(){}
    default void BeforeSave(){}
    default void AfterSave(){}
}
