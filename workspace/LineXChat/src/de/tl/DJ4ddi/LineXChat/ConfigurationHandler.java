package de.tl.DJ4ddi.LineXChat;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigurationHandler {
	private static File configpath = new File("plugins/LineXChat/config.yml");
	private static FileConfiguration config = YamlConfiguration.loadConfiguration(configpath);
	
	public static void updateConfig() {
		if (!config.isSet("chatformat")) {
			config.set("chatformat", "<{NAME}> {MSG}");
		}
		if (!config.isSet("channelformat")) {
			config.set("channelformat", "&8[&3{ID}&8] &f<{NAME}> {MSG}");
		}
		if (!config.isSet("pmformat")) {
			config.set("pmformat", "&8[&3{SENDER} to {TARGET}&8] &f{MSG}");
		} 
		if (!config.isSet("listformat")) {
			config.set("listformat", "{PREFIX}{NAME}{SUFFIX}");
		}
		if (!config.isSet("broadcastformat")) {
			config.set("broadcastformat", "&c[Broadcast] &6{MSG}");
		}
		if (!config.isSet("joinformat")) {
			config.set("joinformat", "&8(&6{PLAYERS}&8) &3{NAME} &7joined.");
		}
		if (!config.isSet("leaveformat")) {
			config.set("leaveformat", "&8(&6{PLAYERS}&8) &3{NAME} &7left.");
		}
		if (!config.isSet("kickformat")) {
			config.set("kickformat", "&8(&6{PLAYERS}&8) &3{NAME} &4was kicked.");
		}
		if (!config.isSet("groupformat")) {
			config.set("groupformat", "&8[{PREFIX}{GROUPFLU}&8] - &7{LIST}");
		}
		if (!config.isSet("consolename")) {
			config.set("consolename", "Server");
		}
		if (!config.isSet("saytobroadcast")) {
			config.set("saytobroadcast", true);
		}
		if (!config.isSet("usetagapi")) {
			config.set("usetagapi", true);
		}
		if (!config.isSet("timeout")) {
			config.set("timeout", 10);
		}
		saveConfig();
	}
	
	public static FileConfiguration getConfig() {
		return config;
	}
	
	public static void reload() {
		saveConfig();
		config = YamlConfiguration.loadConfiguration(configpath);
		updateConfig();
	}
	
	public static void saveConfig() {
		try {
			config.save(configpath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}