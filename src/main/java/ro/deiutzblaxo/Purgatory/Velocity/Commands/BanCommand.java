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
package ro.deiutzblaxo.Purgatory.Velocity.Commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import ro.deiutzblaxo.Purgatory.Velocity.MainVelocity;
import ro.deiutzblaxo.Purgatory.Utils.Messages;

import java.util.Optional;
import java.util.UUID;

public class BanCommand implements SimpleCommand {
    
    private final MainVelocity plugin;
    private String name, reason;
    private UUID uuid;
    
    public BanCommand(MainVelocity plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
        
        // Permission check
        if (!sender.hasPermission("purgatory.ban")) {
            if (sender instanceof Player) {
                Messages.sendMessage((Player) sender, "noPermission");
            }
            return;
        }
        
        // Usage check
        if (args.length < 1) {
            if (sender instanceof Player) {
                Messages.sendMessage((Player) sender, "invalidUsage", 
                    "usage", "/ban <player> <reason>");
            }
            return;
        }
        
        // Find target player
        Optional<Player> targetOpt = plugin.getServer().getPlayer(args[0]);
        if (!targetOpt.isPresent()) {
            if (sender instanceof Player) {
                Messages.sendMessage((Player) sender, "playerNotFound",
                    "player", args[0]);
            }
            return;
        }
        
        Player player = targetOpt.get();
        uuid = player.getUniqueId();
        name = player.getUsername();
        
        // Check if already banned
        if (plugin.getBanFactory().isBan(uuid)) {
            if (sender instanceof Player) {
                Messages.sendMessage((Player) sender, "alreadyBanned",
                    "player", name);
            }
            return;
        }
        
        // Get ban reason
        if (args.length >= 2) {
            args[0] = "";
            StringBuilder stringBuilder = new StringBuilder();
            for (String arg : args) {
                stringBuilder.append(arg).append(" ");
            }
            reason = stringBuilder.toString().trim();
        } else {
            reason = Messages.getMessage("defaultReason");
        }
        
        // Execute ban
        plugin.getBanFactory().setBan(uuid, reason, name);
        
        String senderName = sender instanceof Player ? ((Player) sender).getUsername() : "Console";
        
        // Disconnect or teleport based on config
        if (plugin.getConfigManager().getConfig().getBoolean("Ban-Disconnect")) {
            Messages.sendMessage(player, "banMessage",
                "sender", senderName,
                "reason", reason);
            player.disconnect(net.kyori.adventure.text.Component.text(
                Messages.getMessage("banMessage", "sender", senderName, "reason", reason)));
        } else {
String purgatoryServerName = plugin.getServerManager().getPurgatoryServer();
            Optional<com.velocitypowered.api.proxy.server.RegisteredServer> purgatoryOpt = plugin.getServer().getServer(purgatoryServerName);
            if(purgatoryOpt.isPresent()) {
                player.createConnectionRequest(purgatoryOpt.get())
                .fireAndForget();
                Messages.sendMessage(player, "banMessage",
                    "sender", senderName,
                    "reason", reason);
            }
        }
        
        // Broadcast to sender
        if (sender instanceof Player) {
            Messages.sendMessage((Player) sender, "banConfirm",
                "player", name,
                "reason", reason);
        }
    }
    
    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("purgatory.ban");
    }
}
