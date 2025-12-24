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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.scheduler.ScheduledTask;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import ro.deiutzblaxo.Purgatory.Utils.Messages;
import ro.deiutzblaxo.Purgatory.Velocity.MainVelocity;

public class BanFactory {
    private MainVelocity plugin;
    private HashMap<UUID, Integer> tempban = new HashMap<UUID,Integer>();
    private ScheduledTask task;
    
    public BanFactory(MainVelocity main) {
        plugin = main;
        task = plugin.getServer().getScheduler().buildTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(tempban.isEmpty()) return;
                for(UUID uuid : tempban.keySet()) {
                    plugin.getLogger().debug("Test " + tempban.get(uuid));
                    int time = tempban.get(uuid);
                    if(1 >= time) {
                        tempban.remove(uuid);
                        removeBan(uuid);
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
                                    .replaceAll("%admin%", Messages.get("TempBanExpired"));
                                player.disconnect(deserialize(message));
                            } else {
                                String hubServerName = plugin.getServerManager().getHubServer();
                                Optional<RegisteredServer> hubOpt = plugin.getServer().getServer(hubServerName);
                                if(hubOpt.isPresent()) {
                                    player.createConnectionRequest(hubOpt.get()).fireAndForget();
                                }
                                String message = Messages.get("UnBanFormat")
                                    .replaceAll("%admin%", Messages.get("TempBanExpired"));
                                player.sendMessage(deserialize(message));
                            }
                        }
                    } else {
                        tempban.replace(uuid, time - 1);
                    }
                }
            }
        }).repeat(1, TimeUnit.SECONDS).schedule();
    }
    
    @SuppressWarnings("unchecked")
    public void setBan(UUID uuid, String reason, String Name) {
        plugin.getConfigManager().loadBans();
        Map<String, Object> bans = plugin.getConfigManager().getBans();
        
        Map<String, Object> banData = new HashMap<>();
        banData.put("Reason", reason);
        banData.put("Name", Name);
        bans.put(uuid.toString(), banData);
        
        if(plugin.getConfigManager().getConfig().getBoolean("Remove-Warnings-On-Ban")) {
            plugin.getConfigManager().loadWarnings();
            plugin.getConfigManager().getWarnings().remove(uuid.toString());
            plugin.getConfigManager().saveWarnings();
        }
        plugin.getConfigManager().saveBans();
        Optional<Player> playerOpt = plugin.getServer().getPlayer(uuid);
        if(playerOpt.isPresent()) {
            String purgatoryServerName = plugin.getServerManager().getPurgatoryServer();
            Optional<RegisteredServer> purgatoryOpt = plugin.getServer().getServer(purgatoryServerName);
            if(purgatoryOpt.isPresent()) {
                playerOpt.get().createConnectionRequest(purgatoryOpt.get()).fireAndForget();
            }
        }
        String send = "ban*" + reason + "*" + Name;
        plugin.getSpigotCommunication().send(uuid, send.split("\\*"));
    }
    
    public boolean isBan(UUID uuid) {
        plugin.getConfigManager().loadBans();
        return plugin.getConfigManager().getBans().containsKey(uuid.toString());
    }
    
    @SuppressWarnings("unchecked")
    public boolean isBan(String str) {
        plugin.getConfigManager().loadBans();
        Map<String, Object> bans = plugin.getConfigManager().getBans();
        for(Map.Entry<String, Object> entry : bans.entrySet()) {
            if(entry.getValue() instanceof Map) {
                Map<String, Object> banData = (Map<String, Object>) entry.getValue();
                if(banData.containsKey("Name")) {
                    String name = (String) banData.get("Name");
                    if(name != null && name.equalsIgnoreCase(str)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @SuppressWarnings("unchecked")
    public void removeBan(String str) {
        plugin.getConfigManager().loadBans();
        Map<String, Object> bans = plugin.getConfigManager().getBans();
        for(Map.Entry<String, Object> entry : bans.entrySet()) {
            if(entry.getValue() instanceof Map) {
                Map<String, Object> banData = (Map<String, Object>) entry.getValue();
                if(banData.containsKey("Name")) {
                    String name = (String) banData.get("Name");
                    if(name != null && name.equalsIgnoreCase(str)) {
                        removeBan(UUID.fromString(entry.getKey()));
                        return;
                    }
                }
            }
        }
    }
    
    public void removeBan(UUID uuid) {
        plugin.getConfigManager().getBans().remove(uuid.toString());
        plugin.getConfigManager().saveBans();
        Optional<Player> playerOpt = plugin.getServer().getPlayer(uuid);
        if(playerOpt.isPresent()) {
            String hubServerName = plugin.getServerManager().getHubServer();
            Optional<RegisteredServer> hubOpt = plugin.getServer().getServer(hubServerName);
            if(hubOpt.isPresent()) {
                playerOpt.get().createConnectionRequest(hubOpt.get()).fireAndForget();
            }
        }
        String send = "unban";
        plugin.getSpigotCommunication().send(uuid, send.split("\\*"));
        if(getTempBan().containsKey(uuid)) {
            getTempBan().remove(uuid);
        }
    }
    
    public void setTempBan(UUID uuid, String reason, Integer Time, String Name) {
        if(!isBan(uuid)) {
            setBan(uuid, reason, Name);
        }
        if(tempban.containsKey(uuid)) {
            Integer old = tempban.get(uuid);
            tempban.replace(uuid, old + Time);
        } else {
            tempban.put(uuid, Time);
        }
    }
    
    @SuppressWarnings("unchecked")
    public String getReason(Player player) {
        if(isBan(player.getUniqueId())) {
            Object banDataObj = plugin.getConfigManager().getBans().get(player.getUniqueId().toString());
            if(banDataObj instanceof Map) {
                Map<String, Object> banData = (Map<String, Object>) banDataObj;
                return (String) banData.get("Reason");
            }
        }
        return null;
    }
    
    public HashMap<UUID, Integer> getTempBan(){
        return tempban;
    }
    
    public Integer getTime(UUID uniqueId) {
        return getTempBan().get(uniqueId);
    }
    
    public boolean isTempBan(UUID uniqueId) {
        if(getTempBan().containsKey(uniqueId)) {
            return true;
        }
        return false;
    }
    
    private Component deserialize(String legacy) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(legacy);
    }
}
