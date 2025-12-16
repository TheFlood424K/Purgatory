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
            invocation.source().sendMessage(Component.text("Only players can use this command."));
            return;
        }

        Player sender = (Player) invocation.source();
        String[] args = invocation.arguments();

        if (args.length < 2) {
            Component usage = Component.text("Correct usage: /purgatoryedit <player> <reason>")
                    .hoverEvent(HoverEvent.showText(Component.text("Click to copy")))
                    .clickEvent(ClickEvent.suggestCommand("/purgatoryedit <player> <reason>"));
            sender.sendMessage(usage);
            return;
        }

        String playerName = args[0];
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String newReason = reasonBuilder.toString().trim();

        // Edit ban reason logic here
        String editMessage = ConfigManager.getMessage("editBan")
                .replace("%player%", playerName)
                .replace("%reason%", newReason)
                .replace("%sender%", sender.getUsername());
        
        sender.sendMessage(Component.text(editMessage));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("purgatory.edit");
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        List<String> suggestions = new ArrayList<>();
        // Could add banned player names here
        return suggestions;
    }
}
