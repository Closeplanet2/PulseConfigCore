package com.pandapulsestudios.pulseconfig.APIS;

public class ConfigAPI {
    public static String ReturnConfigPath(Class<?> mainClass, Class<?> subClass){
        return String.format("plugins/%s/Configs/%s", mainClass.getSimpleName(), subClass.getSimpleName());
    }
}
