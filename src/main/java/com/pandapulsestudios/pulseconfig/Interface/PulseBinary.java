package com.pandapulsestudios.pulseconfig.Interface;

import com.pandapulsestudios.pulseconfig.API.StorageAPI;
import com.pandapulsestudios.pulseconfig.Enum.StorageType;
import org.bukkit.plugin.java.JavaPlugin;

public interface PulseBinary {
    String fileExtension();
    default JavaPlugin mainClass(){return com.pandapulsestudios.pulseconfig.PulseConfig.PulseConfig;}
    default String documentID(){return getClass().getSimpleName();}
    default boolean useSubFolder(){return false;}
    default void FirstLoadBinary(){}
    default void BeforeLoadBinary(){}
    default void AfterLoadBinary(){}
    default void BeforeSaveBinary(){}
    default void AfterSaveBinary(){}
    default void SaveBinary(boolean debug){ StorageAPI.Save(this, debug);}
    default void LoadBinary(boolean debug){ StorageAPI.Load(this, debug, StorageType.BINARY);}
    default void DeleteBinary(boolean debug){ StorageAPI.Delete(this, debug);}
    default void DisplayBinary(){ StorageAPI.Display(this, StorageType.BINARY);}
}
