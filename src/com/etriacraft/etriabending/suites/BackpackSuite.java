package com.etriacraft.etriabending.suites;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.etriacraft.etriabending.sql.Database;
import com.etriacraft.etriabending.DBConnection;
import com.etriacraft.etriabending.EtriaBending;
import com.etriacraft.etriabending.sql.SQLite;
import com.etriacraft.etriabending.util.ItemStackSerialize;

public class BackpackSuite {

	public static EtriaBending plugin;
	public static Database sql;

	//Backpacks
	public static HashSet<String> backpackusers = new HashSet();
	public static HashMap<String, ItemStack[]> backpacks = new HashMap();

	// Snooping
	public static HashSet<String> snoopers = new HashSet();

	private static final int MAX_INVENTORY_ROWS = 6;
	private static final int INVENTORY_ROW_SIZE = 9;

	public static void BackpackInitialize() {
		sql = new SQLite(EtriaBending.log, "Using SQLite for Storage.", "backpacks.db", EtriaBending.getInstance().getDataFolder().getAbsolutePath());
		((SQLite) sql).open();
		
		if (!sql.tableExists("packs")) {
			EtriaBending.log.info("Creating backpacks table..");
			String query = "CREATE TABLE `packs` (`player` VARCHAR(32) PRIMARY KEY, `backpack` BLOB);";
			sql.modifyQuery(query);
		}
	}
//	public static void Backpack() {
//
//		if (sql == null || sql.getConnection() == null) {
//			setupDatabase();
//		}
//
//		if (!sql.tableExists("packs")) {
//			EtriaBending.log.info("Creating backpacks table..");
//			String query = "CREATE TABLE `packs` (`player` VARCHAR(32) PRIMARY KEY, `backpack` BLOB);";
//			sql.modifyQuery(query);
//		}
//
//		loadPackDb();
//
//	}
//
//	public static void setupDatabase() {
//		sql = new SQLite(EtriaBending.log, "Backpacks", "backpacks.db", plugin.getDataFolder().getAbsolutePath());
//		sql.open();
//	}

	public static void loadPackDb() {
		try {
			Statement stmt = sql.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM `packs`");

			while(rs.next()) {
				byte[] buf = rs.getBytes("backpack");
				String player = rs.getString("player");

				ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buf));
				Object o = ois.readObject();

				ItemStack[] iss = ItemStackSerialize.deserialize((List) o);

				backpacks.put(player, iss);
			}
		} catch (SQLException | IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void closePack(String player, Inventory inv) {
		backpackusers.remove(player);
		updatePack(player, inv.getContents());
	}

	// Saves backpacks
	private static void updatePack(String player, ItemStack[] iss) {
		backpacks.put(player, iss);
		List<Map<String, Object>> list = ItemStackSerialize.serialize(iss);

		writeToDb(list, player);

		EtriaBending.log.info("Saved backpack for " + player);
	}

	// Serializes and writes to table
	private static void writeToDb(List list, String player) {
		try {
			final PreparedStatement stmt = sql.getConnection().prepareStatement("INSERT OR REPLACE INTO `packs` (`player`, `backpack`) VALUES (?, ?);");
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final ObjectOutputStream oos = new ObjectOutputStream(baos);

			oos.writeObject(list);
			oos.close();

			stmt.setString(1, player);
			stmt.setBytes(2, baos.toByteArray());

			stmt.execute();
			stmt.close();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	public BackpackSuite(EtriaBending instance) {
		this.plugin = instance;
		init();
	}

	private void init() {
		PluginCommand backpack = plugin.getCommand("backpack");
		CommandExecutor exe;

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (!(s instanceof Player)) return false;
				if (!s.hasPermission("eb.backpack")) {
					s.sendMessage("§cYou don't have permission to do that!");
					return true;
				} else {
					final Player p = (Player) s;
					final int packSize = getPackSize(p);
					String packOwner = s.getName();

					final OfflinePlayer op;
					if (args.length >= 1 && s.hasPermission("eb.backpack.other")) {
						snoopers.add(s.getName());
						op = Bukkit.getOfflinePlayer(args[0]);

						if (!op.hasPlayedBefore()) {
							s.sendMessage("§cPlayer has not played before, therefore they do not have a backpack.");
							return true;
						}

						packOwner = op.getName();
					}

					final Inventory i = Bukkit.createInventory(p, packOwner.equals(s.getName())? packSize : MAX_INVENTORY_ROWS * INVENTORY_ROW_SIZE, packSize > 9? "Backpack" : "Satchel");

					if (backpacks.containsKey(packOwner)) {
						setInventoryContents(i, backpacks.get(packOwner));
					} else {
						EtriaBending.log.info("Creating a new backpack for " + packOwner);
					}
					p.openInventory(i);

					backpackusers.add(p.getName());
					return true;
				}

			}
		}; backpack.setExecutor(exe);
	}

	public static boolean pack(CommandSender s, String[] args) {
		backpackusers.add(s.getName());
		return true;
	}

	private static int getPackSize(Player p) {
		int size = 0;
		for (int i = 1; i <= MAX_INVENTORY_ROWS; i++) {
			if (p.hasPermission("ec.backpack.size." + i)) size = i * INVENTORY_ROW_SIZE;
		}
		return size;
	}

	public static boolean getPackIsProtected(Player p) {
		return p.hasPermission("ec.backpack.protected");
	}

	public static void clearPack(String p) {
		if (backpackusers.contains(p)) {
			Bukkit.getPlayerExact(p).closeInventory();
		}

		updatePack(p, new ItemStack[0]);
	}

	private static void setInventoryContents(Inventory inv, ItemStack[] iss) {
		if (inv.getSize() >= iss.length) {
			inv.setContents(iss);
		} else {
			int index = 0;
			for (int i = 0; i < iss.length; i++) {
				if (iss[i] != null) {
					inv.setItem(i, iss[i]);
					index++;
				}

				if (index == inv.getSize()) break;
			}
		}
	}

	public static void shutdown() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (backpackusers.contains(p.getName())) {
				if (p.getOpenInventory().getTitle().equalsIgnoreCase("Satchel")
						|| p.getOpenInventory().getTitle().equalsIgnoreCase("Backpack")) {
					closePack(p.getName(), p.getOpenInventory().getTopInventory());
					p.closeInventory();
				}
			}
		}
		sql.close();
	}
}
