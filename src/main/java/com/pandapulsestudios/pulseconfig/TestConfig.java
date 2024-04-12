package com.pandapulsestudios.pulseconfig;

import com.pandapulsestudios.pulseconfig.Interface.PulseMongo;
import com.pandapulsestudios.pulseconfig.Interface.StorageComment;
import com.pandapulsestudios.pulseconfig.Interface.ConfigFooter;
import com.pandapulsestudios.pulseconfig.Interface.ConfigHeader;
import com.pandapulsestudios.pulseconfig.Interface.PulseConfig;
import com.pandapulsestudios.pulseconfig.Objects.SaveAbleInventory;
import com.pandapulsestudios.pulsecore.Java.PulseAutoRegister;
import org.bukkit.Statistic;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

@PulseAutoRegister
@ConfigHeader("This is the header for the config")
@ConfigFooter("This is the footer for the config")
public class TestConfig implements PulseConfig, PulseMongo {
    @StorageComment("This is a comment for a")
    public boolean a;
    @StorageComment("This is a comment for b")
    public double b;
    @StorageComment("This is a comment for c")
    public float c;
    public int d;
    public long e;
    public String f;
    public UUID uuid;
    public Statistic statistic;
    public SaveAbleInventory saveAbleInventory = new SaveAbleInventory("Test Inventory", 54);

    @Override
    public String mongoIP() {return "mongodb+srv://Admin:Admin1234@portfolio.poeobnf.mongodb.net/?retryWrites=true&w=majority&appName=PORTFOLIO";}
    @Override
    public String databaseName() { return com.pandapulsestudios.pulseconfig.PulseConfig.class.getSimpleName(); }

    @Override
    public void AfterLoadConfig() {
        DisplayConfig();
    }

    @Override
    public JavaPlugin mainClass() {
        return PulseConfig.super.mainClass();
    }

    @Override
    public String documentID() {
        return PulseConfig.super.documentID();
    }
}
