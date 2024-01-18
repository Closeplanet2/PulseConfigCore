package com.pandapulsestudios.pulseconfig.Interface;

import com.pandapulsestudios.pulseconfig.APIS.ConfigAPI;
import com.pandapulsestudios.pulseconfig.Object.ConfigObject;
import com.pandapulsestudios.pulseconfig.Serializers.ConfigDeSerializer;
import com.pandapulsestudios.pulseconfig.Serializers.ConfigSerializer;

public interface PandaConfig {
    String documentID();

    default void Save(Class<?> mainClass){ Save(mainClass, true, false); }
    default void Save(Class<?> mainClass, boolean clearConfig){ Save(mainClass, clearConfig, false); }
    default void Save(Class<?> mainClass, boolean clearConfig, boolean debugSave){
        try { SaveRaw(mainClass, clearConfig, debugSave); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    default void SaveRaw(Class<?> mainClass) throws Exception{ SaveRaw(mainClass, true, false); }
    default void SaveRaw(Class<?> mainClass, boolean clearConfig) throws Exception{ SaveRaw(mainClass, clearConfig, false); }
    default void SaveRaw(Class<?> mainClass, boolean clearConfig, boolean debugSave) throws Exception{
        var configObject = new ConfigObject(ConfigAPI.ReturnConfigPath(mainClass, getClass()), documentID());
        if(clearConfig) configObject.ClearConfig();
        ConfigSerializer.SaveConfig(this, configObject, debugSave);
    }

    default void Load(Class<?> mainClass){ Load(mainClass, true, false); }
    default void Load(Class<?> mainClass, boolean clearConfig){ Load(mainClass, clearConfig, false); }
    default void Load(Class<?> mainClass, boolean clearConfig, boolean debugSave){
        try { LoadRaw(mainClass, clearConfig, debugSave); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    default void LoadRaw(Class<?> mainClass) throws Exception{ LoadRaw(mainClass, true, false); }
    default void LoadRaw(Class<?> mainClass, boolean clearConfig) throws Exception{ LoadRaw(mainClass, clearConfig, false); }
    default void LoadRaw(Class<?> mainClass, boolean clearConfig, boolean debugSave) throws Exception {
        var configObject = new ConfigObject(ConfigAPI.ReturnConfigPath(mainClass, getClass()), documentID());
        if(!configObject.saveFlag) SaveRaw(mainClass, clearConfig, debugSave);
        else ConfigDeSerializer.LoadConfig(this, configObject);
    }
}
