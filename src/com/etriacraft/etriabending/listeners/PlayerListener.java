package com.etriacraft.etriabending.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import com.etriacraft.etriabending.EtriaBending;
import com.etriacraft.etriabending.suites.MessagingSuite;
import com.etriacraft.etriabending.suites.PlayerSuite;
import com.etriacraft.etriabending.util.Utils;

public class PlayerListener implements Listener {

	EtriaBending plugin;
	public PlayerListener(EtriaBending instance) {
		this.plugin = instance;
	}
	
	public static String joinmessage;
	public static String welcomemessage;
	public static String quitmessage;
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		for (String o : PlayerSuite.vanishDb) {
			if (e.getPlayer().hasPermission("ec.vanish.seehidden")) continue;
			if (Bukkit.getPlayer(o) == null) continue;
			e.getPlayer().hidePlayer(Bukkit.getPlayer(o));
		}
		if (PlayerSuite.isVanished(e.getPlayer())) {
			PlayerSuite.setVanished(e.getPlayer(), true);
			e.getPlayer().sendMessage("§aYou logged in vanished!");
		}
		
		MessagingSuite.showMotd(e.getPlayer());

	}
	
	@EventHandler
	public void playerquitmessage(PlayerQuitEvent e) {
		e.setQuitMessage(Utils.colorize(quitmessage).replaceAll("<name>", e.getPlayer().getDisplayName()));
	}
	@EventHandler
	public void playerjoinmessages(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		String displayName;
		if (!(plugin.getConfig().get("displaynames." + p.getName()) == null)) {
			displayName = plugin.getConfig().getString("displaynames." + p.getName());
		} else {
			displayName = p.getName();
		}
		p.setDisplayName(displayName);
		if (!p.hasPlayedBefore()) {
			e.setJoinMessage(Utils.colorize(welcomemessage).replaceAll("<name>", p.getDisplayName()));
		} else {
			e.setJoinMessage(Utils.colorize(joinmessage).replaceAll("<name>", p.getDisplayName()));
		}
	}
	
//	@EventHandler
//	public void onPlayerChat(AsyncPlayerChatEvent event) {
//		if (event.isCancelled()) return;
//		
//		Map<String, List<String>> ignoreList = plugin.getList();
//		String p = event.getPlayer().getName();
//		List<String> playerIgnores = ignoreList.get(p);
//		
//		for (Iterator<Player> it = event.getRecipients().iterator(); it.hasNext();) {
//			Player r = it.next();
//			List<String> recipientIgnores = ignoreList.get(r.getName());
//			
//			if (playerIgnores.contains(r.getName())) {
//				event.getRecipients().remove(r);
//			}
//			boolean playerIgnoresRecipient = playerIgnores != null && playerIgnores.contains(r.getName());
//			boolean recipientIgnoresPlayer = recipientIgnores != null && recipientIgnores.contains(p);
//			if (playerIgnoresRecipient || recipientIgnoresPlayer) {
//				event.getRecipients().remove(r);
//				it.remove();
//			}
//		}
//	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
		if (e.getClickedBlock().getType().equals(Material.REDSTONE_ORE) && PlayerSuite.isVanished(e.getPlayer())) e.setCancelled(true);

		if (PlayerSuite.isVanished(e.getPlayer()) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.CHEST)) {
			e.setCancelled(true);
			final Chest c = (Chest) e.getClickedBlock().getState();
			final Inventory i = Bukkit.getServer().createInventory(e.getPlayer(), c.getInventory().getSize());
			i.setContents(c.getInventory().getContents());
			e.getPlayer().openInventory(i);
			PlayerSuite.silentChestOpen(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		if (PlayerSuite.noexpdropDB.contains(player.getName())) {
			e.setKeepLevel(true);
			e.setDroppedExp(0);
			
		}
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		if (PlayerSuite.isVanished(e.getPlayer())) e.setCancelled(true);
	}
}