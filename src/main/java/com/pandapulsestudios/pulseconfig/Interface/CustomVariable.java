package com.pandapulsestudios.pulseconfig.Interface;

import com.pandapulsestudios.pulseconfig.Objects.SaveableHashmap;

import java.util.HashMap;

public interface CustomVariable {
    SaveableHashmap<Integer, Object> SerializeData();
    void DeSerializeData(HashMap<Integer, Object> configData);
    default void BeforeLoadConfig(){}
    default void AfterLoadConfig(){}
    default void BeforeSaveConfig(){}
    default void AfterSaveConfig(){}
    default void BeforeLoadMongo(){}
    default void AfterLoadMongo(){}
    default void BeforeSaveMongo(){}
    default void AfterSaveMongo(){}
}
