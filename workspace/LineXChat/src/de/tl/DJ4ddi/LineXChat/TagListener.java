package de.tl.DJ4ddi.LineXChat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.tag.PlayerReceiveNameTagEvent;

public class TagListener implements Listener {
	@EventHandler
	public void onNameTag(PlayerReceiveNameTagEvent e) {
		if (!e.isModified()) {
			if (Main.nick.containsKey(e.getNamedPlayer().getName())) {
				e.setTag(Main.nick.get(e.getNamedPlayer().getName()).replaceAll("(&([a-f0-9]))", "\u00A7$2"));
			}
		}
	}

}
