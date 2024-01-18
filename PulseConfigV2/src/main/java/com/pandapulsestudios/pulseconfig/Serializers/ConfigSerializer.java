package com.pandapulsestudios.pulseconfig.Serializers;

import com.pandapulsestudios.pulseconfig.Interface.PandaClass;
import com.pandapulsestudios.pulseconfig.Interface.PandaConfig;
import com.pandapulsestudios.pulseconfig.Interface.PandaMongo;
import com.pandapulsestudios.pulseconfig.Interface.SaveName;
import com.pandapulsestudios.pulseconfig.Object.ConfigObject;
import com.pandapulsestudios.pulseconfig.Object.MongoObject;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import com.pandapulsestudios.pulsecore.Data.Interface.CustomVariable;
import org.bson.Document;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class ConfigSerializer {
    public static void SaveConfig(PandaConfig pandaConfig, ConfigObject configObject, boolean debugSave) throws Exception {
        var storedData = new HashMap<String, Object>();
        var dataFields = SerializerHelpers.ReturnAllFields(pandaConfig, null, null);
        for (var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            storedData.put(fieldName, SaveConfig(dataFields.get(field), false));
        }
        configObject.SetData(pandaConfig.documentID(), SerializerHelpers.StoreHashMapInListA(storedData), debugSave);
    }

    private static Object SaveConfig(PandaClass pandaClass, boolean addPandaClassToArray) throws Exception {
        var storedData = new HashMap<String, Object>();
        var dataFields = SerializerHelpers.ReturnAllFields(null, null, pandaClass);
        for (var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            storedData.put(fieldName, SaveConfig(dataFields.get(field), false));
        }
        return addPandaClassToArray ? SerializerHelpers.StoreHashMapInListA(storedData) : storedData;
    }

    private static ArrayList<Object> SaveConfig(HashMap<Object, Object> currentData) throws Exception{
        var returnData = new HashMap<>();
        for (var datakey : currentData.keySet())  returnData.put(SaveConfig(datakey, false), SaveConfig(currentData.get(datakey), false));
        return SerializerHelpers.StoreHashMapInListB(returnData);
    }

    private static ArrayList<Object> SaveConfig(ArrayList<Object> currentData) throws Exception {
        var returnData = new ArrayList<>();
        for (var data : currentData) returnData.add(SaveConfig(data, false));
        return returnData;
    }

    private static Object SaveConfig(Object data, boolean addPandaClassToArray) throws Exception {
        if(data instanceof PandaClass) return SaveConfig((PandaClass) data, addPandaClassToArray);
        else if(data instanceof HashMap) return SaveConfig((HashMap<Object, Object>) data);
        else if(data instanceof ArrayList) return SaveConfig((ArrayList<Object>) data);
        else if(data instanceof CustomVariable) return ((CustomVariable) data).SerializeData();
        else if(ConfigurationSerializable.class.isAssignableFrom(data.getClass())) return data;
        else if(data instanceof Date) return SerializerHelpers.SimpleDateFormat.format((Date) data);
        else{
            var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(data.getClass());
            return variableTest == null ? null : variableTest.SerializeData(data);
        }
    }


}
