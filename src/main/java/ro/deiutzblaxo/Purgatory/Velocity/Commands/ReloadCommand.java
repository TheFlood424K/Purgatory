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

public class ReloadCommand implements SimpleCommand {
    private final ProxyServer server;
    private final MainVelocity plugin;

    public ReloadCommand(ProxyServer server, MainVelocity plugin) {
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
        
        // Reload configuration
        try {
            ConfigManager.reload();
            sender.sendMessage(Component.text("Configuration reloaded successfully!").color(NamedTextColor.GREEN));
        } catch (Exception e) {
            sender.sendMessage(Component.text("Failed to reload configuration: " + e.getMessage()).color(NamedTextColor.RED));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("purgatory.reload");
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return new ArrayList<>();
    }
}
