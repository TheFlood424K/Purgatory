package ro.deiutzblaxo.Purgatory.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Server Manager for Purgatory plugin
 * Manages player connections and server information
 * Compatible with both Velocity and Spigot implementations
 */
public class ServerManager {
    private static Map<UUID, String> playerServers = new HashMap<>();
    private static Map<UUID, Long> playerConnectTimes = new HashMap<>();
    
    /**
     * Register a player connection to a server
     * @param playerId The player's UUID
     * @param serverName The server name
     */
    public static void registerPlayerConnection(UUID playerId, String serverName) {
        playerServers.put(playerId, serverName);
        playerConnectTimes.put(playerId, System.currentTimeMillis());
    }
    
    /**
     * Unregister a player disconnection
     * @param playerId The player's UUID
     */
    public static void unregisterPlayer(UUID playerId) {
        playerServers.remove(playerId);
        playerConnectTimes.remove(playerId);
    }
    
    /**
     * Get the server a player is connected to
     * @param playerId The player's UUID
     * @return Optional containing server name if found
     */
    public static Optional<String> getPlayerServer(UUID playerId) {
        return Optional.ofNullable(playerServers.get(playerId));
    }
    
    /**
     * Get the time a player connected to their current server
     * @param playerId The player's UUID
     * @return Optional containing connect time in milliseconds
     */
    public static Optional<Long> getPlayerConnectTime(UUID playerId) {
        return Optional.ofNullable(playerConnectTimes.get(playerId));
    }
    
    /**
     * Check if a player is tracked
     * @param playerId The player's UUID
     * @return true if player is tracked
     */
    public static boolean isPlayerTracked(UUID playerId) {
        return playerServers.containsKey(playerId);
    }
    
    /**
     * Get all tracked players
     * @return Map of player IDs to server names
     */
    public static Map<UUID, String> getAllPlayers() {
        return new HashMap<>(playerServers);
    }
    
    /**
     * Clear all tracked data
     */
    public static void clearAll() {
        playerServers.clear();
        playerConnectTimes.clear();
    }

        /**
     * Get the purgatory server name from config
     * @return The purgatory server name
     */
    public static String getPurgatoryServer() {
        // TODO: Load from config - for now return default
        return "purgatory";
    }
    
    /**
     * Get the hub server name from config
     * @return The hub server name
     */
    public static String getHubServer() {
        // TODO: Load from config - for now return default
        return "hub";
    }
}
