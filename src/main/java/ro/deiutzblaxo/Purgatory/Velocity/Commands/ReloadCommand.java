package ro.deiutzblaxo.Purgatory.Velocity.Commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import ro.deiutzblaxo.Purgatory.Velocity.MainVelocity;
import ro.deiutzblaxo.Purgatory.Utils.ConfigManager;
import ro.deiutzblaxo.Purgatory.Utils.Messages;

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
            Messages.sendMessage(invocation.source(), "playerOnly");
            return;
        }

        Player sender = (Player) invocation.source();

        // Reload configuration
        try {
            ConfigManager.reload();
            Messages.sendMessage(sender, "configReload");
        } catch (Exception e) {
            Messages.sendMessage(sender, "configReloadFail", e.getMessage());
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
