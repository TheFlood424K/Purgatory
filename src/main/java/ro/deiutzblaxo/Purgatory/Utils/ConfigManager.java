package ro.deiutzblaxo.Purgatory.Utils;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration Manager for Purgatory plugin
 * Compatible with both Velocity and Spigot implementations
 */
public class ConfigManager {
    private static Path dataDirectory;
    private static Map<String, String> messages = new HashMap<>();
    
    // Default messages
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
     * Initialize the ConfigManager with a data directory
     * @param directory The data directory path
     */
    public static void init(Path directory) {
        dataDirectory = directory;
        try {
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }
            loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Load configuration from file
     */
    private static void loadConfig() {
        // TODO: Implement config file loading
        // For now, using default messages
    }
    
    /**
     * Reload the configuration
     */
    public static void reload() {
        messages.clear();
        loadConfig();
    }
    
    /**
     * Get a message from the config
     * @param key The message key
     * @return The message string
     */
    public static String getMessage(String key) {
        return messages.getOrDefault(key, "Message not found: " + key);
    }

        /**
     * Get all messages from the config
     * @return The messages map
     */
    public static Map<String, String> getMessages() {
        return messages;
    }
    
    /**
     * Get the data directory
     * @return The data directory path
     */
    public static Path getDataDirectory() {
        return dataDirectory;
    }
}
