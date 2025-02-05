package pro.vulpine.vCrates.manager;

import pro.vulpine.vCrates.VCrates;
import pro.vulpine.vCrates.instance.Profile;
import pro.vulpine.vCrates.utils.logger.Logger;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ProfileManager {

    private final VCrates plugin;

    private final List<Profile> profiles;

    public ProfileManager(VCrates plugin) {
        this.plugin = plugin;

        profiles = new ArrayList<>();

        createTables();
    }

    private void createTables() {

        String createProfileTables = "CREATE TABLE IF NOT EXISTS `profiles` (" +
                " owner VARCHAR(36) NOT NULL," +
                " PRIMARY KEY (owner)" +
                ")";

        String createKeysTable = "CREATE TABLE IF NOT EXISTS `keys` (" +
                " owner VARCHAR(36) NOT NULL," +
                " key_type VARCHAR(255) NOT NULL," +
                " key_count INT NOT NULL," +
                " PRIMARY KEY (owner, key_type)" +
                ")";

        plugin.getStorageManager().executeUpdate(createProfileTables);
        plugin.getStorageManager().executeUpdate(createKeysTable);

    }

    public CompletableFuture<Profile> loadProfile(UUID owner, boolean createIfNotFound) {

        Logger.info("Loading profile for " + owner, "ProfileManager");

        for (Profile profile : profiles) {

            if (profile.getOwner().equals(owner)) {

                Logger.info("Profile for " + owner + " already loaded. Skipping.", "ProfileManager");

                return CompletableFuture.completedFuture(profile);

            }

        }

        CompletableFuture<Profile> future = new CompletableFuture<>();

        String query = "SELECT owner FROM `profiles` WHERE owner = ?";

        plugin.getStorageManager().executeQuery(query, owner.toString()).thenAccept(rs -> {

           try {

               if (rs != null) {

                   if (rs.next()) {

                       Profile profile = new Profile(this, owner, new HashMap<>());

                       loadKeysForProfile(owner, profile).thenRun(() -> {

                           profiles.add(profile);

                           Logger.info("Loaded profile for " + owner, "ProfileManager");

                           future.complete(profile);

                       }).exceptionally(ex -> {

                           Logger.error("Error while loading keys for " + owner + ": " + ex.getMessage(), "ProfileManager");

                           future.completeExceptionally(ex);
                           return null;

                       });

                   } else {

                       Logger.warn("Profile for " + owner + " not found.", "ProfileManager");

                       if (createIfNotFound) {

                           createProfile(owner, true);

                           future.complete(getProfile(owner));

                       } else {

                           future.complete(null);

                       }
                   }
               } else {

                   Logger.error("Error while loading profile for " + owner + ": ResultSet is null", "ProfileManager");
                   future.complete(null);

               }

           } catch (SQLException e) {

               Logger.error("Error while loading profile for " + owner + ": " + e.getMessage(), "ProfileManager");

               future.complete(null);

           } finally {

               try {

                   plugin.getStorageManager().closeResources(rs, rs.getStatement(), rs.getStatement().getConnection());

               } catch (SQLException e) {

                   Logger.error("Error while closing resources for " + owner + ": " + e.getMessage(), "ProfileManager");

               }

           }

        });

        return future;

    }

    public CompletableFuture<Void> loadKeysForProfile(UUID owner, Profile profile) {

        String query = "SELECT key_type, key_count FROM `keys` WHERE owner = ?";

        return plugin.getStorageManager().executeQuery(query, owner.toString()).thenCompose(rs -> {

            try {

                if (rs != null) {

                    while (rs.next()) {

                        profile.getKeys().put(rs.getString("key_type"), rs.getInt("key_count"));

                    }

                }

                return CompletableFuture.completedFuture(null);

            } catch (SQLException e) {

                Logger.error("Error while loading keys for " + owner + ": " + e.getMessage(), "ProfileManager");

                return CompletableFuture.failedFuture(e);

            } finally {

                try {

                    plugin.getStorageManager().closeResources(rs, rs.getStatement(), rs.getStatement().getConnection());

                } catch (SQLException e) {

                    Logger.error("Error while closing resources for " + owner + ": " + e.getMessage(), "ProfileManager");

                }

            }

        });

    }

    public void unloadProfile(UUID owner) {

        Logger.info("Unloading profile for " + owner, "ProfileManager");

        boolean removed = profiles.removeIf(profile -> profile.getOwner().equals(owner));

        if (removed) {

            Logger.info("Unloaded profile for " + owner, "ProfileManager");

        } else {

            Logger.info("Profile for " + owner + " not loaded. Skipping.", "ProfileManager");

        }

    }

    public void createProfile(UUID owner, boolean loadAfter) {

        Logger.info("Creating profile for " + owner, "ProfileManager");

        String query = "INSERT INTO profiles (owner) VALUES (?)";

        CompletableFuture<Integer> updateFuture = plugin.getStorageManager().executeUpdate(query, owner.toString());

        if (loadAfter) {
            updateFuture.thenRun(() -> {
                loadProfile(owner, false);
            });
        }

        Logger.info("Created profile for " + owner, "ProfileManager");
    }


    public CompletableFuture<Void> updateProfile(Profile profile) {

        Logger.info("Updating profile for " + profile.getOwner(), "ProfileManager");

        String query = "INSERT INTO `keys` (owner, key_type, key_count) VALUES (?, ?, ?)" +
                " ON DUPLICATE KEY UPDATE key_count = VALUES(key_count)";

        List<CompletableFuture<Integer>> futures = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : profile.getKeys().entrySet()) {

            CompletableFuture<Integer> updateFuture =
                    plugin.getStorageManager().executeUpdate(query, profile.getOwner().toString(), entry.getKey(), entry.getValue());

            futures.add(updateFuture);

        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> Logger.info("Updated profile for " + profile.getOwner(), "ProfileManager"));

    }

    public Profile getProfile(UUID owner) {

        for (Profile profile : profiles) {

            if (profile.getOwner().equals(owner)) {
                return profile;
            }

        }

        return null;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public VCrates getPlugin() {
        return plugin;
    }
}
