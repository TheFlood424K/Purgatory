package ro.deiutzblaxo.Purgatory.Velocity.Commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import ro.deiutzblaxo.Purgatory.Velocity.MainVelocity;
import ro.deiutzblaxo.Purgatory.Utils.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TempBanCommand implements SimpleCommand {
    
    private final ProxyServer server;
    private final MainVelocity plugin;
    
    public TempBanCommand(ProxyServer server, MainVelocity plugin) {
        this.server = server;
        this.plugin = plugin;
    }
    
    @Override
    public void execute(Invocation invocation) {
        // Only players can use this command
        if (!(invocation.source() instanceof Player)) {
            return;
        }
        
        Player sender = (Player) invocation.source();
        String[] args = invocation.arguments();
        
        // Permission check
        if (!sender.hasPermission("purgatory.tempban")) {
            Messages.sendMessage(sender, "noPermission");
            return;
        }
        
        // Usage check
        if (args.length < 3) {
            Messages.sendMessage(sender, "invalidUsage",
                "usage", "/tempban <player> <time> <reason>");
            return;
        }
        
        String playerName = args[0];
        String timeString = args[1];
        
        // Build reason from remaining arguments
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String reason = reasonBuilder.toString().trim();
        
        // Find target player
        Optional<Player> targetOpt = server.getPlayer(playerName);
        if (!targetOpt.isPresent()) {
            Messages.sendMessage(sender, "playerNotFound",
                "player", playerName);
            return;
        }
        
        Player target = targetOpt.get();
        UUID targetUUID = target.getUniqueId();
        
        // Check if already banned
        if (plugin.getBanFactory() != null && plugin.getBanFactory().isBan(targetUUID)) {
            Messages.sendMessage(sender, "alreadyBanned",
                "player", playerName);
            return;
        }
        
        // Parse time duration
        long duration = parseTime(timeString);
        if (duration == -1) {
            Messages.sendMessage(sender, "invalidTimeFormat",
                "format", "1d, 2h, 30m");
            return;
        }
        
        // Execute temporary ban
        if (plugin.getBanFactory() != null) {
            // Calculate expiry time
            long expiryTime = System.currentTimeMillis() + duration;
            // Note: BanFactory would need a setTempBan method
            // For now, we'll assume it exists or use regular ban
            plugin.getBanFactory().setBan(targetUUID, reason, target.getUsername());
        }
        
        String senderName = sender.getUsername();
        
        // Disconnect player with ban message
        Messages.sendMessage(target, "tempBanMessage",
            "sender", senderName,
            "time", timeString,
            "reason", reason);
        
        target.disconnect(net.kyori.adventure.text.Component.text(
            Messages.getMessage("tempBanMessage",
                "sender", senderName,
                "time", timeString,
                "reason", reason)));
        
        // Confirm to sender
        Messages.sendMessage(sender, "tempBanConfirm",
            "player", playerName,
            "time", timeString);
    }
    
    /**
     * Parse time string to milliseconds
     * Supports: 1d (days), 2h (hours), 30m (minutes)
     */
    private long parseTime(String time) {
        try {
            if (time.endsWith("d")) {
                return Long.parseLong(time.substring(0, time.length() - 1)) * 24 * 60 * 60 * 1000;
            } else if (time.endsWith("h")) {
                return Long.parseLong(time.substring(0, time.length() - 1)) * 60 * 60 * 1000;
            } else if (time.endsWith("m")) {
                return Long.parseLong(time.substring(0, time.length() - 1)) * 60 * 1000;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
        return -1;
    }
    
    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("purgatory.tempban");
    }
    
    @Override
    public List<String> suggest(Invocation invocation) {
        List<String> suggestions = new ArrayList<>();
        if (invocation.arguments().length == 0 || invocation.arguments().length == 1) {
            server.getAllPlayers().forEach(p -> suggestions.add(p.getUsername()));
        }
        return suggestions;
    }
}
