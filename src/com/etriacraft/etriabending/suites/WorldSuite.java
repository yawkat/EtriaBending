package com.etriacraft.etriabending.suites;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.etriacraft.etriabending.EtriaBending;

public class WorldSuite {

	EtriaBending plugin;

	public WorldSuite(EtriaBending instance) {
		this.plugin = instance;
		init();
	}

	private void init() {
		PluginCommand chunkfix = plugin.getCommand("chunkfix");
		PluginCommand setspawn = plugin.getCommand("setspawn");
		PluginCommand spawnmob = plugin.getCommand("spawnmob");
		PluginCommand weather = plugin.getCommand("weather");
		CommandExecutor exe;

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!(s instanceof Player)) return false;
				if (!s.hasPermission("eb.chunkfix")) {
					s.sendMessage("§cYou don't have permission to do that!");
				} else {
					final ArrayList<Chunk> chunks = new ArrayList();
					final Player p = ((Player) s);
					if (args.length >= 1) {
						try {
							final int radius = Integer.parseInt(args[0]);
							final int chunkval = (radius * 16);
							int minX = p.getLocation().getBlockX() - chunkval;
							int minZ = p.getLocation().getBlockZ() - chunkval;
							int maxX = p.getLocation().getBlockX() + chunkval;
							int maxZ = p.getLocation().getBlockZ() + chunkval;
							for (int opx = minX; opx <= maxX; opx += 16) {
								for (int opz = minZ; opz <= maxZ; opz += 16) {
									chunks.add(p.getWorld().getChunkAt(opx, opz));
								}
							}
						} catch (NumberFormatException e) {
							s.sendMessage("§7" + args[0] + "§c is not a valid number.");
							return true;
						}
					} else {
						chunks.add(p.getWorld().getChunkAt(p.getLocation()));
					}

					int op = 0;
					for (Chunk chunk : chunks) {
						if (p.getWorld().refreshChunk(chunk.getX(), chunk.getZ()))
							op++;
					}

					s.sendMessage("§aRefreshed§e " + op + " §achunks");
					return true;
				} return true;
			}
		}; chunkfix.setExecutor(exe);

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!(s instanceof Player)) return false;
				if (!s.hasPermission("eb.setspawn")) {
					s.sendMessage("§cYou don't have permission to do that!");
				} else {
					final Location newspawn = ((Player) s).getLocation();
					newspawn.getWorld().setSpawnLocation(newspawn.getBlockX(), newspawn.getBlockY(), newspawn.getBlockZ());
					s.sendMessage("§aSet spawn of§e " + newspawn.getWorld().getName());
					return true;
				} return true;
			} 
		}; setspawn.setExecutor(exe);

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!(s instanceof Player)) return false;
				if (args.length < 2) {
					s.sendMessage("§cNot enough arguments.");
				}
				if (!s.hasPermission("eb.spawnmob")) {
					s.sendMessage("§cYou don't have permission to do that!");
				} else {
					final EntityType et = EntityType.fromName(args[0]);
					if (et == null || (!et.isSpawnable() || !et.isAlive())) {
						String types = "";
						for (EntityType ett : EntityType.values()) {
							if (!ett.isAlive() || !ett.isSpawnable()) continue;
							if (!types.isEmpty()) types += "§a, ";
							types += "§e" + ett.getName();
						}
						s.sendMessage("§aValid mob types: " + types);
						return true;
					}

					int amount = 1;
					try {
						amount = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						return false;
					}

					s.sendMessage("§aSpawned§e " + amount + "§a " + et.getName());
					while (amount != 0) {
						((Player) s).getWorld().spawnEntity(((Player) s).getLocation(), et);
						--amount;
					}

					return true;
				} return true;
			}
		}; spawnmob.setExecutor(exe);

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (args.length < 1) {
					s.sendMessage("§cNot enough arguments.");
				}
				if (!s.hasPermission("eb.weather")) {
					s.sendMessage("§cYou don't have permission to do that!");
				} else {
					final World w;
					if (args.length >= 2) {
						w = Bukkit.getWorld(args[1]);
						if (w == null) {
							s.sendMessage("§cThat world doesn't exist!");
						}
					} else {
						if (!(s instanceof Player)) return false;
						w = ((Player) s).getWorld();
					}

					switch (args[0]) {
					case "on":
						w.setStorm(true);
						break;
					case "off":
						w.setStorm(false);
						break;
					case "thunder":
						w.setThundering(true);
						break;
					default:
						s.sendMessage("§cInvalid weather state.");
						return true;
					}
					s.sendMessage("§aSet the weather in§e " + w.getName() + " §ato§e " + args[0]);
					return true;
				} return true;
			}

		}; weather.setExecutor(exe);
	}

}