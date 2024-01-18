package com.pandapulsestudios.pulseconfig.Serializers;

import com.pandapulsestudios.pulseconfig.Interface.PandaClass;
import com.pandapulsestudios.pulseconfig.Interface.PandaConfig;
import com.pandapulsestudios.pulseconfig.Interface.SaveName;
import com.pandapulsestudios.pulseconfig.Object.ConfigObject;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import com.pandapulsestudios.pulsecore.Data.Interface.CustomVariable;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ConfigDeSerializer {
    public static void LoadConfig(PandaConfig pandaConfig, ConfigObject configObject) throws Exception {
        var dataFields = SerializerHelpers.ReturnAllFields(pandaConfig, null, null);
        var storedData = configObject.GetHashMap(pandaConfig.documentID());
        for (var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            field.set(pandaConfig, LoadConfig(dataFields.get(field), storedData.get(fieldName)));
        }
    }

    private static Object LoadConfig(PandaClass pandaClass, ArrayList<Object> storedArrayList) throws Exception {
        if(storedArrayList.isEmpty()) return null;
        var dataFields = SerializerHelpers.ReturnAllFields(null, null, pandaClass);
        for (var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            field.set(pandaClass, LoadConfig(dataFields.get(field), ((HashMap<Object, Object>) storedArrayList.get(0)).get(fieldName)));
        }
        return pandaClass;
    }

    private static HashMap<Object, Object> LoadConfig(HashMap<Object, Object> storedData) throws Exception{
        var returnData = new HashMap<Object, Object>();
        for (var datakey : storedData.keySet()) returnData.put(LoadConfig(datakey, datakey), LoadConfig(storedData.get(datakey), storedData.get(datakey)));
        return returnData;
    }

    private static ArrayList<Object> LoadConfig(ArrayList<Object> storedData) throws Exception {
        var deSerialisedData = new ArrayList<>();
        for(var data: storedData) deSerialisedData.add(LoadConfig(data, data));
        return deSerialisedData;
    }

    private static Object LoadConfig(Object classObject, Object configObject) throws Exception {
        if(classObject instanceof PandaClass){
            return LoadConfig((PandaClass) classObject, (ArrayList<Object>) configObject);
        }else if(classObject instanceof HashMap){
            try{ return LoadConfig((HashMap<Object, Object>) configObject); }
            catch(Exception e){ return LoadConfig((HashMap<Object, Object>) ((ArrayList<Object>) configObject).get(0)); }
        }else if(classObject instanceof ArrayList){
            return LoadConfig((ArrayList<Object>) configObject);
        }else if(classObject instanceof CustomVariable){
            return ((CustomVariable) classObject.getClass().getDeclaredConstructor().newInstance()).DeSerializeData(configObject.toString());
        }else if(ConfigurationSerializable.class.isAssignableFrom(classObject.getClass())){
            return configObject;
        }else if(classObject instanceof Date){
            return SerializerHelpers.SimpleDateFormat.parse(configObject.toString());
        }else{
            var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(classObject.getClass());
            return variableTest == null ? null : variableTest.DeSerializeData(configObject);
        }
    }
}
