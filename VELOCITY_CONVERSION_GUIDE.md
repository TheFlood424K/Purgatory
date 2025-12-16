# Purgatory Velocity Conversion Guide

## ✅ Completed Work

The foundation for Velocity support has been established:

1. **pom.xml** - Added Velocity API 3.3.0-SNAPSHOT dependency and repository
2. **MainVelocity.java** - Created the main plugin class with @Plugin annotation, dependency injection, and event handling
3. **velocity-plugin.json** - Created the Velocity manifest file

## 🔧 Remaining Conversion Tasks

### Directory Structure to Create

```
src/main/java/ro/deiutzblaxo/Purgatory/Velocity/
├── MainVelocity.java ✅ (DONE)
├── ConfigManager.java
├── ServerManager.java  
├── SpigotCommunication.java
├── Commands/
│   ├── BanCommand.java ✅ (DONE)
│   ├── TempBanCommand.java ✅ (DONE)
│   ├── UnbanCommand.java ✅ (DONE)
│   ├── InfoCommand.java ✅ (DONE)
│   ├── WarningCommand.java ✅ (DONE)
│   ├── TeleportCommand.java ✅ (DONE)
│   ├── Teleport2Command.java ✅ (DONE)
│   └── PurgatoryCommand.java ✅ (DONE)
├── Event/
│   └── Events.java
└── Factory/
    ├── BanFactory.java
    └── WarningFactory.java
```

---

## 📋 Step-by-Step Conversion Instructions

### 1. ✅ Command Classes

**BungeeCord Pattern:**
```java
public class BanCommand extends Command {
    public BanCommand(String name, MainBungee plugin) {
        super(name);
        this.plugin = plugin;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        // Command logic
    }
}
```

**Velocity Pattern:**
```java
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.command.CommandSource;

public class BanCommand implements SimpleCommand {
    private final MainVelocity plugin;
    
    public BanCommand(MainVelocity plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        // Command logic
    }
    
    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("purgatory.ban");
    }
}
```

**Key Changes:**
- Extend `Command` → Implement `SimpleCommand`
- `execute(CommandSender, String[])` → `execute(Invocation)`
- Get source: `invocation.source()`
- Get args: `invocation.arguments()`
- Add `hasPermission()` method

---

### 2. Event Classes

**BungeeCord Pattern:**
```java
public class Events implements Listener {
    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        // Event logic
    }
}
```

**Velocity Pattern:**
```java
public class Events {
    private final MainVelocity plugin;
    
    public Events(MainVelocity plugin) {
        this.plugin = plugin;
    }
    
    @Subscribe
    public void onServerPreConnect(ServerPreConnectEvent event) {
        Player player = event.getPlayer();
        // Event logic
    }
}
```

**Key Changes:**
- Remove `implements Listener`
- `@EventHandler` → `@Subscribe`
- `ProxiedPlayer` → `Player`
- Event names may differ: `ServerConnectEvent` → `ServerPreConnectEvent`

**Common Event Mappings:**
- `ServerConnectEvent` → `ServerPreConnectEvent`
- `PostLoginEvent` → `LoginEvent`
- `PlayerDisconnectEvent` → `DisconnectEvent`
- `PluginMessageEvent` → `PluginMessageEvent` (different handling)

---

### 3. ConfigManager

**Key Changes Needed:**
- Replace `File` with `Path` from `java.nio.file`
- Update file operations to use `Files` class
- Replace `plugin.getDataFolder()` with `plugin.getDataDirectory()`
- Replace `plugin.getLogger()` calls (Logger is SLF4J, not java.util.logging)

**Example:**
```java
// BungeeCord
File configFile = new File(plugin.getDataFolder(), "config.yml");

// Velocity  
Path configFile = plugin.getDataDirectory().resolve("config.yml");
```

---

### 4. ServerManager

**Key Changes:**
- `ServerInfo` → `RegisteredServer`
- `plugin.getProxy().getServers()` → `plugin.getServer().getAllServers()`
- `plugin.getProxy().getServerInfo(name)` → `plugin.getServer().getServer(name)`

**Example:**
```java
// BungeeCord
public ServerInfo getHubServer() {
    return plugin.getProxy().getServerInfo("hub");
}

// Velocity
public Optional<RegisteredServer> getHubServer() {
    return plugin.getServer().getServer("hub");
}
```

---

### 5. SpigotCommunication (Plugin Messaging)

**BungeeCord Pattern:**
```java
public class SpigotCommunication implements Listener {
    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if(event.getTag().equals("purgatory:main")) {
            byte[] data = event.getData();
            ByteArrayDataInput in = ByteStreams.newDataInput(data);
            String type = in.readUTF();
            // Handle message
        }
    }
    
    public void send(UUID uuid, String[] str) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF(str[0]);
        server.sendData("purgatory:main", output.toByteArray());
    }
}
```

**Velocity Pattern:**
```java
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

public class SpigotCommunication {
    private final MainVelocity plugin;
    public static final MinecraftChannelIdentifier CHANNEL = 
        MinecraftChannelIdentifier.from("purgatory:main");
    
    public SpigotCommunication(MainVelocity plugin) {
        this.plugin = plugin;
        // Register channel
        plugin.getServer().getChannelRegistrar().register(CHANNEL);
    }
    
    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().equals(CHANNEL)) {
            return;
        }
        
        byte[] data = event.getData();
        ByteArrayDataInput in = ByteStreams.newDataInput(data);
        String type = in.readUTF();
        // Handle message
    }
    
    public void send(RegisteredServer server, UUID uuid, String[] str) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF(str[0]);
        server.sendPluginMessage(CHANNEL, output.toByteArray());
    }
}
```

