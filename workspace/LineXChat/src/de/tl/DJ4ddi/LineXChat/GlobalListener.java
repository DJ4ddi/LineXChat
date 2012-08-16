package de.tl.DJ4ddi.LineXChat;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class GlobalListener implements Listener {
	private Plugin plugin;

	public GlobalListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerJoin (PlayerJoinEvent e) {
		Player p = e.getPlayer();
		// Check fake name
		if (Main.nick.containsKey(p.getName())) {
			if (Main.permission.has(p, "lnxchat.color.name") || Main.permission.has(p, "lnxchat.color.all") || p.isOp()) {
				p.setDisplayName(Main.nick.get(p.getName()).replaceAll("(&([a-f0-9]))", "\u00A7$2"));
			} else {
				p.setDisplayName(Main.nick.get(p.getName()));
			}
		}
		// Set list name
		String format = ConfigurationHandler.getConfig().getString("listformat");
		if (!format.equalsIgnoreCase("false")) {
			String name = format
					.replace("{PREFIX}", Main.chat.getGroupPrefix(p.getWorld(), Main.chat.getPrimaryGroup(p)) + Main.chat.getPlayerPrefix(p))
					.replace("{SUFFIX}", Main.chat.getGroupSuffix(p.getWorld(), Main.chat.getPrimaryGroup(p)) + Main.chat.getPlayerSuffix(p))
					.replace("{NAME}", p.getDisplayName())
					.replaceAll("(&([a-f0-9]))", "\u00A7$2");
			if (name.length() > 16) {
				name = name.substring(0, 16);
			}
			p.setPlayerListName(name);
		}
		if (e.getJoinMessage() != null) {
			// Set join message
			String jformat = ConfigurationHandler.getConfig().getString("joinformat");
			if (!jformat.equalsIgnoreCase("false")) {
				e.setJoinMessage(jformat
						.replace("{PLAYERS}", String.valueOf(Bukkit.getServer().getOnlinePlayers().length - Main.quit.size()))
						.replace("{NAME}", p.getDisplayName().replaceAll("(&([a-f0-9]))", ""))
						.replaceAll("(&([a-f0-9]))", "\u00A7$2"));
			}
		}
	}

	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerQuit (final PlayerQuitEvent e) {
		// Schedule channel removal
		plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
			public void run() {
				String pname = e.getPlayer().getName();
				if (Bukkit.getServer().getPlayer(pname) == null) {
					if (Main.getFocus(pname) != null) {
						Main.toggleChannel(pname);
					}
					ArrayList<Channel> deadchannels = new ArrayList<Channel>();
					for (Channel c : Main.getChannels(pname)) {
						ArrayList<String> targets = c.getTargets();
						int players = 0;
						for (String s : targets) {
							if (s.startsWith("g:")) {
								for (Player p : Bukkit.getServer().getOnlinePlayers()) {
									if (Main.permission.getPrimaryGroup(p).equals(s.substring(2))) {
										players++;
									}
								}
							} else if (s.startsWith("c:")) {
								players++;
							} else {
								if (Bukkit.getServer().getPlayer(s) != null) {
									players++;
								}
							}
						}
						if (players < 2) {
							deadchannels.add(c);
						}
					}
					for (Channel c : deadchannels) {
						Main.closeChannel(c);
						c.sendCloseMessage();
					}
				}
			}
		}, ConfigurationHandler.getConfig().getLong("timeout") * 1200);
		if (e.getQuitMessage() != null) {
			// Set quit message
			String format = ConfigurationHandler.getConfig().getString("leaveformat");
			if (!format.equalsIgnoreCase("false")) {
				e.setQuitMessage(format
						.replace("{PLAYERS}", String.valueOf(Bukkit.getServer().getOnlinePlayers().length - Main.quit.size()))
						.replace("{NAME}", e.getPlayer().getDisplayName().replaceAll("(&([a-f0-9]))", ""))
						.replaceAll("(&([a-f0-9]))", "\u00A7$2"));
			}
		}
	}

	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerKick (PlayerKickEvent e) {
		if (e.getLeaveMessage() != null) {
			// Set kick message
			String format = ConfigurationHandler.getConfig().getString("kickformat");
			if (!format.equalsIgnoreCase("false")) {
				e.setLeaveMessage(format
						.replace("{PLAYERS}", String.valueOf(Bukkit.getServer().getOnlinePlayers().length - Main.quit.size()))
						.replace("{NAME}", e.getPlayer().getDisplayName().replaceAll("(&([a-f0-9]))", ""))
						.replace("{REASON}", e.getReason())
						.replaceAll("(&([a-f0-9]))", "\u00A7$2"));
			}
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerChat (AsyncPlayerChatEvent e) {
		if (e.getMessage() != null) {
			Player p = e.getPlayer();
			if (e.getMessage().startsWith("@")) {
				e.setCancelled(true);
				String tmp = e.getMessage();
				String pname = p.getName();
				if (tmp.trim().equals("@")) {
					// Toggle between last channel and global
					p.sendMessage(Main.toggleChannel(pname));
				} else {
					if (tmp.startsWith("@ ")) {
						// Send message to last channel
						Channel c = Main.getLastChannel(pname);
						if (c != null) {
							c.sendMessage(tmp.substring(2), p);
						} else {
							p.sendMessage(ChatColor.GRAY + "No conversations found.");
						}
					} else {
						// Create new or find existing channel
						if ((Main.permission.has(p, "lnxchat.command.tell") || Main.permission.has(p, "lnxchat.command.all") || p.isOp()) && !Main.denied.contains(p.getName())) {
							String[] targets = null;
							String msg = null;
							if (tmp.contains(" ")) {
								targets = tmp.substring(1, tmp.indexOf(" ")).split(",");
								msg = tmp.substring(tmp.indexOf(" ") + 1);
							} else {
								targets = tmp.substring(1).split(",");
							}
							Channel c = Main.parseChannel(targets, p);
							if (c != null) {
								c.sendMessage(msg, p);
							}
						} else {
							p.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to send private messages.");
						}
					}
				}
				e.setMessage(null);
			} else {
				if (Main.getFocus(p.getName()) == null) {
					// Send message to global
					String format = ConfigurationHandler.getConfig().getString("chatformat");
					if (!format.equalsIgnoreCase("false")) {
						e.setFormat(format
								.replace("{NAME}", p.getDisplayName())
								.replace("{PREFIX}", Main.chat.getGroupPrefix(p.getWorld(), Main.chat.getPrimaryGroup(p)) + Main.chat.getPlayerPrefix(p))
								.replace("{SUFFIX}", Main.chat.getGroupSuffix(p.getWorld(), Main.chat.getPrimaryGroup(p)) + Main.chat.getPlayerSuffix(p))
								.replaceAll("(&([a-f0-9]))", "\u00A7$2")
								.replace("{MSG}", "%2$s"));
					}
					if (Main.permission.has(p, "lnxchat.color.global") || Main.permission.has(p, "lnxchat.color.all") || p.isOp()) {
						e.setMessage(e.getMessage().replaceAll("(&([a-f0-9]))", "\u00A7$2"));
					}
				} else {
					// Send message to focus
					e.setCancelled(true);
					Main.getFocus(p.getName()).sendMessage(e.getMessage(), p);
					e.setMessage(null);
				}
			}
		}
	}
}