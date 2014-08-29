package com.etriacraft.etriabending.suites;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.etriacraft.etriabending.EtriaBending;
import com.etriacraft.etriabending.Strings;
import com.etriacraft.etriabending.util.Utils;

public class PlayerSuite {

	public static boolean maintenanceon = false;

	// Methods

	public static void sendTradeRequest(Player target, Player sender) {
		tradedb.put(sender.getName(), target.getName());
		target.sendMessage("§3" + sender.getName() + " §a has requested to trade with you.");
		target.sendMessage("§aType §3/trade " + sender.getName() + " §a to accept the trade.");
	}

	public static Inventory getTradeInv(Player p, Player t) {
		synchronized (trades) {
			for (final HashMap<String, String> set : trades.keySet()) {
				if ((set.containsKey(t.getName()) && set.get(t.getName()).equals(p.getName())) || (set.containsKey(p.getName()) && set.get(p.getName()).equals(t.getName())))
					return trades.get(set);
			}
		}
		return null;
	}
	public static void Helps() {
		File helps = new File(EtriaBending.getInstance().getDataFolder(), "helps.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(helps);

		if (!helps.exists()) {
			config.set("default", new String[] {"&eYou can change this and add your own content to the default page"});
			try { config.save(helps); } catch (IOException e) { e.printStackTrace(); }
		}

		for (String key : config.getKeys(true)) {
			helpPagesDb.put(key.toLowerCase(), config.getStringList(key));
		}
		EtriaBending.log.info("Successfully loaded " + helpPagesDb.size() + " help pages");
	}

	public static void setVanished(Player p, boolean state) {
		for (Player o : Bukkit.getOnlinePlayers()) {
			if (state) {
				if (o.hasPermission("eb.vanish.seehidden")) continue;
				o.hidePlayer(p);
			} else o.showPlayer(p);
		}
		if (state) vanishDb.add(p.getName());
		else vanishDb.remove(p.getName());
	}
	public static boolean isVanished(Player p) {
		return vanishDb.contains(p.getName());
	}

	public static void silentChestOpen(Player p) {
		chestUserDb.add(p.getName());
	}

	public static void silentChestClose(Player p) {
		chestUserDb.remove(p.getName());
	}

	public static boolean silentChestInUse(Player p) {
		return chestUserDb.contains(p.getName());
	}

	public static Set<String> godDb = new HashSet();
	public static HashMap<String, List<String>> helpPagesDb = new HashMap();
	public static Set<String> vanishDb = new HashSet<String>();
	public static Set<String> chestUserDb = new HashSet<String>();
	public static Set<String> noexpdropDB = new HashSet<String>();
	public static final HashMap<String, String> tradedb = new HashMap<String, String>();
	public static final HashMap<HashMap<String, String>, Inventory> trades = new HashMap<HashMap<String, String>, Inventory>();
	public static HashMap<String, Location> modmodedb = new HashMap();

	EtriaBending plugin;

	public PlayerSuite(EtriaBending instance) {
		this.plugin = instance;
		init();
	}

	private void init() {
		PluginCommand gamemode = plugin.getCommand("gamemode");
		PluginCommand getpos = plugin.getCommand("getpos");
		PluginCommand god = plugin.getCommand("god");
		PluginCommand hat = plugin.getCommand("hat");
		PluginCommand help = plugin.getCommand("help");
		PluginCommand vanish = plugin.getCommand("vanish");
		PluginCommand workbench = plugin.getCommand("workbench");
		PluginCommand enchantingtable = plugin.getCommand("enchantingtable");
		PluginCommand displayname = plugin.getCommand("displayname");
		PluginCommand eb = plugin.getCommand("eb");
		PluginCommand maintenance = plugin.getCommand("maintenance");
		PluginCommand modmode = plugin.getCommand("modmode");
		PluginCommand trade = plugin.getCommand("trade");
		PluginCommand hug = plugin.getCommand("hug");
		CommandExecutor exe;

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.modmode")) {
					s.sendMessage("§cYou don't have permission to do that!");
					return true;
				}
				if (args.length > 0) {
					s.sendMessage("§3Proper Usage: §5/modmode");
					return true;
				}
				Player p = (Player) s;
				Location loc = p.getLocation();
				if (modmodedb.containsKey(p.getName())) {
					p.teleport(modmodedb.get(p.getName()));
					s.sendMessage("§aSent back to your original location.");
					if (PlayerSuite.isVanished(p)) {
						vanishDb.remove(p.getName());
						s.sendMessage("§aYou have been unvanished.");
					}
					modmodedb.remove(p.getName());
					for(Player player: Bukkit.getOnlinePlayers()) {
						if ((player.hasPermission("eb.msg.spy"))) {
							player.sendMessage("§3" + p.getName() + " §a has left Mod Mode.");
						}
					}
					return true;
				}
				if (!modmodedb.containsKey(p.getName())) {
					modmodedb.put(p.getName(), loc);
					s.sendMessage("§aYou have turned Moderator Mode on.");
					s.sendMessage("§aYou can leave it by running the command again.");
					if (PlayerSuite.isVanished(p)) {
						s.sendMessage("§aYou are already vanished.");
					}
					if (!PlayerSuite.isVanished(p)) {
						vanishDb.add(p.getName());
						s.sendMessage("§aYou have been vanished.");
					}
					for(Player player: Bukkit.getOnlinePlayers()) {
						if ((player.hasPermission("eb.msg.spy"))) {
							player.sendMessage("§3" + p.getName() + "§a has entered Mod Mode.");
						}
					}
					return true;
				}
				return true;
			}
		}; modmode.setExecutor(exe);

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.hug")) {
					s.sendMessage("§cYou don't have permission to do that!");
					return true;
				}
				if (args.length != 1) {
					s.sendMessage("§3Proper Usage: §6/hug [Player]");
					return true;
				}
				Player p = Bukkit.getPlayer(args[0]);
				if (p == null) {
					s.sendMessage("§cThat player is not online.");
					return true;
				}
				Bukkit.getServer().broadcastMessage("§3" + s.getName() + " §2has hugged §3" + p.getName() + "§3! §d<3");
				return true;
			}
		}; hug.setExecutor(exe);


		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.gamemode")) {
					s.sendMessage("§cYou don't have permission to do that!");
				} else {
					final Player p;
					if (args.length >= 1) {
						if (!s.hasPermission("eb.gamemode.other")) {
							s.sendMessage("§cYou don't have permission to do that!");
						}
						p = Bukkit.getPlayer(args[0]);
					}
					else {
						if (!(s instanceof Player)) return false;
						p = (Player) s;
					}
					if (p == null) {
						s.sendMessage("§cThat player is not online.");
						return true;
					}

					p.setGameMode((p.getGameMode().equals(GameMode.CREATIVE))? GameMode.SURVIVAL : GameMode.CREATIVE);
					if (s != p) s.sendMessage("§aSet§e " + p.getName() + "'s §agamemode to§e" + Strings.toTitle(p.getGameMode().name()));
					return true;
				} return true;
			}
		}; gamemode.setExecutor(exe);

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.getpos")) {
					s.sendMessage("§cYou don't have permission to do that!");
				} else {
					if (!(s instanceof Player)) return false;

					Location loc = ((Player) s).getLocation();

					s.sendMessage("§aYour position:");
					s.sendMessage("§aWorld:§e " + loc.getWorld().getName());
					s.sendMessage(String.format("§aCoords: X:§e %1$s §aY:§e %2$s §aZ:§e %3$s", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
					return true;
				} return true;
			}
		}; getpos.setExecutor(exe);

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.god")) {
					s.sendMessage("§cYou don't have permission to do that!");
				} else {
					final Player p;
					if (args.length >= 1) {
						if (!s.hasPermission("eb.god.other")) {
							s.sendMessage("§cYou don't have permission to do that!");
							return true;
						}
						p = Bukkit.getPlayer(args[0]);
					} else {
						if (!(s instanceof Player)) return false;
						p = (Player) s;
					}
					if (p == null) {
						s.sendMessage("§cThat player is not online!");
						return true;
					}

					if (godDb.contains(p.getName())) godDb.remove(p.getName());
					else godDb.add(p.getName());

					final String action = (godDb.contains(p.getName()))? " enabled " : " disabled";
					if (p == s) s.sendMessage("§aGod mod" + action);
					else {
						s.sendMessage("§aGod mode" + action + "on§e " + p.getName());
						p.sendMessage("§aGod mode" + action);
					}
					return true;
				} return true;
			}
		}; god.setExecutor(exe);

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.hat")) {
					s.sendMessage("§cYou don't have permission to do that!");
					return true;
				} else {
					if (!(s instanceof Player)) return false;

					final Player p = (Player) s;
					final ItemStack oldhat = p.getInventory().getHelmet();
					if (oldhat == null) {
						final PlayerInventory inv = p.getInventory();
						final ItemStack hat = new ItemStack(p.getItemInHand().getType(), 1, p.getItemInHand().getDurability());

						if (hat.getTypeId() >= 256 || hat.getTypeId() == 0) {
							s.sendMessage("§cYou can't wear that! Please use a block.");
							return true;
						}

						inv.removeItem(hat);
						inv.setHelmet(hat);
						s.sendMessage("§aNice hat bro :O");
						return true;
					} else {
						s.sendMessage("§cTake off your old hat first.");
						return true;
					}
				} 
			}
		}; hat.setExecutor(exe);

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.help")) {
					s.sendMessage("§cYou don't have permission to do that!");
					return true;
				}

				String page = "default";
				if (args.length >= 1)
					page = args[0].toLowerCase();

				if (helpPagesDb.containsKey(page)) {
					for (String sent : helpPagesDb.get(page)) {
						s.sendMessage(sent.replaceAll("(?i)&([a-fk-or0-9])", "\u00A7$1"));
					}
				} else s.sendMessage("§cThat page does not exist.");
				return true;
			}
		}; help.setExecutor(exe);

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.vanish")) {
					s.sendMessage("§cYou don't have permission to do that!");
					return true;
				} else {
					s.sendMessage("§cThis command no longer exists.");
					s.sendMessage("§cTo vanish you must enter ModMode via §3/modmode");
					return true;
				}
			}
		}; vanish.setExecutor(exe);
