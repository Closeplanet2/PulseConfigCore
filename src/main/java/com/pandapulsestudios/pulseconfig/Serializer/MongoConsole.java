package com.pandapulsestudios.pulseconfig.Serializer;

import com.pandapulsestudios.pulseconfig.Interface.PulseMongo;
import com.pandapulsestudios.pulseconfig.Interface.CustomVariable;
import com.pandapulsestudios.pulseconfig.Interface.PulseClass;
import com.pandapulsestudios.pulseconfig.Interface.SaveName;
import com.pandapulsestudios.pulseconfig.Objects.SaveableArrayList;
import com.pandapulsestudios.pulseconfig.Objects.SaveableHashmap;
import com.pandapulsestudios.pulseconfig.Objects.SaveableLinkedHashMap;
import com.pandapulsestudios.pulsecore.VariableAPI.API.VariableAPI;

import java.util.Date;

public class MongoConsole {
    private static String ReturnIndent(int indent){
        return " ".repeat(Math.max(0, indent));
    }

    public static String ConsoleOutput(PulseMongo pulseConfig) throws Exception {
        var stringBuilder = new StringBuilder(String.format("==========[%s / PULSE MONGO]==========\n{\n", pulseConfig.documentID()));
        var dataFields = SerializerHelpers.ReturnALlFields(pulseConfig.getClass(), pulseConfig);
        for(var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            var fieldValue = ConsoleOutputSingle(dataFields.get(field), 2);
            if(fieldValue == null) continue;
            stringBuilder.append(String.format("%s%s:%s\n", ReturnIndent(1), fieldName, fieldValue.toString()));
        }
        stringBuilder.append("}\n==========[END]==========");
        return stringBuilder.toString();
    }

    private static String ConsoleOutputPulseClass(PulseClass pulseClass, int indent) throws Exception {
        var stringBuilder = new StringBuilder("<");
        var dataFields = SerializerHelpers.ReturnALlFields(pulseClass.getClass(), pulseClass);
        for(var field : dataFields.keySet()){
            var fieldName = field.isAnnotationPresent(SaveName.class) ? field.getAnnotation(SaveName.class).value() : field.getName();
            var fieldValue = ConsoleOutputSingle(dataFields.get(field), 0);
            if(fieldValue == null) continue;
            stringBuilder.append(String.format("\n%s%s:%s", ReturnIndent(indent + 1), fieldName, fieldValue.toString()));
        }
        stringBuilder.append(String.format("\n%s>", ReturnIndent(indent - 1)));
        return stringBuilder.toString();
    }

    private static String ConsoleOutputSaveableHashmap(SaveableHashmap saveableHashmap, int indent) throws Exception {
        if(saveableHashmap.isEmpty()) return "{}";
        var stringBuilder = new StringBuilder("{");
        for(var key : saveableHashmap.keySet()){
            var skey = ConsoleOutputSingle(key, indent);
            var sValue = ConsoleOutputSingle(saveableHashmap.get(key), indent);
            stringBuilder.append(String.format("\n%s%s:%s", ReturnIndent(indent), skey, sValue));
        }
        stringBuilder.append(String.format("\n%s}", ReturnIndent(indent - 1)));
        return stringBuilder.toString();
    }

    private static String ConsoleOutputSaveableLinkedHashMap(SaveableLinkedHashMap saveableLinkedHashMap, int indent) throws Exception {
        if(saveableLinkedHashMap.isEmpty()) return "{}";
        var stringBuilder = new StringBuilder("{");
        for(var key : saveableLinkedHashMap.keySet()){
            var skey = ConsoleOutputSingle(key, indent);
            var sValue = ConsoleOutputSingle(saveableLinkedHashMap.get(key), indent);
            stringBuilder.append(String.format("\n%s%s:%s", ReturnIndent(indent), skey, sValue));
        }
        stringBuilder.append(String.format("\n%s}", ReturnIndent(indent - 1)));
        return stringBuilder.toString();
    }

    private static String ConsoleOutputSaveableArrayList(SaveableArrayList saveableArrayList, int indent) throws Exception {
        if(saveableArrayList.isEmpty()) return "[]";

        var stringBuilder = new StringBuilder("[");
        for(var value : saveableArrayList.ReturnArrayList()){
            var sValue = ConsoleOutputSingle(value, indent);
            stringBuilder.append(String.format("\n%s%s", ReturnIndent(indent), sValue));
        }

        stringBuilder.append(String.format("\n%s]", ReturnIndent(indent - 1)));
        return stringBuilder.toString();
    }

    private static String ConsoleOutputSingle(Object storedData, int indent) throws Exception{
        if(storedData == null) return null;
        var variableTest = VariableAPI.RETURN_TEST_FROM_TYPE(storedData.getClass());
        if(storedData instanceof PulseClass pulseClass){
            return ConsoleOutputPulseClass(pulseClass, indent + 1);
        }else if(storedData instanceof SaveableHashmap saveableHashmap){
            return ConsoleOutputSaveableHashmap(saveableHashmap, indent + 1);
        }else if(storedData instanceof SaveableLinkedHashMap saveableLinkedHashMap){
            return ConsoleOutputSaveableLinkedHashMap(saveableLinkedHashMap, indent + 1);
        }else if(storedData instanceof SaveableArrayList saveableArrayList){
            return ConsoleOutputSaveableArrayList(saveableArrayList, indent + 1);
        }else if(storedData instanceof CustomVariable customVariable){
            return ConsoleOutputSaveableHashmap(customVariable.SerializeData(), indent + 1);
        }else if(storedData instanceof Date date){
            return date.toString();
        }else if(variableTest != null){
            return String.format("%s%s", ReturnIndent(indent), variableTest.SerializeData(storedData).toString());
        }else{
            return storedData.toString();
        }
    }
}
