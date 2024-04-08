package com.pandapulsestudios.pulseconfig.Serializers;

import com.pandapulsestudios.pulseconfig.Interfaces.*;
import com.pandapulsestudios.pulseconfig.Interfaces.BinaryFile.PulseBinaryFile;
import com.pandapulsestudios.pulseconfig.Interfaces.JSON.PulseJson;
import com.pandapulsestudios.pulseconfig.Interfaces.Mongo.PulseMongo;
import com.pandapulsestudios.pulseconfig.Interfaces.PulseClass;
import com.pandapulsestudios.pulseconfig.Interfaces.Config.PulseConfig;
import com.pandapulsestudios.pulsecore.Data.API.VariableAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.*;

public class SerializerHelpers {

    public static final java.text.SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd-HH-mm-ss", Locale.ENGLISH);

    public static String ReturnFieldName(Field field){
        var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
        return field.isAnnotationPresent(ConfigComment.class) ? String.format("#%s\n%s", field.getAnnotation(ConfigComment.class).value(), fieldName) : fieldName;
    }

    public static LinkedHashMap<Field, Object> ReturnALlFields(PulseConfig pulseConfig) throws Exception{
        var data = new LinkedHashMap<Field, Object>();

        //Loop through every variable in the class implementing pulse config
        for(var field : pulseConfig.getClass().getDeclaredFields()){
            //If the field is marked as ignore save, pass over it
            if(field.isAnnotationPresent(IgnoreSave.class)) continue;
            //If the field isn't marked as public, pass over it
            if(!Modifier.isPublic(field.getModifiers())) continue;
            //If the field is marked as static, pass over it
            if(Modifier.isStatic(field.getModifiers())) continue;

            //return the variable from the field
            var storedData = field.get(pulseConfig);
            //if the stored data is null and we haven't marked the field as dont default, try and
            //give the field a value
            if(storedData == null && !field.isAnnotationPresent(DontDefault.class)){
                if(field.getType() == List.class) storedData = new ArrayList<Object>();
                else if(field.getType() == HashMap.class) storedData = new HashMap<Object, Object>();
                else if(field.getType() == Date.class) storedData = new Date();
                else{
                    var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(field.getType());
                    if(variableTest != null) storedData = variableTest.ReturnDefaultValue();
                }
            }

            //If the stored data is still null continue as we cant serialise a empty vairable
            if(storedData != null) data.put(field, storedData);
        }

        return data;
    }

    public static LinkedHashMap<Field, Object> ReturnALlFields(PulseClass pulseConfig) throws Exception{
        var data = new LinkedHashMap<Field, Object>();

        //Loop through every variable in the class implementing pulse config
        for(var field : pulseConfig.getClass().getDeclaredFields()){
            //If the field is marked as ignore save, pass over it
            if(field.isAnnotationPresent(IgnoreSave.class)) continue;
            //If the field isn't marked as public, pass over it
            if(!Modifier.isPublic(field.getModifiers())) continue;
            //If the field is marked as static, pass over it
            if(Modifier.isStatic(field.getModifiers())) continue;
            //return the variable from the field
            var storedData = field.get(pulseConfig);
            //if the stored data is null and we haven't marked the field as dont default, try and
            //give the field a value
            if(storedData == null && !field.isAnnotationPresent(DontDefault.class)){
                if(field.getType() == List.class) storedData = new ArrayList<Object>();
                else if(field.getType() == HashMap.class) storedData = new HashMap<Object, Object>();
                else if(field.getType() == Date.class) storedData = new Date();
                else{
                    var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(field.getType());
                    if(variableTest != null) storedData = variableTest.ReturnDefaultValue();
                }
            }

            //If the stored data is still null continue as we cant serialise a empty vairable
            if(storedData != null) data.put(field, storedData);
        }

        return data;
    }

