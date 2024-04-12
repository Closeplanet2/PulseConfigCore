package com.pandapulsestudios.pulseconfig.Interface;

import com.pandapulsestudios.pulseconfig.API.StorageAPI;
import com.pandapulsestudios.pulseconfig.Enum.StorageType;
import org.bukkit.plugin.java.JavaPlugin;

public interface PulseJSON {
    default JavaPlugin mainClass(){return com.pandapulsestudios.pulseconfig.PulseConfig.PulseConfig;}
    default String documentID(){return getClass().getSimpleName();}
    default boolean useSubFolder(){return false;}
    default void FirstLoadJSON(){}
    default void BeforeLoadJSON(){}
    default void AfterLoadJSON(){}
    default void BeforeSaveJSON(){}
    default void AfterSaveJSON(){}
    default void SaveJSON(boolean debug){ StorageAPI.Save(this, debug);}
    default void LoadJSON(boolean debug){ StorageAPI.Load(this, debug, StorageType.JSON);}
    default void DeleteJSON(boolean debug){ StorageAPI.Delete(this, debug);}
    default void DisplayJSON(){ StorageAPI.Display(this, StorageType.JSON);}
}
