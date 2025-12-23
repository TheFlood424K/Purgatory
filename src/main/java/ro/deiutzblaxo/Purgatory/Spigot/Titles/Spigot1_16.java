// Purgatory , a ban system for servers of Minecraft
// Copyright (C) 2020  Deiutz
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
package ro.deiutzblaxo.Purgatory.Spigot.Titles;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Spigot1_16 {
    public void Packet1_16(Player player, String titleString) {
        String titlestring = ChatColor.translateAlternateColorCodes('&', titleString);
        // Use modern Bukkit API for titles (works on 1.11+)
        player.sendTitle(titlestring, "", 1, 20, 20);
    }
}
