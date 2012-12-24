package com.etriacraft.etriabending.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import com.etriacraft.etriabending.suites.PlayerSuite;

public class InventoryListener implements Listener {
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (PlayerSuite.silentChestInUse((Player) e.getWhoClicked())) e.setCancelled(true);
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (PlayerSuite.silentChestInUse((Player) e.getPlayer())) PlayerSuite.silentChestClose((Player) e.getPlayer());
    }
    
}