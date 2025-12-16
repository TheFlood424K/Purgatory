package ro.deiutzblaxo.Purgatory.Velocity.Commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import ro.deiutzblaxo.Purgatory.Velocity.MainVelocity;
import ro.deiutzblaxo.Purgatory.Utils.ConfigManager;
import ro.deiutzblaxo.Purgatory.Utils.ServerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TempBanCommand implements SimpleCommand {
    private final ProxyServer server;
    private final MainVelocity plugin;

    public TempBanCommand(ProxyServer server, MainVelocity plugin) {
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

        if (args.length < 3) {
            Component usage = Component.text("Correct usage: /tempban <player> <time> <reason>")
                    .hoverEvent(HoverEvent.showText(Component.text("Click to copy")))
                    .clickEvent(ClickEvent.suggestCommand("/tempban <player> <time> <reason>"));
            sender.sendMessage(usage);
            return;
        }

        String playerName = args[0];
        String time = args[1];
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String reason = reasonBuilder.toString().trim();

        Optional<Player> targetOpt = server.getPlayer(playerName);
        if (!targetOpt.isPresent()) {
            String notFound = ConfigManager.getMessage("playerNotFound").replace("%player%", playerName);
            sender.sendMessage(Component.text(notFound));
            return;
        }

        Player target = targetOpt.get();
        
        // Parse time to milliseconds
        long duration = parseTime(time);
        if (duration == -1) {
            sender.sendMessage(Component.text("Invalid time format. Use: 1d, 2h, 30m, etc."));
            return;
        }

        // Add temporary ban logic here
        String banMessage = ConfigManager.getMessage("tempBanMessage")
                .replace("%player%", target.getUsername())
                .replace("%time%", time)
                .replace("%reason%", reason)
                .replace("%sender%", sender.getUsername());
        
        Component kickMessage = Component.text(banMessage);
        target.disconnect(kickMessage);
        
        // Notify sender
        String confirmMsg = ConfigManager.getMessage("tempBanConfirm")
                .replace("%player%", target.getUsername())
                .replace("%time%", time);
        sender.sendMessage(Component.text(confirmMsg));
    }

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
