package com.pandapulsestudios.pulseconfig.Serializer;

import com.pandapulsestudios.pulseconfig.Interface.DontDefault;
import com.pandapulsestudios.pulseconfig.Interface.DontSave;
import com.pandapulsestudios.pulseconfig.Objects.SaveableArrayList;
import com.pandapulsestudios.pulseconfig.Objects.SaveableHashmap;
import com.pandapulsestudios.pulseconfig.Objects.SaveableLinkedHashMap;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

public class SerializerHelpers {
    public static final java.text.SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd-HH-mm-ss", Locale.ENGLISH);

    public static LinkedHashMap<Field, Object> ReturnALlFields(Class<?> parentClass, Object object) throws IllegalAccessException {
        var data = new LinkedHashMap<Field, Object>();
        for(var field : parentClass.getDeclaredFields()){
            if(field.isAnnotationPresent(DontSave.class)) continue;
            if(Modifier.isStatic(field.getModifiers())) continue;
            var storedData = field.get(object);
            if(storedData == null && !field.isAnnotationPresent(DontDefault.class)){
                var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(field.getType());
                if(field.getType() == SaveableArrayList.class) storedData = new SaveableArrayList<>(Object.class);
                else if(field.getType() == SaveableHashmap.class) storedData = new SaveableHashmap<>(Object.class, Object.class);
                else if(field.getType() == SaveableLinkedHashMap.class) storedData = new SaveableLinkedHashMap<>(Object.class, Object.class);
                else if(field.getType() == Date.class) storedData = new Date();
                else if(variableTest != null) storedData = variableTest.ReturnDefaultValue();
                else storedData = CreateClassInstanceBlank(field.getType());
            }
            if(storedData != null) data.put(field, storedData);
        }
        return data;
    }

    public static Object CreateInstanceWithID(String fileName, Class<?> instanceClass){
        try{
            return instanceClass.getConstructor(String.class).newInstance(fileName);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException ignored) {
            try {
                return instanceClass.getConstructor().newInstance(); }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                return null;
            }
        }
    }

    public static Object CreateClassInstanceBlank(Class<?> instanceClass){
        try{
            return instanceClass.getConstructor().newInstance();
        }catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException ignored) {
            return null;
        }
    }

}
