package de.tl.DJ4ddi.LineXChat.Commands;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tl.DJ4ddi.LineXChat.Channel;
import de.tl.DJ4ddi.LineXChat.ConfigurationHandler;
import de.tl.DJ4ddi.LineXChat.Main;

public class ChannelCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("list")) {
				if (cs instanceof Player) {
					if (Main.permission.has(cs, "lnxchat.command.list") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp()) {
						cs.sendMessage(ChatColor.DARK_AQUA + "Your conversations:");
						for (Channel c : Main.getChannels(cs.getName())) {
							cs.sendMessage(ChatColor.AQUA + String.valueOf(c.getId()) + ChatColor.GRAY + ": " + c.getTargetString(cs.getName()));
						}
					} else {
						cs.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to list your channels.");
					}
				} else {
					cs.sendMessage("This command can not be used from console.");
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("listall")) {
				if (cs instanceof Player) {
					if (!(Main.permission.has(cs, "lnxchat.command.listall") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp())) {
						cs.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to list all channels.");
						return true;
					}
				}
				cs.sendMessage(ChatColor.DARK_AQUA + "Open channels:");
				for (Channel c : Main.getAllChannels()) {
					cs.sendMessage(ChatColor.AQUA + String.valueOf(c.getId()) + ChatColor.GRAY + ": " + c.getTargetString(""));
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("reload")) {
				if (cs instanceof Player) {
					if (!(Main.permission.has(cs, "lnxchat.command.reload") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp())) {
						cs.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to reload the plugin.");
						return true;
					}
				}
				Main.reload();
				ConfigurationHandler.reload();
				return true;
			}
			
			if (args[0].equalsIgnoreCase("arguments")) {
				if (cs instanceof Player) {
					if (!(Main.permission.has(cs, "lnxchat.command.arguments") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp())) {
						cs.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to list available arguments.");
						return true;
					}
				}
				cs.sendMessage(ChatColor.DARK_AQUA + "LineX Chat Help" + ChatColor.DARK_GRAY + " - Arguments");
				if (Main.permission.has(cs, "lnxchat.argument.group") || Main.permission.has(cs, "lnxchat.argument.all") || cs.isOp()) {
					cs.sendMessage(ChatColor.AQUA + "<Group>-g" + ChatColor.GRAY + " - Targets a group");
				} else {
					cs.sendMessage(ChatColor.GRAY + "<Group>-g - Targets a group");
				}
				if (Main.permission.has(cs, "lnxchat.argument.force") || Main.permission.has(cs, "lnxchat.argument.all") || cs.isOp()) {
					cs.sendMessage(ChatColor.AQUA + "-f" + ChatColor.GRAY + " - Focuses a user or group on the channel");
				} else {
					cs.sendMessage(ChatColor.GRAY + "-f - Focuses a user or group on the channel");
				}
				if (Main.permission.has(cs, "lnxchat.argument.silent") || Main.permission.has(cs, "lnxchat.argument.all") || cs.isOp()) {
					cs.sendMessage(ChatColor.AQUA + "-s" + ChatColor.GRAY + " - Suppresses all info messages");
				} else {
					cs.sendMessage(ChatColor.GRAY + "-s - Suppresses all info messages");
				}
				if (Main.permission.has(cs, "lnxchat.argument.anonym") || Main.permission.has(cs, "lnxchat.argument.all") || cs.isOp()) {
					cs.sendMessage(ChatColor.AQUA + "-a" + ChatColor.GRAY + " - Deactivates console logging");
				} else {
					cs.sendMessage(ChatColor.GRAY + "-a - Deactivates console logging");
				}
				return true;
			}
		}

