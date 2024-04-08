package com.pandapulsestudios.pulseconfig.Objects.Savable;

import com.pandapulsestudios.pulseconfig.Interfaces.IgnoreSave;
import com.pandapulsestudios.pulseconfig.Interfaces.PulseClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
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
        title = inventoryTitle;
        size = inventory.getSize();
        maxStackSize = inventory.getMaxStackSize();
    }

    @Override
    public void BeforeSave() {
        contents = Arrays.stream(inventory.getContents()).toList();
    }

    @Override
    public void AfterLoad() {
        inventory = Bukkit.createInventory(null, size, title);
        inventory.setContents(contents.toArray(new ItemStack[0]));
    }

    public void AddItem(ItemStack... itemStacks){
        inventory.addItem(itemStacks);
    }
    public void SetItem(int pos, ItemStack itemStack){
        inventory.setItem(pos, itemStack);
    }
    public void RemoveItem(ItemStack... itemStacks){
        inventory.removeItem(itemStacks);
    }
    public void OpenInventory(Player player){
        player.openInventory(inventory);
    }
    public ItemStack[] GetLiveContents(){
        return inventory.getContents();
    }
    public List<HumanEntity> ReturnViewers(){ return inventory.getViewers();}
}
