package com.pandapulsestudios.pulseconfig.Interfaces;

public interface PulseClass {
    default void BeforeLoad(){}
    default void AfterLoad(){}
    default void BeforeSave(){}
    default void AfterSave(){}
}
