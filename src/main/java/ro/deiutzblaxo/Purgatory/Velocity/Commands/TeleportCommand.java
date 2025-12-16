package ro.deiutzblaxo.Purgatory.Velocity.Commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import ro.deiutzblaxo.Purgatory.Velocity.MainVelocity;
import ro.deiutzblaxo.Purgatory.Utils.ConfigManager;

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
            invocation.source().sendMessage(Component.text("Only players can use this command."));
            return;
        }

        Player sender = (Player) invocation.source();
        String[] args = invocation.arguments();

        if (args.length < 1) {
            Component usage = Component.text("Correct usage: /purgatoryteleport <player>")
                    .hoverEvent(HoverEvent.showText(Component.text("Click to copy")))
                    .clickEvent(ClickEvent.suggestCommand("/purgatoryteleport <player>"));
            sender.sendMessage(usage);
            return;
        }

        String playerName = args[0];
        Optional<Player> targetOpt = server.getPlayer(playerName);
        
        if (!targetOpt.isPresent()) {
            String notFound = ConfigManager.getMessage("playerNotFound").replace("%player%", playerName);
            sender.sendMessage(Component.text(notFound));
            return;
        }

        Player target = targetOpt.get();
        
        // Teleport to player's server
        target.getCurrentServer().ifPresent(serverConnection -> {
            sender.createConnectionRequest(serverConnection.getServer()).fireAndForget();
            String tpMessage = ConfigManager.getMessage("teleportSuccess")
                    .replace("%player%", target.getUsername())
                    .replace("%server%", serverConnection.getServerInfo().getName());
            sender.sendMessage(Component.text(tpMessage));
        });
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
