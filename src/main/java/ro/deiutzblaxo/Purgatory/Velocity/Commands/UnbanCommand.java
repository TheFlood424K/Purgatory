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

public class UnbanCommand implements SimpleCommand {
    private final ProxyServer server;
    private final MainVelocity plugin;

    public UnbanCommand(ProxyServer server, MainVelocity plugin) {
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
            Component usage = Component.text("Correct usage: /unban <player>")
                    .hoverEvent(HoverEvent.showText(Component.text("Click to copy")))
                    .clickEvent(ClickEvent.suggestCommand("/unban <player>"));
            sender.sendMessage(usage);
            return;
        }

        String playerName = args[0];

        // Remove ban logic here
        // This would interact with the ban storage system
        String unbanMessage = ConfigManager.getMessage("unbanSuccess")
                .replace("%player%", playerName)
                .replace("%sender%", sender.getUsername());
        
        sender.sendMessage(Component.text(unbanMessage));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("purgatory.unban");
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        List<String> suggestions = new ArrayList<>();
        // Could add banned player names here
        return suggestions;
    }
}