		if (args.length == 2) {
			if (args[1].equalsIgnoreCase("close")) {
				if (cs instanceof Player) {
					if (!(Main.permission.has(cs, "lnxchat.command.close") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp())) {
						cs.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to close channels.");
						return true;
					}
				}
				try {
					Channel c = Main.getChannelBy(Integer.valueOf(args[0]));
					if (c != null) {
						c.sendCloseMessage();
						Main.closeChannel(c);
					} else {
						cs.sendMessage(ChatColor.GOLD + "Channel " + args[0] + ChatColor.GRAY + " was not found.");
					}
				} catch (NumberFormatException e) {
					cs.sendMessage(ChatColor.GOLD + args[0] + ChatColor.GRAY + " is not a valid channel ID.");
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("deny")) {
				if (cs instanceof Player) {
					if (!(Main.permission.has(cs, "lnxchat.command.deny") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp())) {
						cs.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to deny private messages.");
						return true;
					}
				}
				if (!Main.denied.contains(args[1].toLowerCase())) {
					Player p = Bukkit.getServer().getPlayer(args[1]);
					if (p != null) {
						Main.denied.add(p.getName().toLowerCase());
						cs.sendMessage(ChatColor.AQUA + p.getName() + ChatColor.GRAY + " was muted.");
						p.sendMessage(ChatColor.GOLD + "You are no longer allowed to send private messages.");
					} else {
						cs.sendMessage(ChatColor.GOLD + args[1] + ChatColor.GRAY + " is not online.");
					}
				} else {
					cs.sendMessage(ChatColor.GOLD + args[1] + ChatColor.GRAY + " is already muted.");
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("allow")) {
				if (cs instanceof Player) {
					if (!(Main.permission.has(cs, "lnxchat.command.deny") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp())) {
						cs.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to deny private messages.");
						return true;
					}
				}
				if (Main.denied.contains(args[1].toLowerCase())) {
					Main.denied.remove(args[1].toLowerCase());
					cs.sendMessage(ChatColor.AQUA + args[1] + ChatColor.GRAY + " is no longer muted.");
					Player p = Bukkit.getServer().getPlayer(args[1]);
					if (p != null) {
						p.sendMessage(ChatColor.GRAY + "You are now allowed to send private messages.");
					}
				} else {
					cs.sendMessage(ChatColor.GOLD + args[1] + ChatColor.GRAY + " is not muted.");
				}
				return true;
			}
		}

		if (args.length == 3) {
			if (args[1].equalsIgnoreCase("add")) {
				if (cs instanceof Player) {
					if (!(Main.permission.has(cs, "lnxchat.command.add") || Main.permission.has(cs, "lnxchat.command.addall") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp())) {
						cs.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to add users to channels.");
						return true;
					}
				}
				try {
					Channel c = Main.getChannelBy(Integer.valueOf(args[0]));
					if (c != null) {
						if (cs instanceof Player) {
							if (!c.getTargets().contains(cs.getName()) && !c.getTargets().contains(Main.chat.getPrimaryGroup((Player) cs))) {
								if (!(Main.permission.has(cs, "lnxchat.command.addall") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp())) {
									cs.sendMessage(ChatColor.GRAY + "You are not inside " + ChatColor.GOLD + "Channel " + c.getId() + ChatColor.GRAY + ".");
									return true;
								}
							}
						}
						Map<String, String> target = Main.parseTargets(new String[] {args[2]}, cs);
						for (Entry<String, String> t : target.entrySet()) {
							if (!t.getKey().equals(cs.getName())) {
								if (!t.getValue().contains("s")) {
									c.sendMessage(ChatColor.AQUA + t.getKey() + ChatColor.GRAY + " was added to the conversation.", cs);
								}
								c.addTarget(t.getKey());
							}
						}
						Main.forceFocus(target, c);
					} else {
						cs.sendMessage(ChatColor.GOLD + "Channel " + args[0] + ChatColor.GRAY + " was not found.");
					}
				} catch (NumberFormatException e) {
					cs.sendMessage(ChatColor.GOLD + args[0] + ChatColor.GRAY + " is not a valid channel ID.");
				}
				return true;
			}

			if (args[1].equalsIgnoreCase("remove")) {
				if (cs instanceof Player) {
					if (!(Main.permission.has(cs, "lnxchat.command.remove") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp())) {
						cs.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to remove users from channels.");
						return true;
					}
				}
				try {
					Channel c = Main.getChannelBy(Integer.valueOf(args[0]));
					if (c != null) {
						if (cs instanceof Player) {
							if (!c.getTargets().contains(cs.getName()) && !c.getTargets().contains(Main.chat.getPrimaryGroup((Player) cs))) {
								if (!(Main.permission.has(cs, "lnxchat.command.removeall") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp())) {
									cs.sendMessage(ChatColor.GRAY + "You are not inside " + ChatColor.GOLD + "Channel " + c.getId() + ChatColor.GRAY + ".");
									return true;
								}
							}
						}
						if (args[2].contains("-g")) {
							args[2] = "g:" + args[2].substring(0, args[2].length() - 2);
						}
						boolean silent = false;
						if (args[2].contains("-s")) {
							args[2] = args[2].substring(0, args[2].length() - 2);
							silent = true;
						}
						boolean found = false;
						for (String s : c.getTargets()) {
							if (s.equalsIgnoreCase(args[2])) {
								found = true;
								if (!silent) {
									c.sendMessage(ChatColor.AQUA + s + ChatColor.GRAY + " was removed from the conversation.", cs);
								}
								c.removeTarget(s);
								if (s.startsWith("g:")) {
									for (Player p : Bukkit.getServer().getOnlinePlayers()) {
										if (Main.permission.getPrimaryGroup(p).equals(s.substring(2))) {
											if (c.equals(Main.getFocus(p.getName()))) {
												Main.toggleChannel(p.getName());
											}
										}
									}
								} else if (c.equals(Main.getFocus(s))) {
									Main.toggleChannel(s);
								}
								break;
							}
						}
						if (!found) {
							cs.sendMessage(ChatColor.GOLD + args[2] + ChatColor.GRAY + " is not in this channel.");
						}
					} else {
						cs.sendMessage(ChatColor.GOLD + "Channel " + args[0] + ChatColor.GRAY + " was not found.");
					}
				} catch (NumberFormatException e) {
					cs.sendMessage(ChatColor.GOLD + args[0] + ChatColor.GRAY + " is not a valid channel ID.");
				}
				return true;
			}
		}

		cs.sendMessage(ChatColor.DARK_AQUA + "LineX Chat Help" + ChatColor.DARK_GRAY + " - Channel Commands");
		if (Main.permission.has(cs, "lnxchat.command.list") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp()) {
			cs.sendMessage(ChatColor.AQUA + "/ch list" + ChatColor.GRAY + " - Lists your current conversations");
		} else {
			cs.sendMessage(ChatColor.GRAY + "/ch list - Lists your current conversations.");
		}
		if (Main.permission.has(cs, "lnxchat.command.listall") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp()) {
			cs.sendMessage(ChatColor.AQUA + "/ch listall" + ChatColor.GRAY + " - Lists all open conversations");
		} else {
			cs.sendMessage(ChatColor.GRAY + "/ch listall - Lists all open conversations");
		}
		if (Main.permission.has(cs, "lnxchat.command.add") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp()) {
			cs.sendMessage(ChatColor.AQUA + "/ch <ID> add <Player>" + ChatColor.GRAY + " - Adds a user to the channel");
		} else {
			cs.sendMessage(ChatColor.GRAY + "/ch <ID> add <Player> - Adds a user to the channel");
		}
		if (Main.permission.has(cs, "lnxchat.command.remove") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp()) {
			cs.sendMessage(ChatColor.AQUA + "/ch <ID> remove <Player>" + ChatColor.GRAY + " - Removes a user from the channel");
		} else {
			cs.sendMessage(ChatColor.GRAY + "/ch <ID> remove <Player> - Removes a user from the channel");
		}
		if (Main.permission.has(cs, "lnxchat.command.close") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp()) {
			cs.sendMessage(ChatColor.AQUA + "/ch <ID> close" + ChatColor.GRAY + " - Closes the channel");
		} else {
			cs.sendMessage(ChatColor.GRAY + "/ch <ID> close - Closes the channel");
		}
		if (Main.permission.has(cs, "lnxchat.command.deny") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp()) {
			cs.sendMessage(ChatColor.AQUA + "/ch deny <Player>" + ChatColor.GRAY + " - Denies a user to create channels");
		} else {
			cs.sendMessage(ChatColor.GRAY + "/ch deny <Player> - Denies a user to create channels");
		}
		if (Main.permission.has(cs, "lnxchat.command.allow") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp()) {
			cs.sendMessage(ChatColor.AQUA + "/ch allow <Player>" + ChatColor.GRAY + " - Allows a user to create channels");
		} else {
			cs.sendMessage(ChatColor.GRAY + "/ch allow <Player> - Allows a user to create channels");
		}
		if (Main.permission.has(cs, "lnxchat.command.arguments") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp()) {
			cs.sendMessage(ChatColor.AQUA + "/ch arguments" + ChatColor.GRAY + " - Lists available arguments");
		} else {
			cs.sendMessage(ChatColor.GRAY + "/ch arguments - Lists available arguments.");
		}
		if (Main.permission.has(cs, "lnxchat.command.reload") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp()) {
			cs.sendMessage(ChatColor.AQUA + "/ch reload" + ChatColor.GRAY + " - Reloads the plugin");
		} else {
			cs.sendMessage(ChatColor.GRAY + "/ch reload - Reloads the plugin");
		}
		return true;
	}

}
