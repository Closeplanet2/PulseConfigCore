package com.pandapulsestudios.pulseconfig.Objects;

import com.pandapulsestudios.pulseconfig.Interface.CustomVariable;
import com.pandapulsestudios.pulseconfig.Interface.DontSave;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SaveAbleInventory implements CustomVariable {
    @DontSave
    private Inventory inventory;
    private String inventoryTitle;

    public SaveAbleInventory(){}
    public SaveAbleInventory(String inventoryTitle, int inventorySize){
        inventory = Bukkit.createInventory(null, inventorySize, inventoryTitle);
        this.inventoryTitle = inventoryTitle;
    }

    @Override
    public SaveableHashmap<Object, Object> SerializeData() {
        var data = new SaveableHashmap<Object, Object>(Object.class, Object.class);
        data.put("CONTENTS", Arrays.asList(inventory.getContents()));
        data.put("SIZE", inventory.getSize());
        data.put("TITLE", inventoryTitle);
        return data;
    }

    @Override
    public void DeSerializeData(HashMap<Object, Object> hashMap) {
        inventoryTitle = (String) hashMap.get("TITLE");
        inventory = Bukkit.createInventory(null, (Integer) hashMap.get("SIZE"), inventoryTitle);
        inventory.setContents(((List<ItemStack>) hashMap.get("CONTENTS")).toArray(new ItemStack[0]));
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
