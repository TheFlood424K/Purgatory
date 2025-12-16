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
package ro.deiutzblaxo.Purgatory.Velocity.Event;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import ro.deiutzblaxo.Purgatory.Velocity.MainVelocity;
import ro.deiutzblaxo.Purgatory.Utils.ServerManager;

import java.util.Optional;

/**
 * Event handler class for Velocity proxy events
 * Handles player connections and redirects banned players to purgatory server
 */
public class Events {
    private final MainVelocity plugin;

    public Events(MainVelocity plugin) {
        this.plugin = plugin;
    }

    /**
     * Handle server pre-connect events
     * Redirects banned players to the purgatory server
     */
    @Subscribe(order = PostOrder.EARLY)
    public void onServerPreConnect(ServerPreConnectEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is banned (placeholder - needs BanFactory integration)
        // if (plugin.getBanFactory().isBan(player.getUniqueId())) {
        //     Optional<RegisteredServer> purgatoryServer = plugin.getProxy()
        //             .getServer("purgatory"); // TODO: Get from config
        //     
        //     purgatoryServer.ifPresent(server -> {
        //         event.setResult(ServerPreConnectEvent.ServerResult.allowed(server));
        //     });
        // }
        
        // Register player connection for tracking
        event.getOriginalServer().getServerInfo().getName();
        ServerManager.registerPlayerConnection(
            player.getUniqueId(),
            event.getOriginalServer().getServerInfo().getName()
        );
    }

    /**
     * Handle player disconnect
     * Clean up player tracking data
     */
    @Subscribe
    public void onDisconnect(com.velocitypowered.api.event.connection.DisconnectEvent event) {
        ServerManager.unregisterPlayer(event.getPlayer().getUniqueId());
    }
}
