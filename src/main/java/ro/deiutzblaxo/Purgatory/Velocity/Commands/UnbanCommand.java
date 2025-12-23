package ro.deiutzblaxo.Purgatory.Velocity.Commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import ro.deiutzblaxo.Purgatory.Velocity.MainVelocity;
import ro.deiutzblaxo.Purgatory.Utils.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnbanCommand implements SimpleCommand {
    
    private final ProxyServer server;
    private final MainVelocity plugin;
    
    public UnbanCommand(ProxyServer server, MainVelocity plugin) {
        this.server = server;
        this.plugin = plugin;
    }
    
    @Override
    public void execute(Invocation invocation) {
        // Only players can use this command
        if (!(invocation.source() instanceof Player)) {
            // Console execution - just proceed without sending player-specific messages
            String[] args = invocation.arguments();
            if (args.length < 1) {
                return;
            }
            // Console can unban by name - would need to look up UUID from storage
            return;
        }
        
        Player sender = (Player) invocation.source();
        String[] args = invocation.arguments();
        
        // Permission check
        if (!sender.hasPermission("purgatory.unban")) {
            Messages.sendMessage(sender, "noPermission");
            return;
        }
        
        // Usage check
        if (args.length < 1) {
            Messages.sendMessage(sender, "invalidUsage",
                "usage", "/unban <player>");
            return;
        }
        
        String playerName = args[0];
        
        // Check if player is banned using BanFactory
        // Note: In a real implementation, you'd look up the UUID from the player name
        // For now, we'll check if we can find an online/cached player
        java.util.Optional<Player> targetOpt = server.getPlayer(playerName);
        UUID targetUUID = null;
        
        if (targetOpt.isPresent()) {
            targetUUID = targetOpt.get().getUniqueId();
        } else {
            // Player is offline - would need to query ban storage by name
            // For demonstration, we'll assume we have ban factory methods
            // that can handle this
        }
        
        // Remove ban (assuming BanFactory has appropriate methods)
        if (targetUUID != null && plugin.getBanFactory() != null) {
            if (!plugin.getBanFactory().isBan(targetUUID)) {
                Messages.sendMessage(sender, "playerNotBanned",
                    "player", playerName);
                return;
            }
            
            plugin.getBanFactory().removeBan(targetUUID);
        }
        
        // Send success message
        Messages.sendMessage(sender, "unbanSuccess",
            "player", playerName,
            "sender", sender.getUsername());
    }
    
    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("purgatory.unban");
    }
    
    @Override
    public List<String> suggest(Invocation invocation) {
        List<String> suggestions = new ArrayList<>();
        // Could add banned player names here from ban storage
        return suggestions;
    }
}
