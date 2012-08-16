package de.tl.DJ4ddi.LineXChat.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tl.DJ4ddi.LineXChat.Channel;
import de.tl.DJ4ddi.LineXChat.Main;

public class ReplyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
		if (cs instanceof Player) {
			if (args.length > 0) {
				Channel c = Main.getLastChannel(cs.getName());
				if (c != null) {
					if (args.length > 0) {
						StringBuilder message = new StringBuilder();
						for (String s : args) {
							message.append(" " + s);
						}
						c.sendMessage(message.substring(1), cs);
					}
				} else {
					cs.sendMessage(ChatColor.GRAY + "No conversations found.");
				}
			} else {
				cs.sendMessage(Main.toggleChannel(cs.getName()));
			}
		} else {
			cs.sendMessage("This command can not be used from console.");
		}
		return true;
	}
}