//				if (!s.hasPermission("eb.vanish")) {
//					s.sendMessage("§cYou don't have permission to do that!");
//				} else {
//
//					final Player p;
//					if (args.length >= 1) p = Bukkit.getPlayer(args[0]);
//					else {
//						if (!(s instanceof Player)) return false;
//						p = (Player) s;
//					}
//					if (p == null) {
//						s.sendMessage("§cThat player is not online.");
//						return true;
//					}
//
//					if (!isVanished(p)) {
//						godDb.add(p.getName());
//						p.sendMessage("§aPoof!");
//					} else {
//						if (p.getAllowFlight()) {
//							s.sendMessage("§cYou can't unvanish right now, perhaps you're in creative mode?");
//							return true;
//						}
//						godDb.remove(p.getName());
//						p.sendMessage("§aYou are now visible.");
//					}
//					setVanished(p, !isVanished(p));
//					Utils.serverBroadcast("§e" + p.getName() + " §ahas " + (isVanished(p)? "vanished" : "reappeared"), "eb.vanish.alert");
//					return true;
//				} return true;
//			}
//		}; vanish.setExecutor(exe);

		exe = new CommandExecutor() {
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.workbench")) {
					s.sendMessage("§cYou don't have permission to do that!");
				} else {
					if (!(s instanceof Player)) return false;
					Player p = (Player) s;
					p.openWorkbench(null, true);
					p.sendMessage("§aOpened workbench.");
					return true;
				} return true;
			}
		}; workbench.setExecutor(exe);

		exe = new CommandExecutor() {
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.enchantingtable")) {
					s.sendMessage("§cYou don't have permission to do that!");
				} else {
					if (!(s instanceof Player)) return false;
					Player p = (Player) s;
					p.openEnchanting(null, true);
					p.sendMessage("§aHave an enchanting table.");
					return true;
				} return true;
			}
		}; enchantingtable.setExecutor(exe);

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				final Player p = (Player) s;
				if (!(s instanceof Player)) {
					s.sendMessage("§cThis command is only usable by players.");
				}
				if (!s.hasPermission("eb.displayname")) {
					s.sendMessage("§cYou don't have permission to do that!");
					return true;
				} if (args.length < 1) {
					s.sendMessage("§cNot enough arguments.");
					return true;
				} if (args.length > 1) {
					if (!s.hasPermission("eb.displayname.other")) {
						s.sendMessage("§cYou don't have permission to do that!");
						return true;
					}
					String displayName = args[1];
					String target = args[0];
					Player player = Bukkit.getServer().getPlayer(target);
					if (player == null) {
						s.sendMessage("§cThat player is not online.");
						return true;
					}
					player.setDisplayName(displayName);
					s.sendMessage("§aYou have set §3" + player.getName() + "'s §adisplay name to §3" + displayName);
					plugin.getConfig().set("displaynames." + p.getName(), displayName);
					plugin.saveConfig();
					return true;
				} else {
					String displayName = args[0];
					p.setDisplayName(displayName);
					s.sendMessage("§aYour display name is now: §3" + displayName);
					plugin.getConfig().set("displaynames." + p.getName(), displayName);
					plugin.saveConfig();
				}
				return true;
			}

		}; displayname.setExecutor(exe);

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.eb")) {
					s.sendMessage("§cYou don't have permission to do that!");
					return true;
				} if (args.length != 0) {
					s.sendMessage("§cToo many arguments.");
					return true;
				} else {
					s.sendMessage("§aEtriaBending reloaded.");
					plugin.reloadConfig();
				} return true;
			}
		}; eb.setExecutor(exe);

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.maintenance")) {
					s.sendMessage("§cYou don't have permission to do that!");
					return true;
				} else {
					if (!maintenanceon) {
						s.sendMessage("§aThe server has now been put into Maintenance Mode.");
						s.sendMessage("§aFrom this point on, unpermitted players will be kicked.");
						s.sendMessage("§aYou can disable maintenance mode by typing §3/maintenance§a.");

						Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
						for (Player p : players) {
							if ((!p.isOp()) && (!p.hasPermission("eb.maintenance.safe"))) {
								p.kickPlayer("Undergoing Maintenance");
							}
						}
						maintenanceon = true;
						return true;
					} else if (maintenanceon) {
						s.sendMessage("§aYour server is no longer undergoing maintenance mode.");
						maintenanceon = false;
						return true;
					}
				}
				return true;
			}
		}; maintenance.setExecutor(exe);

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.trade")) {
					s.sendMessage("§cYou don't have permission to do that!");
					return true;
				}
				if (args.length < 1) {
					s.sendMessage("§3Proper Usage: §6/trade [Player]");
					return true;
				}

				Player p = (Player) s;
				Player t = plugin.getServer().getPlayer(args[0]);
				if (t == null || PlayerSuite.isVanished(t)) {
					s.sendMessage("§cThat player is not online.");
					return true;
				}
				if (t.equals(p)) {
					s.sendMessage("§cYou can't trade with yourself!");
					return true;
				}
				if (t.getGameMode() != p.getGameMode()) {
					s.sendMessage("§cYou can't trade with someone who is in a different gamemode than you.");
					return true;
				}
				if (plugin.getConfig().getStringList("NoTrade").contains(p.getWorld().getName())) {
					s.sendMessage("§cYou can't open the trading interface in this world.");
					return true;
				}
				if (!t.getWorld().getName().equals(p.getWorld().getName())) {
					s.sendMessage("§cYou can't trade with someone who is in a different world than you.");
					return true;
				}
				Inventory inv = getTradeInv(p, t);
				if (inv != null) {
					p.sendMessage("§aResumed trading with §3" + t.getName() + "§a.");
					p.openInventory(inv);
					return true;
				}
				if (tradedb.containsKey(t.getName())) {
					inv = plugin.getServer().createInventory(null, 54, "Trade");
					p.sendMessage("§aOpened trading interface.");
					p.openInventory(inv);
					t.openInventory(inv);
					final HashMap<String, String> trade = new HashMap<String, String>();
					trade.put(p.getName(), t.getName());
					trades.put(trade, inv);
					tradedb.remove(t.getName());
					return true;
				} else {
					sendTradeRequest(t, p);
					p.sendMessage("§aSent a trade request to §3" + t.getName() + "§a.");
					return true;
				}	
			}
		}; trade.setExecutor(exe);
	}
}