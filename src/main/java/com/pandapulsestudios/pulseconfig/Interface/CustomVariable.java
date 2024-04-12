package com.pandapulsestudios.pulseconfig.Interface;

import com.pandapulsestudios.pulseconfig.Objects.SaveableHashmap;

import java.util.HashMap;

public interface CustomVariable {
    SaveableHashmap<Object, Object> SerializeData();
    void DeSerializeData(HashMap<Object, Object> configData);
    default void BeforeLoad(){}
    default void AfterLoad(){}
    default void BeforeSave(){}
    default void AfterSave(){}
}