---

### 6. Factory Classes

Factory classes (BanFactory, WarningFactory) require minimal changes:
- Update logger calls from `java.util.logging.Logger` to `org.slf4j.Logger`
- Replace `plugin.getLogger().log(Level.INFO, msg)` with `plugin.getLogger().info(msg)`
- Ensure all references to MainBungee are changed to MainVelocity

---

## 🎨 Text Component Conversion

**BungeeCord uses net.md_5.bungee.api.chat.TextComponent**
**Velocity uses net.kyori.adventure.text.Component**

```java
// BungeeCord
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

player.sendMessage(new TextComponent(ChatColor.RED + "You are banned!"));
player.disconnect(new TextComponent("Banned"));

// Velocity
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

player.sendMessage(Component.text("You are banned!").color(NamedTextColor.RED));
player.disconnect(Component.text("Banned"));
```

**Legacy Color Code Support:**
```java
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

String legacyText = "&cYou are banned!";
Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(legacyText);
player.sendMessage(component);
```

---

## 🔌 Player Management

**Key API Differences:**

| BungeeCord | Velocity |
|------------|----------|
| `ProxiedPlayer` | `Player` |
| `player.getName()` | `player.getUsername()` |
| `player.getUniqueId()` | `player.getUniqueId()` |
| `player.getServer()` | `player.getCurrentServer()` (returns Optional) |
| `player.connect(ServerInfo)` | `player.createConnectionRequest(RegisteredServer).fireAndForget()` |
| `player.disconnect(TextComponent)` | `player.disconnect(Component)` |
| `player.hasPermission(String)` | `player.hasPermission(String)` |

---

## 🌐 Server Management

**Get all players:**
```java
// BungeeCord
Collection<ProxiedPlayer> players = plugin.getProxy().getPlayers();

// Velocity
Collection<Player> players = plugin.getServer().getAllPlayers();
```

**Get player by UUID:**
```java
// BungeeCord
ProxiedPlayer player = plugin.getProxy().getPlayer(uuid);

// Velocity
Optional<Player> player = plugin.getServer().getPlayer(uuid);
```

**Get player by name:**
```java
// BungeeCord
ProxiedPlayer player = plugin.getProxy().getPlayer(name);

// Velocity
Optional<Player> player = plugin.getServer().getPlayer(name);
```

---

## ✅ Testing Checklist

After completing the conversion:

1. ✅ Verify plugin loads on Velocity server
2. ✅ Test each command works correctly
3. ✅ Test event handlers trigger properly
4. ✅ Verify plugin messaging between Spigot and Velocity
5. ✅ Check player ban/unban functionality
6. ✅ Verify temporary ban timer system
7. ✅ Test warning system
8. ✅ Verify server switching and purgatory world teleportation
9. ✅ Check configuration loading
10. ✅ Test all permissions

---

## 📚 Additional Resources

- [Velocity API Documentation](https://jd.papermc.io/velocity/3.0.0/index.html)
- [Velocity Plugin Development Guide](https://docs.papermc.io/velocity/dev/getting-started)
- [Adventure API (Text Components)](https://docs.advntr.dev/)
- [Velocity GitHub Examples](https://github.com/PaperMC/Velocity/tree/dev/3.0.0/proxy/src/main/java/com/velocitypowered/proxy)

---

## 🚀 Quick Start

1. Copy each file from `Bungee/` package to `Velocity/` package
2. Update imports according to this guide
3. Apply the conversion patterns shown above
4. Build with Maven: `mvn clean package`
5. Test the generated JAR on your Velocity proxy

---

## ⚠️ Important Notes

- Velocity requires Java 17+ (already configured in pom.xml)
- The plugin JAR will work on BOTH BungeeCord and Velocity if both packages are included
- Adventure API (used by Velocity) is more modern than BungeeCord's chat API
- Optional returns are common in Velocity API - always handle empty() cases
- SLF4J logger methods: `debug()`, `info()`, `warn()`, `error()`

---

## 📝 Example: Complete BanCommand Conversion

See the conversion pattern in action. You can use this as a template for other commands.

**Original BungeeCord version** is in `src/main/java/ro/deiutzblaxo/Purgatory/Bungee/Commands/BanCommand.java`

**Velocity version template:**
```java
package ro.deiutzblaxo.Purgatory.Velocity.Commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import ro.deiutzblaxo.Purgatory.Velocity.MainVelocity;

import java.util.Optional;
import java.util.UUID;

public class BanCommand implements SimpleCommand {
    private final MainVelocity plugin;
    
    public BanCommand(MainVelocity plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        
        // Copy logic from BungeeCord version, adapting:
        // 1. TextComponent → Component
        // 2. ChatColor.translateAlternateColorCodes → LegacyComponentSerializer
        // 3. plugin.getProxy() → plugin.getServer()
        // 4. Handle Optional returns
        
        if (args.length < 2) {
            source.sendMessage(deserialize("&cUsage: /ban <player> <reason>"));
            return;
        }
        
        String playerName = args[0];
        Optional<Player> targetOpt = plugin.getServer().getPlayer(playerName);
        
        if (!targetOpt.isPresent()) {
            source.sendMessage(deserialize("&cPlayer not found!"));
            return;
        }
        
        Player target = targetOpt.get();
        // Continue with ban logic...
    }
    
    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("purgatory.ban");
    }
    
    private Component deserialize(String legacy) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(legacy);
    }
}
```

---

Good luck with your conversion! The foundation is solid, and with your Java expertise, you should be able to complete the remaining files efficiently using these patterns.
