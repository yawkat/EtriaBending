package com.etriacraft.etriabending.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import com.etriacraft.etriabending.suites.PlayerSuite;

public class EntityListener implements Listener {
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (PlayerSuite.godDb.contains(((Player) e.getEntity()).getName())) {
            e.setCancelled(true);
            e.getEntity().setFireTicks(0);
        }
    }
    
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (PlayerSuite.godDb.contains(((Player) e.getEntity()).getName())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityTarget(EntityTargetEvent e) {
        if ((e.getTarget() instanceof Player) && PlayerSuite.isVanished((Player) e.getTarget())) {
            e.setCancelled(true);
        }
    }
    
}