    public static LinkedHashMap<Field, Object> ReturnALlFields(PulseBinaryFile pulseBinaryFile) throws Exception{
        var data = new LinkedHashMap<Field, Object>();

        //Loop through every variable in the class implementing pulse config
        for(var field : pulseBinaryFile.getClass().getDeclaredFields()){
            //If the field is marked as ignore save, pass over it
            if(field.isAnnotationPresent(IgnoreSave.class)) continue;
            //If the field isn't marked as public, pass over it
            if(!Modifier.isPublic(field.getModifiers())) continue;
            //If the field is marked as static, pass over it
            if(Modifier.isStatic(field.getModifiers())) continue;

            //return the variable from the field
            var storedData = field.get(pulseBinaryFile);
            //if the stored data is null and we haven't marked the field as dont default, try and
            //give the field a value
            if(storedData == null && !field.isAnnotationPresent(DontDefault.class)){
                if(field.getType() == List.class) storedData = new ArrayList<Object>();
                else if(field.getType() == HashMap.class) storedData = new HashMap<Object, Object>();
                else if(field.getType() == Date.class) storedData = new Date();
                else{
                    var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(field.getType());
                    if(variableTest != null) storedData = variableTest.ReturnDefaultValue();
                }
            }

            //If the stored data is still null continue as we cant serialise a empty vairable
            if(storedData != null) data.put(field, storedData);
        }

        return data;
    }

    public static LinkedHashMap<Field, Object> ReturnALlFields(PulseJson pulseJson) throws Exception{
        var data = new LinkedHashMap<Field, Object>();

        //Loop through every variable in the class implementing pulse config
        for(var field : pulseJson.getClass().getDeclaredFields()){
            //If the field is marked as ignore save, pass over it
            if(field.isAnnotationPresent(IgnoreSave.class)) continue;
            //If the field isn't marked as public, pass over it
            if(!Modifier.isPublic(field.getModifiers())) continue;
            //If the field is marked as static, pass over it
            if(Modifier.isStatic(field.getModifiers())) continue;

            //return the variable from the field
            var storedData = field.get(pulseJson);
            //if the stored data is null and we haven't marked the field as dont default, try and
            //give the field a value
            if(storedData == null && !field.isAnnotationPresent(DontDefault.class)){
                if(field.getType() == List.class) storedData = new ArrayList<Object>();
                else if(field.getType() == HashMap.class) storedData = new HashMap<Object, Object>();
                else if(field.getType() == Date.class) storedData = new Date();
                else{
                    var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(field.getType());
                    if(variableTest != null) storedData = variableTest.ReturnDefaultValue();
                }
            }

            //If the stored data is still null continue as we cant serialise a empty vairable
            if(storedData != null) data.put(field, storedData);
        }

        return data;
    }

    public static LinkedHashMap<Field, Object> ReturnALlFields(PulseMongo pulseMongo) throws Exception{
        var data = new LinkedHashMap<Field, Object>();

        //Loop through every variable in the class implementing pulse config
        for(var field : pulseMongo.getClass().getDeclaredFields()){
            //If the field is marked as ignore save, pass over it
            if(field.isAnnotationPresent(IgnoreSave.class)) continue;
            //If the field isn't marked as public, pass over it
            if(!Modifier.isPublic(field.getModifiers())) continue;
            //If the field is marked as static, pass over it
            if(Modifier.isStatic(field.getModifiers())) continue;

            //return the variable from the field
            var storedData = field.get(pulseMongo);
            //if the stored data is null and we haven't marked the field as dont default, try and
            //give the field a value
            if(storedData == null && !field.isAnnotationPresent(DontDefault.class)){
                if(field.getType() == List.class) storedData = new ArrayList<Object>();
                else if(field.getType() == HashMap.class) storedData = new HashMap<Object, Object>();
                else if(field.getType() == Date.class) storedData = new Date();
                else{
                    var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(field.getType());
                    if(variableTest != null) storedData = variableTest.ReturnDefaultValue();
                }
            }

            //If the stored data is still null continue as we cant serialise a empty vairable
            if(storedData != null) data.put(field, storedData);
        }

        return data;
    }
}
