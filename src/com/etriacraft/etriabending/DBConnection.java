package com.etriacraft.etriabending;

import com.etriacraft.etriabending.sql.Database;
import com.etriacraft.etriabending.sql.SQLite;

public class DBConnection {
	
	public static Database sql;
	public static String sqlite_db;
	
	public static void initialize() {
		sql = new SQLite(EtriaBending.log, "Using SQLite for Storage.", sqlite_db, EtriaBending.getInstance().getDataFolder().getAbsolutePath());
		((SQLite) sql).open();
		
		if (!sql.tableExists("player_homes")) {
			String query = "CREATE TABLE `player_homes` ("
					+ "`owner` TEXT NOT NULL,"
					+ "`name` TEXT NOT NULL,"
					+ "`world` TEXT NOT NULL,"
					+ "`x` DOUBLE NOT NULL,"
					+ "`y` DOUBLE NOT NULL,"
					+ "`z` DOUBLE NOT NULL,"
					+ "`pitch` FLOAT NOT NULL,"
					+ "`yaw` FLOAT NOT NULL);";
			sql.modifyQuery(query);
			EtriaBending.log.info("Created player_homes table.");
		}
	}

}
