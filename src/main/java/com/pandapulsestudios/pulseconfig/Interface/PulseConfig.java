package com.pandapulsestudios.pulseconfig.Interface;

import com.pandapulsestudios.pulseconfig.API.StorageAPI;
import com.pandapulsestudios.pulseconfig.Enum.StorageType;
import org.bukkit.plugin.java.JavaPlugin;

public interface PulseConfig {
    default JavaPlugin mainClass(){return com.pandapulsestudios.pulseconfig.PulseConfig.PulseConfig;}
    default String documentID(){return getClass().getSimpleName();}
    default boolean useSubFolder(){return false;}
    default void FirstLoadConfig(){}
    default void BeforeLoadConfig(){}
    default void AfterLoadConfig(){}
    default void BeforeSaveConfig(){}
    default void AfterSaveConfig(){}
    default void SaveConfig(boolean debug){StorageAPI.Save(this, debug);}
    default void LoadConfig(boolean debug){StorageAPI.Load(this, debug, StorageType.CONFIG);}
    default void DeleteConfig(boolean debug){StorageAPI.Delete(this, debug);}
    default void DisplayConfig(){StorageAPI.Display(this, StorageType.CONFIG);}
}
