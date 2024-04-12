package com.pandapulsestudios.pulseconfig;

import com.pandapulsestudios.pulseconfig.Interface.*;
import com.pandapulsestudios.pulseconfig.Interface.PulseConfig;
import com.pandapulsestudios.pulseconfig.Objects.SaveAbleInventory;
import com.pandapulsestudios.pulsecore.Java.PulseAutoRegister;
import org.bukkit.Statistic;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

@PulseAutoRegister
@ConfigHeader("This is the header for the config")
@ConfigFooter("This is the footer for the config")
public class TestConfig implements PulseConfig, PulseMongo, PulseBinary, PulseJSON {
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
    public String fileExtension() { return "yty"; }

    @Override
    public JavaPlugin mainClass() {
        return PulseConfig.super.mainClass();
    }

    @Override
    public String documentID() {
        return PulseConfig.super.documentID();
    }

    @Override
    public boolean useSubFolder() { return PulseConfig.super.useSubFolder(); }

    @Override
    public void FirstLoadBinary() { PulseBinary.super.FirstLoadBinary(); }

    @Override
    public void BeforeLoadBinary() { PulseBinary.super.BeforeLoadBinary(); }

    @Override
    public void AfterLoadBinary() { PulseBinary.super.AfterLoadBinary(); }

    @Override
    public void BeforeSaveBinary() { PulseBinary.super.BeforeSaveBinary(); }

    @Override
    public void AfterSaveBinary() { PulseBinary.super.AfterSaveBinary(); }

    @Override
    public void SaveBinary(boolean debug) { PulseBinary.super.SaveBinary(debug); }

    @Override
    public void LoadBinary(boolean debug) { PulseBinary.super.LoadBinary(debug); }

    @Override
    public void DeleteBinary(boolean debug) { PulseBinary.super.DeleteBinary(debug); }

    @Override
    public void DisplayBinary() { PulseBinary.super.DisplayBinary(); }
}
