package ro.deiutzblaxo.Purgatory.Velocity.Commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import ro.deiutzblaxo.Purgatory.Velocity.MainVelocity;
import ro.deiutzblaxo.Purgatory.Utils.ConfigManager;

import java.util.ArrayList;
import java.util.List;

public class PurgatoryCommand implements SimpleCommand {
    private final ProxyServer server;
    private final MainVelocity plugin;

    public PurgatoryCommand(ProxyServer server, MainVelocity plugin) {
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
        
        // Display help menu
        sender.sendMessage(Component.text("").color(NamedTextColor.DARK_GRAY));
        sender.sendMessage(Component.text("===== Purgatory Help =====").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/ban <player> <reason> - Ban a player").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/tempban <player> <time> <reason> - Temporarily ban a player").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/unban <player> - Unban a player").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/check <player> - Check player status").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/purgatory edit <player> - Edit ban details").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/purgatory tp <player> - Teleport to banned player").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/purgatory reload - Reload configuration").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("=========================").color(NamedTextColor.GOLD));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("purgatory.help");
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        List<String> suggestions = new ArrayList<>();
        String[] args = invocation.arguments();
        
        if (args.length == 0 || args.length == 1) {
            suggestions.add("edit");
            suggestions.add("tp");
            suggestions.add("reload");
        }
        
        return suggestions;
    }
}
