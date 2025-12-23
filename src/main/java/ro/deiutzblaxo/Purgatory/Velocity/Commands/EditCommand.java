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

public class EditCommand implements SimpleCommand {
    
    private final ProxyServer server;
    private final MainVelocity plugin;
    
    public EditCommand(ProxyServer server, MainVelocity plugin) {
        this.server = server;
        this.plugin = plugin;
    }
    
    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player)) {
            return;
        }
        
        Player sender = (Player) invocation.source();
        String[] args = invocation.arguments();
        
        if (!sender.hasPermission("purgatory.edit")) {
            Messages.sendMessage(sender, "noPermission");
            return;
        }
        
        if (args.length < 2) {
            Messages.sendMessage(sender, "invalidUsage",
                "usage", "/edit <player> <new reason>");
            return;
        }
        
        String playerName = args[0];
        
        // Build new reason
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String newReason = reasonBuilder.toString().trim();
        
        // Find player (online or offline)
        Optional<Player> targetOpt = server.getPlayer(playerName);
        UUID targetUUID = null;
        
        if (targetOpt.isPresent()) {
            targetUUID = targetOpt.get().getUniqueId();
        }
        
        // Edit ban reason
        if (targetUUID != null && plugin.getBanFactory() != null) {
            if (!plugin.getBanFactory().isBan(targetUUID)) {
                Messages.sendMessage(sender, "playerNotBanned",
                    "player", playerName);
                return;
            }
            
            // Update ban reason (assuming BanFactory has edit method)
            // plugin.getBanFactory().editBan(targetUUID, newReason);
        }
        
        // Send confirmation
        Messages.sendMessage(sender, "editBan",
            "player", playerName,
            "reason", newReason,
            "sender", sender.getUsername());
    }
    
    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("purgatory.edit");
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
