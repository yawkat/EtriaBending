package com.etriacraft.etriabending.listeners;

import java.util.HashSet;

import org.bukkit.event.Event;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Button;

public class ElevatorListener implements Listener {

public static final byte[] SIGN_DRECTIONS = {5, 3, 4, 2};
    public static final BlockFace[] BLOCK_FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    public static final HashSet<Integer> attachableIds = new HashSet();
    
    public static final HashSet<Integer> safeLiftIds = new HashSet();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
    if (e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
    if (!(e.getClickedBlock().getState() instanceof Sign) && e.getClickedBlock().getType() != Material.STONE_BUTTON) return;

    if (e.getPlayer().isSneaking()) return;

    Sign sign = null;
    if (e.getClickedBlock().getType() == Material.STONE_BUTTON) {
        Button btn = (Button) e.getClickedBlock().getState().getData();
        Block block = e.getClickedBlock().getRelative(btn.getAttachedFace());
        for (BlockFace bf : BlockFace.values()) {
            if (block.getRelative(bf).getState() instanceof Sign) {
                sign = (Sign) block.getRelative(bf).getState();
                break;
            }
        }
    } else {
        sign = (Sign) e.getClickedBlock().getState();
    }

    if (sign == null) return;

    if (!sign.getLine(1).equalsIgnoreCase("[Elevator]")) return;

    if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
        e.setUseItemInHand(Event.Result.DENY);
        e.setUseInteractedBlock(Event.Result.DENY);
        for (int y = (sign.getY() - 1); y != 0; y--) {
            Block b = sign.getWorld().getBlockAt(sign.getX(), y, sign.getZ());
            if (!(b.getState() instanceof Sign)) continue;
            Sign s = (Sign) b.getState();
            if (s.getLine(1).equalsIgnoreCase("[Elevator]")) {
                if (teleport(e.getPlayer(), s.getWorld(), y)) {
                    e.getPlayer().sendMessage("§eGoing " + (s.getLine(0).isEmpty()? "down.." :"to§r " + s.getLine(0)));
                }
                return;
            }
        }
        e.getPlayer().sendMessage("§cThere was no Elevator found below this");
    } else {
        e.setCancelled(true);
        sign.update(true);
        for (int y = (sign.getY() + 1); y <= 256; y++) {
            Block b = sign.getWorld().getBlockAt(sign.getX(), y, sign.getZ());
            if (!(b.getState() instanceof Sign)) continue;
            Sign s = (Sign)b.getState();
            if (s.getLine(1).equalsIgnoreCase("[Elevator]")) {
                if (teleport(e.getPlayer(), s.getWorld(), y)) {
                    e.getPlayer().sendMessage("§eGoing " + (s.getLine(0).isEmpty()? "up.." : "to§r " + s.getLine(0)));
                }
                return;
            }
        }
        e.getPlayer().sendMessage("§cThere was no Elevator found above this");
    }
}

@EventHandler
public void onSignChange(SignChangeEvent e) {
    if (!e.getLine(1).equalsIgnoreCase("[Elevator]")) return;

    boolean found = false;
    for (int i = 0; i < 256; i++) {
        Block b = e.getBlock().getWorld().getBlockAt(e.getBlock().getX(), i, e.getBlock().getZ());
        if (!(b.getState() instanceof Sign)) continue;
        Sign s = (Sign)b.getState();
        if (s.getLine(1).equalsIgnoreCase("[Elevator]")) {
            found = true;
            break;
        }
    }

    e.getPlayer().sendMessage("§eElevator created " + (found? "and linked" : ""));
    e.setLine(1, "[Elevator]");
    ((Sign) e.getBlock().getState()).update();
}

private boolean teleport(Player player, World w, int y) {
    y--;
    Location loc = player.getLocation();
    if (!safeLiftIds.contains(w.getBlockAt(loc.getBlockX(), (y), loc.getBlockZ()).getTypeId())) y++;
    if ((safeLiftIds.contains(w.getBlockAt(loc.getBlockX(), (y - 1), loc.getBlockZ()).getTypeId()) //
            && safeLiftIds.contains(w.getBlockAt(loc.getBlockX(), (y - 2), loc.getBlockZ()).getTypeId()))
            || (!safeLiftIds.contains(w.getBlockAt(loc.getBlockX(), (y), loc.getBlockZ()).getTypeId())
            || !safeLiftIds.contains(w.getBlockAt(loc.getBlockX(), (y + 1), loc.getBlockZ()).getTypeId()))) {
        player.sendMessage("§cTeleport location is not safe!");
        return false;
    }

    loc.setY(y);
    return player.teleport(loc);
}

static {
        attachableIds.add(Material.CHEST.getId());
        attachableIds.add(Material.ENDER_CHEST.getId());
        attachableIds.add(Material.ENCHANTMENT_TABLE.getId());
        attachableIds.add(Material.BREWING_STAND.getId());
        attachableIds.add(Material.FURNACE.getId());
        attachableIds.add(Material.BURNING_FURNACE.getId());
        attachableIds.add(Material.DISPENSER.getId());
        attachableIds.add(Material.WOOD_DOOR.getId());
        attachableIds.add(Material.IRON_DOOR.getId());
        attachableIds.add(Material.TRAP_DOOR.getId());
        
        safeLiftIds.add(Material.AIR.getId());
        safeLiftIds.add(Material.STONE_BUTTON.getId());
        safeLiftIds.add(Material.POWERED_RAIL.getId());
        safeLiftIds.add(Material.DETECTOR_RAIL.getId());
        safeLiftIds.add(Material.RAILS.getId());
        safeLiftIds.add(Material.TORCH.getId());
        safeLiftIds.add(Material.REDSTONE_WIRE.getId());
        safeLiftIds.add(Material.REDSTONE_TORCH_ON.getId());
        safeLiftIds.add(Material.REDSTONE_TORCH_OFF.getId());
        safeLiftIds.add(Material.WOOD_PLATE.getId());
        safeLiftIds.add(Material.STONE_PLATE.getId());
        safeLiftIds.add(Material.WATER_LILY.getId());
        safeLiftIds.add(Material.WATER.getId());
        safeLiftIds.add(Material.STATIONARY_WATER.getId());
        safeLiftIds.add(Material.SNOW.getId());
        safeLiftIds.add(Material.SIGN_POST.getId());
        safeLiftIds.add(Material.WALL_SIGN.getId());
        safeLiftIds.add(Material.LONG_GRASS.getId());
    }
    
}