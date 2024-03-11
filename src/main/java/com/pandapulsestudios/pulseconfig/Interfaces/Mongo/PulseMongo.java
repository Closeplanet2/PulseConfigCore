package com.pandapulsestudios.pulseconfig.Interfaces.Mongo;

public interface PulseMongo {
    String databaseName();
    String documentID();
    default void FirstLoad(){}
    default void BeforeLoad(){}
    default void AfterLoad(){}
    default void BeforeSave(){}
    default void AfterSave(){}
}
