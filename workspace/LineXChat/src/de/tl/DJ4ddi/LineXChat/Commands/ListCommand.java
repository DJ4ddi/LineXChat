package de.tl.DJ4ddi.LineXChat.Commands;

import java.util.ArrayList;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tl.DJ4ddi.LineXChat.ConfigurationHandler;
import de.tl.DJ4ddi.LineXChat.Main;

public class ListCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (cs instanceof Player) {
			if (!(Main.permission.has(cs, "lnxchat.command.who") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp())) {
				cs.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to list players.");
				return true;
			}
		}
		StringBuilder overview = new StringBuilder();
		ArrayList<String> groups = new ArrayList<String>();
		for (String s : Main.permission.getGroups()) {
			int gcount = 0;
			int count = 0;
			StringBuilder list = new StringBuilder();
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				gcount++;
				if (Main.permission.getPrimaryGroup(p).equals(s)) {
					if (!Main.quit.contains(p.getName())) {
						count++;
						if (Main.permission.has(cs, "lnxchat.command.adminwho") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp()) {
							list.append(", " + p.getName());
						} else {
							list.append(", " + p.getDisplayName().replaceAll("(&([a-f0-9]))", ""));
						}
					} else if (Main.permission.has(cs, "lnxchat.command.adminwho") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp()) {
						list.append(", " + p.getName());
					}
				}
			}
			if (list.length() > 0) {
				list.delete(0, 2);
				StringBuilder group = new StringBuilder(ConfigurationHandler.getConfig().getString("groupformat")
						.replace("{LIST}", list.toString())
						.replace("{GROUP}", s)
						.replace("{GROUPFLU}", WordUtils.capitalize(s))
						.replace("{FLU}", s.substring(0, 1).toUpperCase())
						.replace("{PREFIX}", Main.chat.getGroupPrefix(Bukkit.getServer().getWorlds().get(0), s))
						.replace("{SUFFIX}", Main.chat.getGroupSuffix(Bukkit.getServer().getWorlds().get(0), s))
						.replaceAll("(&([a-f0-9]))", "\u00A7$2"));
				groups.add(group.toString());
			}
			if (overview.length() == 0) {
				overview.append("(" + (gcount - Main.quit.size()) + ")" + ChatColor.DARK_GRAY + " - ");
			}
			String prefix = Main.chat.getGroupPrefix(Bukkit.getServer().getWorlds().get(0), s);
			int cc = prefix.lastIndexOf('&');
			if (cc > -1) {
				overview.append("[" + prefix.substring(cc, cc + 2).replaceAll("(&([a-f0-9]))", "\u00A7$2") + s.substring(0, 1).toUpperCase() + ":" + count + ChatColor.DARK_GRAY + "]");
			} else {
				overview.append("[" + ChatColor.WHITE + s.substring(0, 1).toUpperCase() + ":" + count + ChatColor.DARK_GRAY + "]");
			}
		}
		cs.sendMessage(overview.toString());
		for (String s : groups) {
			cs.sendMessage(s);
		}
		return true;
	}
}