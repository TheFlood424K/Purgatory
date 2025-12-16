// Purgatory , a ban system for servers of Minecraft
// Copyright (C) 2020 Deiutz
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
package ro.deiutzblaxo.Purgatory.Velocity.Commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import ro.deiutzblaxo.Purgatory.Velocity.MainVelocity;

import java.util.Optional;
import java.util.UUID;

public class BanCommand implements SimpleCommand {
    private final MainVelocity plugin;
    private String name, reason;
    private UUID uuid;

    public BanCommand(MainVelocity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (!sender.hasPermission("purgatory.ban")) {
            sender.sendMessage(deserialize(
                plugin.getConfigManager().getString(plugin.getConfigManager().getMessages(), "NoPermission")));
            return;
        }

        if (args.length < 1) {
            // Build usage message with hover and click events
            Component usageText = deserialize(
                plugin.getConfigManager().getMessages().getString("InvalidCommand.Usage"))
                .clickEvent(ClickEvent.suggestCommand("/" + 
                    plugin.getConfigManager().getConfig().getString("Command.Ban").toLowerCase() + 
                    " <player> <reason>"));

            Component commandText = deserialize("/" + 
                plugin.getConfigManager().getConfig().getString("Command.Ban").toLowerCase())
                .hoverEvent(HoverEvent.showText(deserialize(
                    plugin.getConfigManager().getMessages().getString("InvalidCommand.Command"))));

            Component playerText = deserialize("<player>")
                .hoverEvent(HoverEvent.showText(deserialize(
                    plugin.getConfigManager().getMessages().getString("InvalidCommand.Player"))));

            Component reasonText = deserialize("<reason>")
                .hoverEvent(HoverEvent.showText(deserialize(
                    plugin.getConfigManager().getMessages().getString("InvalidCommand.Reason"))));

            Component finalMessage = usageText
                .append(Component.space())
                .append(commandText)
                .append(Component.space())
                .append(playerText)
                .append(Component.space())
                .append(reasonText);

            sender.sendMessage(finalMessage);
            return;
        }

        Optional<Player> targetOpt = plugin.getServer().getPlayer(args[0]);
        if (!targetOpt.isPresent()) {
            sender.sendMessage(deserialize(
                plugin.getConfigManager().getString(plugin.getConfigManager().getMessages(), "PlayerOffline")));
            return;
        }

        Player player = targetOpt.get();
        uuid = player.getUniqueId();
        name = player.getUsername();

        if (plugin.getBanFactory().isBan(uuid)) {
            sender.sendMessage(deserialize(
                plugin.getConfigManager().getString(plugin.getConfigManager().getMessages(), "isBan")
                    .replaceAll("%player%", name)));
            return;
        } else {
            if (args.length >= 2) {
                args[0] = "";
                StringBuilder stringBuilder = new StringBuilder();
                for (String arg : args) {
                    stringBuilder.append(arg).append(" ");
                }
                reason = stringBuilder.toString();
            } else {
                reason = plugin.getConfigManager().getString(
                    plugin.getConfigManager().getMessages(), "Ban.DefaultReason");
            }

            plugin.getBanFactory().setBan(uuid, reason, name);

            if (plugin.getConfigManager().getConfig().getBoolean("Ban-Disconnect")) {
                player.disconnect(deserialize(
                    plugin.getConfigManager().getString(plugin.getConfigManager().getMessages(), "Ban.Format")
                        .replaceAll("%reason%", reason)));
            } else {
                player.createConnectionRequest(plugin.getServerManager().getPurgatoryServer())
                    .fireAndForget();
                player.sendMessage(deserialize(
                    plugin.getConfigManager().getString(plugin.getConfigManager().getMessages(), "Ban.Format")
                        .replaceAll("%reason%", reason)));
            }

            plugin.getServer().sendMessage(deserialize(
                plugin.getConfigManager().getString(plugin.getConfigManager().getMessages(), "Ban.Broadcast")
                    .replaceAll("%player%", name)
                    .replaceAll("%admin%", sender instanceof Player ? ((Player) sender).getUsername() : "Console")
                    .replaceAll("%reason%", reason)));
        }
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("purgatory.ban");
    }

    private Component deserialize(String legacy) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(legacy);
    }
}
