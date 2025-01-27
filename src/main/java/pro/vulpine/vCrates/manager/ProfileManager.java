package pro.vulpine.vCrates.manager;

import pro.vulpine.vCrates.VCrates;
import pro.vulpine.vCrates.instance.Profile;
import pro.vulpine.vCrates.utils.Logger;

import java.sql.SQLException;
import java.util.*;

public class ProfileManager {

    private final VCrates plugin;

    private final List<Profile> profiles;

    public ProfileManager(VCrates plugin) {
        this.plugin = plugin;

        profiles = new ArrayList<>();

        createTables();
    }

    private void createTables() {

        String query = "CREATE TABLE IF NOT EXISTS `keys` (" +
                " owner VARCHAR(32) NOT NULL," +
                " key_type VARCHAR(255) NOT NULL," +
                " key_count INT NOT NULL," +
                " PRIMARY KEY (owner, key_type)" +
                ")";

        plugin.getStorageManager().executeUpdate(query);

    }

    public void loadProfile(UUID owner, boolean createIfNotFound) {

        Logger.info("Loading profile for " + owner, "ProfileManager");

        for (Profile profile : profiles) {

            if (profile.getOwner().equals(owner)) {

                Logger.warn("Profile for " + owner + " already loaded. Skipping.", "ProfileManager");
                return;

            }

        }

        String query = "SELECT key_type, key_count FROM `keys` WHERE owner = ?";

        plugin.getStorageManager().executeQuery(query, owner).thenAccept(rs -> {

           try {

               Map<String, Integer> keys = new HashMap<>();

               while (rs != null && rs.next()) {

                   String keyType = rs.getString("key_type");
                   int keyCount = rs.getInt("key_count");

                   keys.put(keyType, keyCount);

               }

               if (!keys.isEmpty()) {

                   Profile profile = new Profile(this, owner, keys);

                   profiles.add(profile);

               } else if (createIfNotFound) {

                   createProfile(owner, true);

               }

               Logger.info("Loaded profile for " + owner, "ProfileManager");

           } catch (SQLException e) {

               Logger.error("Error while loading profile for " + owner + ": " + e.getMessage(), "ProfileManager");

           } finally {

               try {

                   if (rs != null) {

                       rs.close();

                   }

                   if (rs.getStatement() != null) {

                       rs.getStatement().close();

                   }

                   if (rs.getStatement().getConnection() != null) {

                       rs.getStatement().getConnection().close();

                   }

               } catch (SQLException e) {

                   Logger.error("Error while closing resources: ", "StorageManager");

               }

           }

        });

    }

    public void unloadProfile(UUID owner) {

        Logger.info("Unloading profile for " + owner, "ProfileManager");

        for (Profile profile : profiles) {

            if (profile.getOwner().equals(owner)) {

                profiles.remove(profile);

                Logger.info("Unloaded profile for " + owner, "ProfileManager");
                return;

            }

        }

        Logger.warn("Profile for " + owner + " is not loaded. Skipping.", "ProfileManager");

    }

    public void createProfile(UUID owner, boolean loadAfter) {

        Profile profile = new Profile(this, owner, new HashMap<>());

        profiles.add(profile);

    }

}
