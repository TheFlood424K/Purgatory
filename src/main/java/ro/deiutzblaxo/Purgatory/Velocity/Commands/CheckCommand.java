package ro.deiutzblaxo.Purgatory.Velocity.Commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import ro.deiutzblaxo.Purgatory.Velocity.MainVelocity;
import ro.deiutzblaxo.Purgatory.Utils.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CheckCommand implements SimpleCommand {
    
    private final ProxyServer server;
    private final MainVelocity plugin;
    
    public CheckCommand(ProxyServer server, MainVelocity plugin) {
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
        if (!sender.hasPermission("purgatory.check")) {
            Messages.sendMessage(sender, "noPermission");
            return;
        }
        
        // Usage check
        if (args.length < 1) {
            Messages.sendMessage(sender, "invalidUsage",
                "usage", "/check <player>");
            return;
        }
        
        String playerName = args[0];
        Optional<Player> targetOpt = server.getPlayer(playerName);
        
        if (!targetOpt.isPresent()) {
            Messages.sendMessage(sender, "playerNotFound",
                "player", playerName);
            return;
        }
        
        Player target = targetOpt.get();
        
        // Get player information
        String serverName = target.getCurrentServer()
            .map(s -> s.getServerInfo().getName())
            .orElse("None");
        String ipAddress = target.getRemoteAddress().getAddress().getHostAddress();
        
        // Send check information
        Messages.sendMessage(sender, "checkPlayer",
            "player", target.getUsername(),
            "server", serverName,
            "ip", ipAddress);
    }
    
    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("purgatory.check");
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
