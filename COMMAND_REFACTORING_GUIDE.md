# Command Refactoring Guide for Messages Utility

This guide provides the step-by-step pattern for refactoring all Velocity command classes to use the new `Messages` utility class.

## ✅ Completed

- **BanCommand.java** - Fully refactored and serves as reference implementation
- **Messages.java** - Created utility class with Adventure API support

## 📋 Refactoring Pattern

### Step 1: Update Imports

**REMOVE these imports:**
```java
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
```

**ADD this import:**
```java
import ro.deiutzblaxo.Purgatory.Utils.Messages;
```

### Step 2: Remove deserialize() Method

**DELETE:**
```java
private Component deserialize(String legacy) {
    return LegacyComponentSerializer.legacyAmpersand().deserialize(legacy);
}
```

### Step 3: Replace Message Calls

**OLD Pattern:**
```java
sender.sendMessage(deserialize(
    plugin.getConfigManager().getString(plugin.getConfigManager().getMessages(), "NoPermission")));
```

**NEW Pattern:**
```java
if (sender instanceof Player) {
    Messages.sendMessage((Player) sender, "noPermission");
}
```

**OLD Pattern with Placeholders:**
```java
sender.sendMessage(deserialize(
    plugin.getConfigManager().getString(plugin.getConfigManager().getMessages(), "PlayerNotFound")
    .replaceAll("%player%", playerName)));
```

**NEW Pattern with Placeholders:**
```java
if (sender instanceof Player) {
    Messages.sendMessage((Player) sender, "playerNotFound",
        "player", playerName);
}
```

### Step 4: Replace String-only Message Gets

**OLD Pattern:**
```java
String reason = plugin.getConfigManager().getString(
    plugin.getConfigManager().getMessages(), "Ban.DefaultReason");
```

**NEW Pattern:**
```java
String reason = Messages.getMessage("defaultReason");
```

## 📝 Commands Requiring Refactoring

### Priority Order

1. **TempBanCommand.java** - Similar to BanCommand, temporary bans
2. **UnbanCommand.java** - Remove bans
3. **CheckCommand.java** - Player status checks
4. **EditCommand.java** - Edit ban reasons
5. **TeleportCommand.java** - Player teleportation  
6. **PurgatoryCommand.java** - Main command
7. **ReloadCommand.java** - Config reload

## 🔑 Key Message Keys

Based on BanCommand implementation, these message keys should be standardized:

- `noPermission` - No permission message
- `invalidUsage` - Invalid command usage
- `playerNotFound` - Player not found
- `alreadyBanned` - Player already banned
- `defaultReason` - Default ban reason
- `banMessage` - Ban notification to player
- `banConfirm` - Ban confirmation to sender
- `tempBanMessage` - Temp ban notification
- `tempBanConfirm` - Temp ban confirmation
- `unbanSuccess` - Unban success message
- `checkPlayer` - Player check info
- `editBan` - Edit ban confirmation
- `teleportSuccess` - Teleport success message

## 💡 Benefits of This Refactoring

1. **Centralized messaging** - All messages in one place
2. **Cleaner code** - Reduced boilerplate
3. **Consistent API** - Same pattern across all commands
4. **Adventure API ready** - Modern Velocity messaging
5. **Easier maintenance** - Update messages in one file
6. **Type safety** - Placeholder system with varargs

## 🚀 Testing Checklist

After refactoring each command, verify:

- [ ] Command compiles without errors
- [ ] All imports are correct
- [ ] No deserialize() method remains
- [ ] All messages use Messages.sendMessage() or Messages.getMessage()
- [ ] Placeholder replacements work correctly
- [ ] Permission checks still function
- [ ] Error messages display properly

## 📖 Reference Implementation

See `BanCommand.java` for a complete working example of the refactored pattern.
