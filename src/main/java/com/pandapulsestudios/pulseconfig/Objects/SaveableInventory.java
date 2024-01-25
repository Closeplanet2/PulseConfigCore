package com.pandapulsestudios.pulseconfig.Objects;

import com.pandapulsestudios.pulseconfig.Interfaces.IgnoreSave;
import com.pandapulsestudios.pulseconfig.Interfaces.PulseClass;
import com.pandapulsestudios.pulseconfig.PulseConfig;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SaveableInventory implements PulseClass {
    @IgnoreSave
    private Inventory inventory;
    public List<ItemStack> contents = new ArrayList<>();
    public String title = "";
    public int size = 9;
    public int maxStackSize = 1;

    public SaveableInventory(){}
    public SaveableInventory(String inventoryTitle, int inventorySize){
        inventory = Bukkit.createInventory(null, inventorySize, inventoryTitle);
        contents = Arrays.stream(inventory.getContents()).toList();
        title = inventoryTitle;
        size = inventory.getSize();
        maxStackSize = inventory.getMaxStackSize();
    }

    public void AddItem(ItemStack... itemStacks){
        inventory.addItem(itemStacks);
        contents = Arrays.stream(inventory.getContents()).toList();
    }

    public void RemoveItem(ItemStack... itemStacks){
        inventory.removeItem(itemStacks);
        contents = Arrays.stream(inventory.getContents()).toList();
    }

    public Inventory ReturnInventory(){
        if(inventory != null) return inventory;
        inventory = Bukkit.createInventory(null, size, title);
        inventory.setContents(contents.toArray(new ItemStack[0]));
        inventory.setMaxStackSize(maxStackSize);
        return inventory;
    }
}
