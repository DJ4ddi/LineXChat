package de.tl.DJ4ddi.LineXChat.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tl.DJ4ddi.LineXChat.ConfigurationHandler;
import de.tl.DJ4ddi.LineXChat.Main;

public class BroadcastCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (cs instanceof Player) {
			if (!(Main.permission.has(cs, "lnxchat.command.broadcast") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp())) {
				cs.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to broadcast messages.");
				return true;
			}
		}
		StringBuilder message = new StringBuilder();
		for (String s : args) {
			message.append(" " + s);
		}
		if (ConfigurationHandler.getConfig().getBoolean("saytobroadcast") || cs instanceof Player) {
			Bukkit.getServer().broadcastMessage(ConfigurationHandler.getConfig().getString("broadcastformat")
					.replace("{MSG}", message.substring(1))
					.replaceAll("(&([a-f0-9]))", "\u00A7$2"));
		} else {
			Bukkit.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "<Server> " + message.substring(1));
		}
		return true;
	}
}