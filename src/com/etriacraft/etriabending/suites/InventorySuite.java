package com.etriacraft.etriabending.suites;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.etriacraft.etriabending.EtriaBending;
import com.etriacraft.etriabending.Strings;
import com.etriacraft.etriabending.util.Utils;

public class InventorySuite {

	private static HashMap<String, ItemStack[]> invRestoreDb = new HashMap<String, ItemStack[]>();
	EtriaBending plugin;

	public InventorySuite(EtriaBending instance) {
		this.plugin = instance;
		init();
	}

	private void init() {
		PluginCommand clear = plugin.getCommand("clear");
		PluginCommand copyinv = plugin.getCommand("copyinv");
		PluginCommand item = plugin.getCommand("item");
		PluginCommand iteminfo = plugin.getCommand("iteminfo");
		CommandExecutor exe;

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.clear")) {
					s.sendMessage("§cYou don't have permission to do that!");
				} else {
					final Player p;
					if (args.length >= 1) {
						if (!s.hasPermission("eb.clear.other")) {
							s.sendMessage("§cYou don't have permission to do that!");
							return true;
						}
						p = Bukkit.getPlayer(args[0]);
					} else {
						if (!(s instanceof Player)) return false;
						p = (Player) s;
					}
					if (p == null) {
						s.sendMessage("§cThat player is not online.");
						return true;
					}

					p.getInventory().clear();
					p.getInventory().setHelmet(null);
					p.getInventory().setChestplate(null);
					p.getInventory().setLeggings(null);
					p.getInventory().setBoots(null);
					p.sendMessage("§aInventory Cleared.");
					return true;
				} return true;
			}
		}; clear.setExecutor(exe);

		exe = new CommandExecutor() {
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.copyinv")) {
					s.sendMessage("§cYou don't have permission to do that!");
				} else {
					if (!(s instanceof Player)) return false;

					if (args.length >= 1) {
						Player p = Bukkit.getPlayer(args[0]);
						if (p == null) {
							s.sendMessage("§cThat player is not online.");
							return true;
						}

						if (!invRestoreDb.containsKey(s.getName())) {
							invRestoreDb.put(s.getName(), ((Player) s).getInventory().getContents());
						}
						((Player) s).getInventory().setContents(p.getInventory().getContents());
						s.sendMessage("§aCpoied inventory of §e" + p.getName());
					} else {
						if (invRestoreDb.containsKey(s.getName())) {
							((Player) s).getInventory().setContents(invRestoreDb.get(s.getName()));
							invRestoreDb.remove(s.getName());
							s.sendMessage("§aInventory restored.");
						} else {
							s.sendMessage("§cYou had no inventory to restore.");
						}
					}
					return true;
				} return true;
			}
		}; copyinv.setExecutor(exe);

		exe = new CommandExecutor() {
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.item")) {
					s.sendMessage("§cYou don't have permisison to do that!");
				} else {
					if (!(s instanceof Player)) return false;

					ItemStack is = Utils.parseItemStack(args[0]);
					if (is == null) {
						s.sendMessage("§cInvalid Item Info.");
						return true;
					}

					Player p;
					if (args.length >= 2) p = Bukkit.getPlayer(args[1]);
					else p = ((Player) s);
					if (p == null) {
						s.sendMessage("That player is not online.");
						return true;
					}

					s.sendMessage("§aGiving§e " + is.getAmount() + " " + Strings.toTitle(is.getType().name()) + ((s != p)? " §ato§e " + p.getName() : ""));
					p.getInventory().addItem(is);
					return true;
				} return true;

			}
		}; item.setExecutor(exe);

		exe = new CommandExecutor() {
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.iteminfo")) {
					s.sendMessage("§cYou don't have permission to do that.");
				} else {
					if (!(s instanceof Player)) return false;

					ItemStack is = ((Player) s).getItemInHand();

					s.sendMessage("§aItem Information:");
					s.sendMessage("§a" + Strings.toTitle(is.getType().name()) + " -§e " + Integer.toString(is.getTypeId()) + (is.getDurability() != 0 ? "§a:" + is.getDurability() : ""));

					if (is.getEnchantments().size() <1) return true;
					for (Map.Entry<Enchantment, Integer> ench : is.getEnchantments().entrySet()) {
						s.sendMessage("§7§o" + Strings.toTitle(ench.getKey().getName()) + " " + Strings.toRomanNumeral(ench.getValue()));
					}
					return true;
				} return true;
			}
		}; iteminfo.setExecutor(exe);
	}

}
