package com.pandapulsestudios.pulseconfig.Objects;

import com.pandapulsestudios.pulseconfig.Interface.CustomVariable;
import com.pandapulsestudios.pulseconfig.Interface.DontSave;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
    public SaveableHashmap<Integer, Object> SerializeData() {
        var data = new SaveableHashmap<Integer, Object>(Integer.class, Object.class);
        data.put(0, inventory.getContents());
        data.put(1, inventory.getSize());
        data.put(2, inventoryTitle);
        return data;
    }

    @Override
    public void DeSerializeData(HashMap<Integer, Object> hashMap) {
        var inventoryTitle = (String) hashMap.get(2);
        var inventorySize = (Integer) hashMap.get(1);
        var inventoryContents = (ItemStack[]) hashMap.get(0);
        inventory = Bukkit.createInventory(null, inventorySize, inventoryTitle);
        inventory.setContents(inventoryContents);
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
