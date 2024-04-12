package com.pandapulsestudios.pulseconfig.Interface;

import com.pandapulsestudios.pulseconfig.PulseConfig;

public interface PulseClass {
    default void FirstLoadConfig(){}
    default void BeforeLoadConfig(){}
    default void AfterLoadConfig(){}
    default void BeforeSaveConfig(){}
    default void AfterSaveConfig(){}
}
