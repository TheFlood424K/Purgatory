package ro.deiutzblaxo.Purgatory.Velocity.Commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import ro.deiutzblaxo.Purgatory.Velocity.MainVelocity;
import ro.deiutzblaxo.Purgatory.Utils.Messages;

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
            Messages.sendMessage(invocation.source(), "playerOnly");
            return;
        }

        Player sender = (Player) invocation.source();

        // Display help menu
        Messages.sendMessage(sender, "helpHeader");
        Messages.sendMessage(sender, "helpBan");
        Messages.sendMessage(sender, "helpTempban");
        Messages.sendMessage(sender, "helpUnban");
        Messages.sendMessage(sender, "helpCheck");
        Messages.sendMessage(sender, "helpEdit");
        Messages.sendMessage(sender, "helpTeleport");
        Messages.sendMessage(sender, "helpReload");
        Messages.sendMessage(sender, "helpFooter");
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
