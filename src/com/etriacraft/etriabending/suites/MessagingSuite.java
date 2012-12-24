package com.etriacraft.etriabending.suites;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import com.etriacraft.etriabending.EtriaBending;
import com.etriacraft.etriabending.Strings;
import com.etriacraft.etriabending.util.Utils;

public class MessagingSuite {

	public static HashMap<CommandSender, CommandSender> chatterDb = new HashMap<CommandSender, CommandSender>();
	static EtriaBending plugin;

	public MessagingSuite(EtriaBending instance) {
		this.plugin = instance;
		init();
	}
	
	public static void showMotd(CommandSender s) {
		for (String mess : plugin.getConfig().getStringList("messaging.motd")) {
			mess = mess.replace("<name>", s.getName());
			mess = mess.replaceAll("(?i)&([a-fk-or0-9])", "\u00A7$1").replaceAll("<name>", s.getName());
			s.sendMessage(mess);
		}
	}

	private void init() {
		PluginCommand motd = plugin.getCommand("motd");
		PluginCommand message = plugin.getCommand("message");
		PluginCommand reply = plugin.getCommand("reply");
		PluginCommand modchat = plugin.getCommand("modchat");
		CommandExecutor exe;

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.motd")) {
					s.sendMessage("§cYou don't have permission to do that!");
				} else {
					for (String mess : plugin.getConfig().getStringList("messaging.motd")) {
						mess = mess.replace("<name>", s.getName());
						mess = mess.replaceAll("(?i)&([a-fk-or0-9])", "\u00A7$1").replaceAll("<name>", s.getName());
						s.sendMessage(mess);
					}
					return true;
				}
				return true;
			}
		}; motd.setExecutor(exe);

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.msg")) {
					s.sendMessage("§cYou don't have permission to do that!");
					return true;
				} else {
					final Player r = Bukkit.getPlayer(args[0]);
					if (r == null) {
						s.sendMessage("§cThat player is not online!");
						return true;
					}

					final String message = Strings.buildString(args, 1, " ");
					s.sendMessage("§a[§7You§a -> §7" + r.getName() + "§a] §e" + message);
					r.sendMessage("§a[§7" + s.getName() + "§a -> §7You§a] §e" + message);
					EtriaBending.log.info(String.format("[PM][%1$s -> %2$s] %3$s", s.getName(), r.getName(), message));

					chatterDb.put(s, r);
					chatterDb.put(r, s);

					for(Player player: Bukkit.getOnlinePlayers()) {
						if ((player.hasPermission("eb.msg.spy"))) {
							player.sendMessage("§3[Spy]§a[§7" + s.getName() + "§a -> §7" + r.getName() + "§a] §e" + message);
						}
					}
					return true;
				}
			}
		}; message.setExecutor(exe);

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!s.hasPermission("eb.reply")) {
					s.sendMessage("§cYou don't have permission to do that!");
					return true;
				} else {
					if (!chatterDb.containsKey(s)) {
						s.sendMessage("§cYou have no one to reply to!");
					} else {
						final CommandSender r = chatterDb.get(s);
						if (!Bukkit.getOfflinePlayer(r.getName()).isOnline()) {
							s.sendMessage("§7" + r.getName() + " §cis no longer online!");
							return true;
						}
						final String message = Strings.buildString(args, 0, " ");

						s.sendMessage("§a[§7You§a -> §7" + r.getName() + "§a] §e" + message);
						r.sendMessage("§a[§7" + s.getName() + "§a -> §7You§a] §e" + message);
						EtriaBending.log.info(String.format("[PM][%1$s -> %2$s] %3$s", s.getName(), r.getName(), message));

						chatterDb.put(r, s);

						for(Player player: Bukkit.getOnlinePlayers()) {
							if ((player.hasPermission("eb.msg.spy"))) {
								player.sendMessage("§3[Spy]§a[§7" + s.getName() + "§a -> §7" + r.getName() + "§a] §e" + message);
							}
						}
						return true;

					}
					return false;
				}
			}
		}; reply.setExecutor(exe);

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (args.length < 1) return false;
				if (!s.hasPermission("eb.modchat")) {
					s.sendMessage("§cYou don't have permission to do that!");
					return true;
				} else {
					String format = plugin.getConfig().getString("messaging.modchat");
					format = format.replace("<message>", Utils.buildString(args, 0)).replace("<name>", s.getName());
					format = Utils.colorize(format);

					for (Player player: Bukkit.getOnlinePlayers()) {
						if ((player.hasPermission("eb.modchat"))) {
							player.sendMessage(format);
						}
					}
					return true;
				}
			}
		}; modchat.setExecutor(exe);
	}

}