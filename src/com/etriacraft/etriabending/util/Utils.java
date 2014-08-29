package com.etriacraft.etriabending.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class Utils {

	public static ItemStack parseItemStack(String str) {
		String name, data = "0", quan = "1";
		if (str.contains(":")) {
			String[] info = str.split(":");
			name = info[0];
			quan = info[1];
			data = (info.length >= 3)? info[2] : "0";
		} else {
			name = str;
		}

		short meta;
		int amnt;
		try {
			meta = Short.parseShort(data);
			amnt = Integer.parseInt(quan);
		} catch (NumberFormatException e) {
			return null;
		}

		Material mat = Material.matchMaterial(name);
		if (mat == null)
			return null;

		ItemStack is = new ItemStack(mat.getId(), amnt, meta);
		return is;
	}

	public static float xpForLevel(int level) {
		return (float) Math.ceil((1.75 * Math.pow(level, 2)) + (5 * level));
	}

	public static void serverBroadcast(String message, String perm) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (perm == null || p.hasPermission(perm)) p.sendMessage(message);
		}
		Bukkit.getConsoleSender().sendMessage(message);
	}

	public static String getEnchantmentList(ItemStack iss) {
		StringBuilder sb = new StringBuilder();
		for (Enchantment ench : Enchantment.values()) {
			if (iss != null && !ench.canEnchantItem(iss)) continue;
			if (sb.length() != 0) sb.append("§a,§e ");
			sb.append("§e").append(ench.getName().toLowerCase());
		}
		return sb.toString();
	}

	public static String buildString(String[] args, int begin) {
		StringBuilder mess = new StringBuilder();
		for (int i = begin; i < args.length; i++) {
			if (i > begin) {
				mess.append(" ");
			}
			mess.append(args[i]);
		}
		return mess.toString().trim();
	}
	
	public static String colorize(String message) {
        return message.replaceAll("(?i)&([a-fk-or0-9])", "\u00A7$1");
    }

}