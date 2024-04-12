package com.pandapulsestudios.pulseconfig.Objects.Savable;

import com.pandapulsestudios.pulseconfig.Enums.SaveableType;
import com.pandapulsestudios.pulseconfig.Interfaces.IgnoreSave;
import com.pandapulsestudios.pulseconfig.Interfaces.PulseClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class SaveAbleInventoryConfig implements PulseClass {
    @IgnoreSave
    private Inventory inventory;
    public SaveableArrayList<ItemStack> contents = new SaveableArrayList<>(SaveableType.CONFIG, ItemStack.class);
    public String title = "Test";
    public int size = 9;
    public int maxStackSize = 1;

    public SaveAbleInventoryConfig(){}

    public SaveAbleInventoryConfig(String inventoryTitle, int inventorySize){
        inventory = Bukkit.createInventory(null, inventorySize, inventoryTitle);
        title = inventoryTitle;
        size = inventory.getSize();
        maxStackSize = inventory.getMaxStackSize();
    }

    @Override
    public void BeforeSave() {
        contents.arrayList = Arrays.stream(inventory.getContents()).toList();
    }

    @Override
    public void AfterLoad() {
        inventory = Bukkit.createInventory(null, size, title);
        inventory.setContents(contents.arrayList.toArray(new ItemStack[0]));
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
