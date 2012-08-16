package de.tl.DJ4ddi.LineXChat;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.tl.DJ4ddi.LineXChat.Commands.ChannelCommand;
import de.tl.DJ4ddi.LineXChat.Commands.FakeCommand;
import de.tl.DJ4ddi.LineXChat.Commands.ListCommand;
import de.tl.DJ4ddi.LineXChat.Commands.PrivateMessageCommand;
import de.tl.DJ4ddi.LineXChat.Commands.ReplyCommand;
import de.tl.DJ4ddi.LineXChat.Commands.BroadcastCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Main extends JavaPlugin {

	public static Permission permission = null;
	public static Chat chat = null;

	private static Map<String, Channel> focus = new HashMap<String, Channel>();
	private static ArrayList<Channel> channels = new ArrayList<Channel>();

	public static ArrayList<String> denied = new ArrayList<String>();
	public static ArrayList<String> quit = new ArrayList<String>();

	public static Map<String, String> nick = new HashMap<String, String>();

	@Override
	public void onEnable() {
		getLogger().info("Loading...");
		getServer().getPluginManager().registerEvents(new GlobalListener(this), this);
		setupPermissions();
		setupChat();
		ConfigurationHandler.updateConfig();
		getCommand("tell").setExecutor(new PrivateMessageCommand());
		getCommand("ch").setExecutor(new ChannelCommand());
		getCommand("r").setExecutor(new ReplyCommand());
		getCommand("say").setExecutor(new BroadcastCommand());
		getCommand("fake").setExecutor(new FakeCommand());
		getCommand("list").setExecutor(new ListCommand());
		if (getServer().getPluginManager().getPlugin("TagAPI") != null && ConfigurationHandler.getConfig().getBoolean("usetagapi")) {
			getServer().getPluginManager().registerEvents(new TagListener(), this);
			getLogger().info("Hooked TagAPI");
		}
		getLogger().info("Enabled.");
	}

	private boolean setupPermissions()
	{
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

	private boolean setupChat()
	{
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}

		return (chat != null);
	}

	@Override
	public void onDisable() {
		getLogger().info("Disabled.");
	}

	public static void reload() {
		focus.clear();
		channels.clear();
		denied.clear();
		nick.clear();
		quit.clear();
	}

	public static Channel parseChannel(String[] targets, CommandSender sender) {
		Map<String, String> finaltargets = parseTargets(targets, sender);
		Channel ch = null;
		if (!finaltargets.isEmpty()) {
			boolean log = true;
			String args = null;
			for (Entry<String, String> s : finaltargets.entrySet()) {
				if (s.getValue().contains("i")) {
					ch = getChannelBy(Integer.valueOf(s.getKey()));
					args = s.getValue().replace("i", "");
				}
				if (s.getValue().contains("a")) {
					log = false;
				}
			}
			if (ch == null) {
				// Find existing channel
				check:
					for (Channel c : getChannels(sender.getName())) {
						for (String s : finaltargets.keySet()) {
							if (!c.getTargets().contains(s)) {
								continue check;
							}
						}
						for (String s : c.getTargets()) {
							if (!finaltargets.containsKey(s)) {
								continue check;
							}
						}
						ch = c;
						if (c.getLogging() != log) {
							c.setLogging(log);
						}
						break;
					}
				if (ch == null) {
					// Create new channel
					ch = new Channel(new ArrayList<String>(finaltargets.keySet()), log);
					channels.add(ch);
				}
				// Execute force & silent
				forceFocus(finaltargets, ch);
			} else {
				Map<String, String> channel = new HashMap<String, String>();
				for (String s : ch.getTargets()) {
					channel.put(s, args);
				}
				forceFocus(channel, ch);
			}
		}
		return ch;
	}

	public static Map<String, String> parseTargets(String[] targets, CommandSender sender) {
		Map<String, String> finaltargets = new HashMap<String, String>();
		boolean owngroup = false;
		finalize:
			for (String s : targets) {
				String finaltarget = s;
				StringBuilder arguments = new StringBuilder();
				// Check group argument
				if (s.contains("-g")) {
					if (Main.permission.has(sender, "lnxchat.argument.group") || Main.permission.has(sender, "lnxchat.argument.all") || sender.isOp()) {
						finaltarget = "g:" + finaltarget.substring(0, finaltarget.length() -2);
					} else {
						sender.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to contact groups.");
						continue;
					}
				}
				// Check silent argument
				if (s.contains("-s")) {
					if (Main.permission.has(sender, "lnxchat.argument.silent") || Main.permission.has(sender, "lnxchat.argument.all") || sender.isOp()) {
						arguments.append("s");
					} else {
						sender.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to force silence.");
					}
					finaltarget = finaltarget.substring(0, finaltarget.length() - 2);
				}
				// Check force argument
				if (s.contains("-f")) {
					if (Main.permission.has(sender, "lnxchat.argument.force") || Main.permission.has(sender, "lnxchat.argument.all") || sender.isOp()) {
						arguments.append("f");
					} else {
						sender.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to use the force.");
					}
					finaltarget = finaltarget.substring(0, finaltarget.length() - 2);
				}
				// Check anonym argument
				if (s.contains("-a")) {
					if (Main.permission.has(sender, "lnxchat.argument.anonym") || Main.permission.has(sender, "lnxchat.argument.all") || sender.isOp()) {
						arguments.append("a");
					} else {
						sender.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to make channels anonym.");
					}
					finaltarget = finaltarget.substring(0, finaltarget.length() - 2);
				}
				boolean found = false;
				if (!finaltarget.startsWith("g:")) {
					// Find player
					boolean self = false;
					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						if (p.getName().toLowerCase().startsWith(finaltarget.toLowerCase())) {
							if (found && !p.getName().equalsIgnoreCase(finaltarget)) {
								sender.sendMessage(ChatColor.GRAY + "More than one player was found for " + ChatColor.GOLD + finaltarget + ChatColor.GRAY + ".");
								continue finalize;
							} else if (p.getName().equals(sender.getName())) {
								self = true;
								continue;
							} else {
								found = true;
								self = false;
								if (p.getName().equalsIgnoreCase(finaltarget)) {
									finaltarget = p.getName();
									break;
								}
								finaltarget = p.getName();
							}
						}
					}
					if (self) {
						sender.sendMessage(ChatColor.GRAY + "You can not chat with yourself.");
						continue finalize;
					}
					if (!found) {
						// Check for existing channel ID
						if (targets.length == 1) {
							try {
								int id = Integer.valueOf(finaltarget);
								for (Channel c : getChannels(sender.getName())) {
									if (c.getId() == id) {
										finaltargets.put(finaltarget, arguments.append("i").toString());
										return finaltargets;
									}
								}
							} catch (NumberFormatException e) {
							}
						}
						sender.sendMessage(ChatColor.GRAY + "The player " + ChatColor.GOLD + finaltarget + ChatColor.GRAY + " was not found.");
					}
				} else {
					// Find group
					for (String g : Main.chat.getGroups()) {
						if (g.toLowerCase().startsWith(finaltarget.substring(2).toLowerCase())) {
							if (found && !g.equalsIgnoreCase(finaltarget)) {
								sender.sendMessage(ChatColor.GRAY + "More than one group was found for " + ChatColor.GOLD + finaltarget.substring(2) + ChatColor.GRAY + ".");
								continue finalize;
							} else {
								found = true;
								if (sender instanceof Player) {
									if (Main.chat.getPrimaryGroup((Player) sender).equals(g)) {
										owngroup = true;
									}
								}
								if (g.equalsIgnoreCase(finaltarget)) {
									finaltarget = "g:" + g;
									break;
								}
								finaltarget = "g:" + g;
							}
						}
					}
					if (!found) {
						sender.sendMessage(ChatColor.GRAY + "The group " + ChatColor.GOLD + finaltarget.substring(2) + ChatColor.GRAY + " does not exist.");
					}
				}
				if (found) {
					if (!finaltargets.containsKey(finaltarget)) {
						finaltargets.put(finaltarget, arguments.toString());
					}
				}
			}

		if (!finaltargets.isEmpty()) {
			if (!owngroup) {
				if (sender instanceof Player) {
					finaltargets.put(sender.getName(), "f");
				} else {
					finaltargets.put("c:" + ConfigurationHandler.getConfig().getString("consolename"), "");
				}
			}
		}
		return finaltargets;
	}

	public static void forceFocus(Map<String, String> finaltargets, Channel ch) {
		for (Entry<String, String> e : finaltargets.entrySet()) {
			if (e.getValue().contains("f")) {
				if (e.getKey().startsWith("g:")) {
					// Change group focus
					String group = e.getKey().substring(2);
					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						if (Main.permission.getPrimaryGroup(p).equals(group)) {
							setFocus(p, ch, e.getValue().contains("s"));
						}
					}
				} else {
					// Change player focus
					Player p = Bukkit.getServer().getPlayer(e.getKey());
					if (p != null) {
						setFocus(p, ch, e.getValue().contains("s"));
					}
				}
			}
		}
	}

	public static Channel getFocus(String player) {
		return focus.get(player);
	}

	public static void setFocus(Player p, Channel c, boolean silent) {
		String pname = p.getName();
		Channel ch = getFocus(pname);
		if (ch != null) {
			if (ch.equals(c)) {
				return;
			}
		}
		focus.remove(pname);
		focus.put(pname, c);
		if (!silent) {
			p.sendMessage(ChatColor.GRAY + "You are now talking to " + ChatColor.AQUA + c.getTargetString(p.getName()) + ChatColor.GRAY + ".");
		}
	}

	public static String toggleChannel(String player) {
		if (focus.get(player) != null) {
			focus.remove(player);
			return ChatColor.GRAY + "You are now talking to " + ChatColor.AQUA + "everybody" + ChatColor.GRAY + ".";
		}
		Channel c = getLastChannel(player);
		if (c != null) {
			focus.put(player, c);
			return ChatColor.GRAY + "You are now talking to " + ChatColor.AQUA + c.getTargetString(player) + ChatColor.GRAY + ".";
		}
		return ChatColor.GRAY + "No conversations found.";
	}

	public static Channel getLastChannel(String player) {
		Channel c = null;
		for (Channel ch : getChannels(player)) {
			if (c == null) {
				c = ch;
			} else if (ch.getLastActivity() > c.getLastActivity()) {
				c = ch;
			}
		}
		return c;
	}

	public static ArrayList<Channel> getAllChannels() {
		return channels;
	}

	public static ArrayList<Channel> getChannels(String player) {
		ArrayList<Channel> ch = new ArrayList<Channel>();
		for (Channel c : channels) {
			if (c.getTargets().contains(player)) {
				ch.add(c);
				continue;
			}
			if (c.getTargets().contains("g:" + permission.getPrimaryGroup(Bukkit.getWorlds().get(0), player))) {
				ch.add(c);
			}
		}
		return ch;
	}

	public static Channel getChannelBy(int id) {
		for (Channel c : channels) {
			if (c.getId() == id) {
				return c;
			}
		}
		return null;
	}

	public static int getFreeId() {
		for (int i = 1; i < 1000; i++) {
			if (getChannelBy(i) == null) {
				return i;
			}
		}
		return 0;
	}

	public static void closeChannel(Channel c) {
		channels.remove(c);
		for (String s : c.getTargets()) {
			if (s.startsWith("g:")) {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (permission.getPrimaryGroup(p).equals(s.substring(2))) {
						if (c.equals(getFocus(p.getName()))) {
							toggleChannel(p.getName());
						}
					}
				}
			} else if (c.equals(getFocus(s))) {
				toggleChannel(s);
			}
		}
	}
}