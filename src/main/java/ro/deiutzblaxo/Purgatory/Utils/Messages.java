package ro.deiutzblaxo.Purgatory.Utils;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Messages utility class for Purgatory plugin
 * Compatible with Velocity proxy using Adventure API
 */
public class Messages {
    
    private static final Map<String, String> messages = new HashMap<>();
    
    // Initialize default messages
    static {
        messages.put("playerNotFound", "Player %player% not found.");
        messages.put("banMessage", "You have been banned by %sender% for: %reason%");
        messages.put("tempBanMessage", "You have been temporarily banned for %time% by %sender% for: %reason%");
        messages.put("unbanSuccess", "Player %player% has been unbanned by %sender%.");
        messages.put("checkPlayer", "Player: %player%\nServer: %server%\nIP: %ip%");
        messages.put("editBan", "Ban reason for %player% has been updated to: %reason% by %sender%");
        messages.put("teleportSuccess", "Teleporting to %player% on server %server%");
        messages.put("banConfirm", "Player %player% has been banned.");
        messages.put("tempBanConfirm", "Player %player% has been temporarily banned for %time%.");
    }
    
    /**
     * Get a message with placeholders replaced
     */
    public static String getMessage(String key, Object... replacements) {
        String message = messages.getOrDefault(key, "Message not found: " + key);
        
        // Replace placeholders
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                String placeholder = "%" + replacements[i] + "%";
                String value = String.valueOf(replacements[i + 1]);
                message = message.replace(placeholder, value);
            }
        }
        
        return message;
    }
    
    /**
     * Send a message to a Velocity player
     */
    public static void sendMessage(Player player, String key, Object... replacements) {
        String message = getMessage(key, replacements);
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        player.sendMessage(component);
    }
    
    /**
     * Set a custom message
     */
    public static void setMessage(String key, String message) {
        messages.put(key, message);
    }
    
    /**
     * Load messages from ConfigManager
     */
    public static void loadMessages(Map<String, String> configMessages) {
        messages.putAll(configMessages);
    }
}
