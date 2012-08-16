package de.tl.DJ4ddi.LineXChat.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tl.DJ4ddi.LineXChat.ConfigurationHandler;
import de.tl.DJ4ddi.LineXChat.Main;

public class FakeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("quit")) {
				if (cs instanceof Player) {
					Player p = (Player) cs;
					if (Main.permission.has(cs, "lnxchat.fake.quit") || Main.permission.has(cs, "lnxchat.fake.all") || cs.isOp()) {
						if (Main.quit.contains(p.getName())) {
							Main.quit.remove(p.getName());
							String format = ConfigurationHandler.getConfig().getString("joinformat");
							if (!format.equalsIgnoreCase("false")) {
								Bukkit.getServer().broadcastMessage(format
										.replace("{PLAYERS}", String.valueOf(Bukkit.getServer().getOnlinePlayers().length - Main.quit.size()))
										.replace("{NAME}", p.getDisplayName().replaceAll("(&([a-f0-9]))", ""))
										.replaceAll("(&([a-f0-9]))", "\u00A7$2"));
							} else {
								Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + p.getDisplayName().replaceAll("(&([a-f0-9]))", "") + " joined the game.");
							}
						} else {
							Main.quit.add(p.getName());
							String format = ConfigurationHandler.getConfig().getString("leaveformat");
							if (!format.equalsIgnoreCase("false")) {
								Bukkit.getServer().broadcastMessage(format
										.replace("{PLAYERS}", String.valueOf(Bukkit.getServer().getOnlinePlayers().length - Main.quit.size()))
										.replace("{NAME}", p.getDisplayName().replaceAll("(&([a-f0-9]))", ""))
										.replaceAll("(&([a-f0-9]))", "\u00A7$2"));
							} else {
								Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + p.getDisplayName().replaceAll("(&([a-f0-9]))", "") + " left the game.");
							}
						}
					} else {
						cs.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to fakequit.");
					}
				} else {
					cs.sendMessage("This command can not be used from console.");
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("name")) {
				if (cs instanceof Player) {
					if (Main.permission.has(cs, "lnxchat.fake.name") || Main.permission.has(cs, "lnxchat.fake.all") || cs.isOp()) {
						Main.nick.remove(cs.getName());
						cs.sendMessage(ChatColor.GRAY + "Your name will be changed to " + ChatColor.AQUA + cs.getName() + ChatColor.GRAY + " the next time you log in.");
					} else {
						cs.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to change your name.");
					}
				} else {
					cs.sendMessage("This command can not be used from console.");
				}
				return true;
			}
		}
		
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("name")) {
				if (cs instanceof Player) {
					if (Main.permission.has(cs, "lnxchat.fake.name") || Main.permission.has(cs, "lnxchat.fake.all") || cs.isOp()) {
						Main.nick.remove(cs.getName());
						if (Main.permission.has(cs, "lnxchat.color.name") || Main.permission.has(cs, "lnxchat.color.all") || cs.isOp()) {
							Main.nick.put(cs.getName(), args[1] + ChatColor.RESET);
							cs.sendMessage(ChatColor.GRAY + "Your name will be changed to " + ChatColor.AQUA + args[1].replaceAll("(&([a-f0-9]))", "\u00A7$2") + ChatColor.GRAY + " the next time you log in.");
						} else {
							Main.nick.put(cs.getName(), args[1]);
							cs.sendMessage(ChatColor.GRAY + "Your name will be changed to " + ChatColor.AQUA + args[1] + ChatColor.GRAY + " the next time you log in.");
						}
					} else {
						cs.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to change your name.");
					}
				} else {
					cs.sendMessage("This command can not be used from console.");
				}
				return true;
			}
		}
		
		cs.sendMessage(ChatColor.DARK_AQUA + "LineX Chat Help" + ChatColor.DARK_GRAY + " - Fake Commands");
		if (Main.permission.has(cs, "lnxchat.fake.name") || Main.permission.has(cs, "lnxchat.fake.all") || cs.isOp()) {
			cs.sendMessage(ChatColor.AQUA + "/fake name <Name>" + ChatColor.GRAY + " - Changes your name for your next login");
		} else {
			cs.sendMessage(ChatColor.GRAY + "/fake name <Name> - Changes the name for your next login");
		}
		if (Main.permission.has(cs, "lnxchat.fake.quit") || Main.permission.has(cs, "lnxchat.fake.all") || cs.isOp()) {
			cs.sendMessage(ChatColor.AQUA + "/fake quit" + ChatColor.GRAY + " - Fakes a quit or join message");
		} else {
			cs.sendMessage(ChatColor.GRAY + "/fake quit - Fakes a quit or join message");
		}
		return true;
	}
}