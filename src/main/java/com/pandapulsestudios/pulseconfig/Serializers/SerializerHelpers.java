package com.pandapulsestudios.pulseconfig.Serializers;

import com.pandapulsestudios.pulseconfig.Interfaces.*;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import com.pandapulsestudios.pulsecore.Data.Interface.CustomVariable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

public class SerializerHelpers {
    public static final java.text.SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd-HH-mm-ss", Locale.ENGLISH);
    public static final String MongoID = "MongoID";

    public static LinkedHashMap<Field, Object> ReturnAllFields(PulseConfig pandaConfig, PulseMongo pandaMongo, PulseClass pandaClass) throws Exception {
        var data = new LinkedHashMap<Field, Object>();
        Object clazz = pandaConfig != null ? pandaConfig : pandaMongo != null ? pandaMongo : pandaClass;
        for(var field : pandaConfig != null ? pandaConfig.getClass().getDeclaredFields() : pandaMongo != null ? pandaMongo.getClass().getDeclaredFields() : pandaClass.getClass().getDeclaredFields()){
            if (field.isAnnotationPresent(IgnoreSave.class)) continue;
            var variable = field.get(clazz);
            if(variable == null && !field.isAnnotationPresent(DontDefault.class)){
                var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(field.getType());
                if(field.getType() == Date.class) variable = new Date();
                else if(variableTest != null) variable = variableTest.ReturnDefaultValue();
            }
            if(variable == null) continue;
            data.put(field, variable);
        }
        return data;
    }

    public static ArrayList<Object> StoreHashMapInListA(HashMap<String, Object> data){
        var arrayList = new ArrayList<Object>();
        arrayList.add(data);
        return arrayList;
    }

    public static ArrayList<Object> StoreHashMapInListB(HashMap<Object, Object> data){
        var arrayList = new ArrayList<Object>();
        arrayList.add(data);
        return arrayList;
    }
}
