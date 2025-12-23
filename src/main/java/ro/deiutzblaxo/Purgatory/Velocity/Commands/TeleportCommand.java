package ro.deiutzblaxo.Purgatory.Velocity.Commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import ro.deiutzblaxo.Purgatory.Velocity.MainVelocity;
import ro.deiutzblaxo.Purgatory.Utils.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeleportCommand implements SimpleCommand {
    
    private final ProxyServer server;
    private final MainVelocity plugin;
    
    public TeleportCommand(ProxyServer server, MainVelocity plugin) {
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
        
        if (!sender.hasPermission("purgatory.teleport")) {
            Messages.sendMessage(sender, "noPermission");
            return;
        }
        
        if (args.length < 1) {
            Messages.sendMessage(sender, "invalidUsage",
                "usage", "/teleport <player>");
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
        
        // Teleport to target's server
        if (target.getCurrentServer().isPresent()) {
            String targetServer = target.getCurrentServer().get().getServerInfo().getName();
            sender.createConnectionRequest(target.getCurrentServer().get().getServer())
                .fireAndForget();
            
            Messages.sendMessage(sender, "teleportSuccess",
                "player", target.getUsername(),
                "server", targetServer);
        } else {
            Messages.sendMessage(sender, "playerNoServer",
                "player", target.getUsername());
        }
    }
    
    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("purgatory.teleport");
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
