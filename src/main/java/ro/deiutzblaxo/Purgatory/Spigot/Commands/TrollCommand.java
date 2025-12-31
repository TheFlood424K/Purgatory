//    Purgatory , a ban system for servers of Minecraft
//    Copyright (C) 2020  Deiutz
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.
package ro.deiutzblaxo.Purgatory.Spigot.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import ro.deiutzblaxo.Purgatory.Spigot.MainSpigot;
import ro.deiutzblaxo.Purgatory.Spigot.Troll.Trolls;

public class TrollCommand extends Command {
	private MainSpigot plugin;
	private Trolls troll;
	public TrollCommand(String name , MainSpigot main) {
		super(name);
		plugin = main;
		troll = new Trolls(plugin);
	}

	@Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        plugin.getConfigManager().loadMessages();

        // Only players
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return false;
        }
        Player player = (Player) sender;

        // NoPermission message with fallback
        String noPerm = plugin.getConfigManager()
                .getString(plugin.getConfigManager().getMessages(), "NoPermission");
        if (noPerm == null) {
            noPerm = "&cYou don't have permission to use this command.";
        }

        if (!sender.hasPermission("purgatory.troll")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPerm));
            return false;
        }

        // If not banned, open inventory
        if (!plugin.getBanFactory().isBan(player.getUniqueId())) {
            player.openInventory(troll.TrollsInventory(player));
            return true;
        }

        // Troll.Banned message with fallback
        String bannedMsg = plugin.getConfigManager()
                .getMessages().getString("Troll.Banned");
        if (bannedMsg == null) {
            bannedMsg = "&cYou cannot use this while banned.";
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', bannedMsg));
        return false;
    }

}
