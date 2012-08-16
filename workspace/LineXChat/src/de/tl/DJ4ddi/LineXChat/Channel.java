package de.tl.DJ4ddi.LineXChat;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Channel {
	private ArrayList<String> targets = new ArrayList<String>();
	private Logger l;
	private boolean log;
	private long lastActive;
	private int id;

	public Channel (ArrayList<String> t, boolean log) {
		targets.addAll(t);
		id = Main.getFreeId();
		l = Bukkit.getServer().getLogger();
		this.log = log;
	}

	public void addTarget(String target) {
		targets.add(target);
	}

	public void removeTarget(String target) {
		targets.remove(target);
	}

	public ArrayList<String> getTargets() {
		return targets;
	}

	public String getTargetString(String exclude) {
		StringBuilder targets = new StringBuilder();
		for (String s : getTargets()) {
			if (s.startsWith("g:")) {
				targets.append(", [" + WordUtils.capitalize(s.substring(2)) + "]");
			} else if (s.startsWith("c:")) {
				targets.append(", " + s.substring(2));
			} else if (!s.equals(exclude)) {
				targets.append(", " + s);
			}
		}
		return targets.substring(2);
	}

	public void sendMessage(String msg, CommandSender sender) {
		if (sender instanceof Player) {
			sendMessage(msg, (Player) sender);
		} else {
			if (msg != null) {
				String message;
				if (isPrivateMessage()) {
					message = ConfigurationHandler.getConfig().getString("pmformat")
							.replace("{SENDER}", ConfigurationHandler.getConfig().getString("consolename"))
							.replace("{TARGET}", getTargetString(sender.getName()))
							.replace("{MSG}", msg)
							.replaceAll("(&([a-f0-9]))", "\u00A7$2");
				} else {
					message = ConfigurationHandler.getConfig().getString("channelformat")
							.replace("{ID}", getIdString(sender.getName()))
							.replace("{NAME}", ConfigurationHandler.getConfig().getString("consolename"))
							.replace("{PREFIX}", "")
							.replace("{SUFFIX}", "")
							.replace("{MSG}", msg)
							.replaceAll("(&([a-f0-9]))", "\u00A7$2");
				}
				sendRawMessage(message);
				lastActive = System.currentTimeMillis();
			}
		}
	}

	public void sendMessage(String msg, Player sender) {
		if (msg != null) {
			String message;
			if (isPrivateMessage()) {
				message = ConfigurationHandler.getConfig().getString("pmformat")
						.replace("{SENDER}", sender.getDisplayName())
						.replace("{TARGET}", getTargetString(sender.getName()))
						.replaceAll("(&([a-f0-9]))", "\u00A7$2")
						.replace("{MSG}", msg);
			} else {
				message = ConfigurationHandler.getConfig().getString("channelformat")
						.replace("{ID}", getIdString(sender.getName()))
						.replace("{NAME}", sender.getDisplayName())
						.replace("{PREFIX}", Main.chat.getGroupPrefix(sender.getWorld(), Main.chat.getPrimaryGroup(sender)) + Main.chat.getPlayerPrefix(sender))
						.replace("{SUFFIX}", Main.chat.getGroupSuffix(sender.getWorld(), Main.chat.getPrimaryGroup(sender)) + Main.chat.getPlayerSuffix(sender))
						.replaceAll("(&([a-f0-9]))", "\u00A7$2")
						.replace("{MSG}", msg);
			}
			if (Main.permission.has(sender, "lnxchat.color.channel") || Main.permission.has(sender, "lnxchat.color.all") || sender.isOp()) {
				message.replaceAll("(&([a-f0-9]))", "\u00A7$2");
			}
			sendRawMessage(message);
			lastActive = System.currentTimeMillis();
		}
	}
	
	public void sendRawMessage(String msg) {
		ArrayList<Player> receivers = new ArrayList<Player>();
		for (String s : targets) {
			if (s.startsWith("g:")) {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (Main.permission.getPrimaryGroup(p).equals(s.substring(2))) {
						if (!receivers.contains(p)) {
							receivers.add(p);
						}
					}
				}
			} else {
				Player p = Bukkit.getServer().getPlayer(s);
				if (p != null) {
					if (!receivers.contains(p)) {
						receivers.add(p);
					}
				}
			}
		}
		for (Player p : receivers) {
			p.sendMessage(msg);
		}
		if (log) {
			l.info(msg);
		}
	}
	
	public void sendCloseMessage() {
		ArrayList<Player> receivers = new ArrayList<Player>();
		for (String s : targets) {
			if (s.startsWith("g:")) {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (Main.permission.getPrimaryGroup(p).equals(s.substring(2))) {
						if (!receivers.contains(p)) {
							receivers.add(p);
						}
					}
				}
			} else {
				Player p = Bukkit.getServer().getPlayer(s);
				if (p != null) {
					if (!receivers.contains(p)) {
						receivers.add(p);
					}
				}
			}
		}
		for (Player p : receivers) {
			p.sendMessage(ChatColor.GOLD + "Your conversation with " + ChatColor.AQUA + getTargetString(p.getName()) + ChatColor.GOLD + " was closed.");
		}
		if (log) {
			l.info("Channel " + id + " was closed.");
		}
	}

	public long getLastActivity() {
		return lastActive;
	}

	public int getId() {
		return id;
	}

	public String getIdString(String exclude) {
		if (targets.size() == 1 && targets.get(0).startsWith("g:")) {
			return WordUtils.capitalize(targets.get(0).substring(2));
		}
		return String.valueOf(id);
	}

	public void setLogging(boolean log) {
		this.log = log;
	}

	public boolean getLogging() {
		return log;
	}

	public boolean isPrivateMessage() {
		if (targets.size() == 2 && !targets.get(0).startsWith("g:") && !targets.get(1).startsWith("g:")) {
			return true;
		}
		return false;
	}
}
