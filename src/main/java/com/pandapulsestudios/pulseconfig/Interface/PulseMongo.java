package com.pandapulsestudios.pulseconfig.Interface;

import com.pandapulsestudios.pulseconfig.API.StorageAPI;
import com.pandapulsestudios.pulseconfig.Enum.StorageType;

public interface PulseMongo {
    String mongoIP();
    String databaseName();
    default String collectionName(){return "DEFAULT";}
    default String documentID(){return getClass().getSimpleName();}
    default void FirstLoadMongo(){}
    default void BeforeLoadMongo(){}
    default void AfterLoadMongo(){}
    default void BeforeSaveMongo(){}
    default void AfterSaveMongo(){}
    default void SaveMongo(boolean debug){StorageAPI.Save(this, debug);}
    default void LoadMongo(boolean debug){StorageAPI.Load(this, debug, StorageType.MONGO);}
    default void DeleteMongo(boolean debug){StorageAPI.Delete(this, debug);}
    default void DisplayMongo(){StorageAPI.Display(this, StorageType.MONGO);}
}
