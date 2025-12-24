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
package ro.deiutzblaxo.Purgatory.Velocity;

import java.util.UUID;
import java.util.Optional;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import ro.deiutzblaxo.Purgatory.Utils.Messages;

public class SpigotCommunication {
    protected MainVelocity plugin;
    public static final MinecraftChannelIdentifier CHANNEL = MinecraftChannelIdentifier.from("purgatory:main");
    
    protected SpigotCommunication(MainVelocity main){
        plugin = main;
        // Register channel
        plugin.getServer().getChannelRegistrar().register(CHANNEL);
    }
    
    @Subscribe
    public void onPluginMessage(PluginMessageEvent ev) {
        if (!ev.getIdentifier().equals(CHANNEL)) {
            return;
        }
        
        byte[] data = ev.getData();
        ByteArrayDataInput in = ByteStreams.newDataInput(data);
        String type = in.readUTF();
        
        if(type.equals("unban")) {
            UUID uuid = UUID.fromString(in.readUTF());
            plugin.getBanFactory().removeBan(uuid);
            
            Optional<Player> playerOpt = plugin.getServer().getPlayer(uuid);
            if(playerOpt.isPresent()) {
                Player player = playerOpt.get();
                
                if(plugin.getConfigManager().getConfig().getBoolean("UnBan-Disconnect")) {
                    String hubServerName = plugin.getServerManager().getHubServer();
                    Optional<RegisteredServer> hubOpt = plugin.getServer().getServer(hubServerName);
                    if(hubOpt.isPresent()) {
                        player.createConnectionRequest(hubOpt.get()).fireAndForget();
                    }
                    
                    String message = Messages.get("UnBanFormat")
                        .replaceAll("%admin%", Messages.get("TasksCompleted"));
                    player.disconnect(deserialize(message));
                } else {
                    String hubServerName = plugin.getServerManager().getHubServer();
                    Optional<RegisteredServer> hubOpt = plugin.getServer().getServer(hubServerName);
                    if(hubOpt.isPresent()) {
                        player.createConnectionRequest(hubOpt.get()).fireAndForget();
                    }
                    
                    String message = Messages.get("UnBanFormat")
                        .replaceAll("%admin%", Messages.get("TasksCompleted"));
                    player.sendMessage(deserialize(message));
                }
            }
        }
    }
    
    public void send(UUID uuid, String[] str) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF(str[0]); // TYPE
        output.writeUTF(uuid.toString()); // PLAYER'S UUID
        
        if(str[0].equals("ban")) {
            output.writeUTF(str[1]); // REASON
            output.writeUTF(str[2]); // Player's name
        } else if(str[0].equals("unban")){
            // No additional data
        } else if(str[0].equals("tempban")){
            output.writeUTF(str[1]); // REASON
            output.writeUTF(str[2]); // Player's name
            output.writeUTF(str[3]); // time
        } else {
            plugin.getLogger().warn("UNAVAILABLE TYPE AT ro.deiutzblaxo.Purgatory.Velocity.SpigotCommunication.class AT send method");
        }
        
        String purgatoryServerName = plugin.getServerManager().getPurgatoryServer();
        Optional<RegisteredServer> purgatoryOpt = plugin.getServer().getServer(purgatoryServerName);
        if(purgatoryOpt.isPresent()) {
            purgatoryOpt.get().sendPluginMessage(CHANNEL, output.toByteArray());
        }
    }
    
    private Component deserialize(String legacy) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(legacy);
    }
}
