// Purgatory , a ban system for servers of Minecraft
// Copyright (C) 2020 Deiutz
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
package ro.deiutzblaxo.Purgatory.Velocity.Factory;

import java.util.UUID;
import java.util.Optional;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import ro.deiutzblaxo.Purgatory.Velocity.MainVelocity;

public class WarningFactory {
    private MainVelocity plugin;
    
    public WarningFactory(MainVelocity mainVelocity) {
        plugin = mainVelocity;
    }
    
    public void setWarning(Player player, CommandSource sender, String reason) {
        String uuid = player.getUniqueId().toString();
        plugin.getConfigManager().loadWarnings();
        if(!isWarning(player)) {
            plugin.getConfigManager().getWarnings().set(uuid + ".Value", 1);
            plugin.getConfigManager().getWarnings().set(uuid + ".Reason", reason);
            plugin.getConfigManager().saveWarnings();
        } else {
            int MaxWarning = plugin.getConfigManager().getConfig().getInt("MaxWarnings");
            int Warning = this.getWarningNumber(player) + 1;
            if(MaxWarning > Warning) {
                plugin.getConfigManager().getWarnings().set(uuid + ".Value", Warning);
                plugin.getConfigManager().getWarnings().set(uuid + ".Reason", reason);
                plugin.getConfigManager().saveWarnings();
            } else if(MaxWarning <= Warning) {
                plugin.getBanFactory().setBan(player.getUniqueId(), reason, player.getUsername());
                
                Optional<Player> playerOpt = plugin.getServer().getPlayer(player.getUniqueId());
                if(playerOpt.isPresent()) {
                    Player p = playerOpt.get();
                    String message = plugin.getConfigManager().getString(plugin.getConfigManager().getMessages(), "Ban.Format")
                        .replaceAll("%reason%", reason);
                    
                    if(plugin.getConfigManager().getConfig().getBoolean("Ban-Disconnect")) {
                        p.disconnect(deserialize(message));
                    } else {
                        Optional<RegisteredServer> purgatoryOpt = plugin.getServerManager().getPurgatoryServer();
                        if(purgatoryOpt.isPresent()) {
                            p.createConnectionRequest(purgatoryOpt.get()).fireAndForget();
                        }
                        p.sendMessage(deserialize(message));
                    }
                }
                
                String senderName = "Console";
                if(sender instanceof Player) {
                    senderName = ((Player) sender).getUsername();
                }
                
                String broadcastMsg = plugin.getConfigManager().getString(plugin.getConfigManager().getMessages(), "Ban.Broadcast")
                    .replaceAll("%player%", player.getUsername())
                    .replaceAll("%admin%", senderName)
                    .replaceAll("%reason%", reason);
                plugin.getServer().sendMessage(deserialize(broadcastMsg));
            }
        }
    }
    
    public void removeWarning(Player player) {
        UUID uuid = player.getUniqueId();
        plugin.getConfigManager().loadWarnings();
        plugin.getConfigManager().getWarnings().set(uuid.toString(), null);
        plugin.getConfigManager().saveWarnings();
    }
    
    public Integer getWarningNumber(Player player) {
        String uuid = player.getUniqueId().toString();
        if(isWarning(player)) {
            plugin.getConfigManager().loadWarnings();
            return plugin.getConfigManager().getWarnings().getInt(uuid + ".Value");
        }
        return 0;
    }
    
    public String getReason(Player player) {
        String uuid = player.getUniqueId().toString();
        plugin.getConfigManager().loadWarnings();
        return plugin.getConfigManager().getWarnings().getString(uuid + ".Reason");
    }
    
    public boolean isWarning(Player player) {
        String uuid = player.getUniqueId().toString();
        plugin.getConfigManager().loadWarnings();
        return plugin.getConfigManager().getWarnings().contains(uuid);
    }
    
    public int getMaxWarning() {
        return plugin.getConfigManager().getConfig().getInt("MaxWarnings");
    }
    
    private Component deserialize(String legacy) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(legacy);
    }
}
