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
package ro.deiutzblaxo.Purgatory.Velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.nio.file.Path;

import ro.deiutzblaxo.Purgatory.Utils.ConfigManager;
import ro.deiutzblaxo.Purgatory.Utils.ServerManager;

import ro.deiutzblaxo.Purgatory.Velocity.Commands.BanCommand;
import ro.deiutzblaxo.Purgatory.Velocity.Commands.CheckCommand;
import ro.deiutzblaxo.Purgatory.Velocity.Commands.PurgatoryCommand;
import ro.deiutzblaxo.Purgatory.Velocity.Commands.EditCommand;
import ro.deiutzblaxo.Purgatory.Velocity.Commands.TeleportCommand;
import ro.deiutzblaxo.Purgatory.Velocity.Commands.TempBanCommand;
import ro.deiutzblaxo.Purgatory.Velocity.Commands.UnbanCommand;
import ro.deiutzblaxo.Purgatory.Velocity.Commands.ReloadCommand;
import ro.deiutzblaxo.Purgatory.Velocity.Event.Events;

@Plugin(
    id = "purgatory",
    name = "Purgatory",
    version = "5.1.2-1.21.10",
    description = "Ban system for Minecraft servers",
    authors = {"Deiutz", "TheFlood424K"}
)
public class MainVelocity {
    
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private ConfigManager configManager;
    private ServerManager serverManager;
    private ro.deiutzblaxo.Purgatory.Velocity.Factory.BanFactory banFactory;
    private SpigotCommunication spigotCommunication;
    private ro.deiutzblaxo.Purgatory.Velocity.Factory.WarningFactory warningFactory;
    
    private static MainVelocity instance;
    
    @Inject
    public MainVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        instance = this;
    }
    
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Initializing Purgatory plugin for Velocity...");
        
        setConfigManager(new ConfigManager());
        setServerManager(new ServerManager());
        setBanFactory(new ro.deiutzblaxo.Purgatory.Velocity.Factory.BanFactory(this));
        setWarningFactory(new ro.deiutzblaxo.Purgatory.Velocity.Factory.WarningFactory(this));
        setSpigotCommunication(new SpigotCommunication(this));
        
        // Register commands
        registerCommands();
        
        // Register events
        server.getEventManager().register(this, new Events(this));
        
        // Load temporary ban data
        getConfigManager().loadTempBan();
        
        // Initialize metrics (port Metrics class to Velocity if needed)
        // new Metrics(this);
        
        logger.info("Purgatory plugin enabled successfully!");
    }
    
    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        logger.info("Saving Purgatory data...");
        getConfigManager().saveTempBan();
        logger.info("Purgatory plugin disabled successfully!");
    }
    
    private void registerCommands() {
        var commandManager = server.getCommandManager();
        
        // Register all commands with their aliases
        commandManager.register(
            commandManager.metaBuilder(getConfigManager().getConfig().getString("Command.Ban")).build(),
            new BanCommand(this)
        );
        
        commandManager.register(
            commandManager.metaBuilder(getConfigManager().getConfig().getString("Command.TempBan")).build(),
            new TempBanCommand(server, this)
        );
        
        commandManager.register(
            commandManager.metaBuilder(getConfigManager().getConfig().getString("Command.UnBan")).build(),
            new UnbanCommand(server, this)
        );
        
        commandManager.register(
            commandManager.metaBuilder(getConfigManager().getConfig().getString("Command.Info")).build(),
            new CheckCommand(server, this)
        );
        
        commandManager.register(
            commandManager.metaBuilder(getConfigManager().getConfig().getString("Command.Warning")).build(),
            new ReloadCommand(server, this)
        );
        
        commandManager.register(
            commandManager.metaBuilder(getConfigManager().getConfig().getString("Command.tpp")).build(),
            new TeleportCommand(server, this)
        );
        
        commandManager.register(
            commandManager.metaBuilder(getConfigManager().getConfig().getString("Command.tpo")).build(),
            new EditCommand(server, this)
        );
        
        commandManager.register(
            commandManager.metaBuilder("purgatory").build(),
            new PurgatoryCommand(server, this)
        );
    }
    
    // Getters and Setters
    public static MainVelocity getInstance() {
        return instance;
    }
    
    public ProxyServer getServer() {
        return server;
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public Path getDataDirectory() {
        return dataDirectory;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    private void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }
    
    public ServerManager getServerManager() {
        return serverManager;
    }
    
    private void setServerManager(ServerManager serverManager) {
        this.serverManager = serverManager;
    }
    
    public ro.deiutzblaxo.Purgatory.Velocity.Factory.BanFactory getBanFactory() {
        return banFactory;
    }
    
    private void setBanFactory(ro.deiutzblaxo.Purgatory.Velocity.Factory.BanFactory banFactory) {
        this.banFactory = banFactory;
    }
    
    public SpigotCommunication getSpigotCommunication() {
        return spigotCommunication;
    }
    
    private void setSpigotCommunication(SpigotCommunication spigotCommunication) {
        this.spigotCommunication = spigotCommunication;
    }
    
    public ro.deiutzblaxo.Purgatory.Velocity.Factory.WarningFactory getWarningFactory() {
        return warningFactory;
    }
    
    public void setWarningFactory(ro.deiutzblaxo.Purgatory.Velocity.Factory.WarningFactory warningFactory) {
        this.warningFactory = warningFactory;
    }
}
