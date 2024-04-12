package com.pandapulsestudios.pulseconfig.Interfaces.BinaryFile;

import org.bukkit.plugin.java.JavaPlugin;

public interface PulseBinaryFile {
    String documentID();
    String fileExtension();
    JavaPlugin mainClass();
    boolean useSubFolder();
    default void FirstLoad(){}
    default void BeforeLoad(){}
    default void AfterLoad(){}
    default void BeforeSave(){}
    default void AfterSave(){}
}
