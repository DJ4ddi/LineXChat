package de.tl.DJ4ddi.LineXChat.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tl.DJ4ddi.LineXChat.Channel;
import de.tl.DJ4ddi.LineXChat.Main;

public class PrivateMessageCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (args.length == 2) {
			if (cs instanceof Player) {
				if (!(Main.permission.has(cs, "lnxchat.command.tell") || Main.permission.has(cs, "lnxchat.command.all") || cs.isOp()) || Main.denied.contains(cs.getName())) {
					cs.sendMessage(ChatColor.GRAY + "You do not have the " + ChatColor.GOLD + "permission" + ChatColor.GRAY + " to send private messages.");
					return true;
				}
			}
			Channel c = Main.parseChannel(args[0].split(","), cs);
			if (c != null) {
				c.sendMessage(args[1], cs);
			}
		} else {
			cs.sendMessage(ChatColor.AQUA + "Usage: " + ChatColor.GRAY + "/" + alias + " <Receiver(s)> [Message]");
		}
		return true;
	}
}