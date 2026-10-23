package fr.danakube.danafly.util.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import fr.danakube.danafly.util.Console;

public class DataMigrationManager {

    private static final Set<String> DEPRECATED_SECTIONS = Set.of(
            "factions",
            "askyblock",
            "bentobox",
            "iridium",
            "iridiumskyblock",
            "mvdw",
            "mvdwplaceholderapi"
    );

    /**
     * Checks if plugins/TempFly exists and migrates data and configs to plugins/DanaFly.
     *
     * @param plugin The DanaFly plugin instance.
     * @return true if migration was performed, false otherwise.
     */
    public static boolean checkAndPerformMigration(Plugin plugin) {
        File dataFolder = plugin.getDataFolder();
        File pluginsFolder = dataFolder.getParentFile();
        if (pluginsFolder == null) {
            return false;
        }

        File oldFolder = new File(pluginsFolder, "TempFly");
        if (!oldFolder.exists() || !oldFolder.isDirectory()) {
            return false;
        }

        File currentDataFile = new File(dataFolder, "data.yml");
        if (currentDataFile.exists()) {
            // Already initialized, no migration needed
            return false;
        }

        Console.info("§e[DanaFly] Détection d'une installation TempFly existante. Démarrage de la migration...");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        try {
            // 1. Migrate data.yml
            File oldDataFile = new File(oldFolder, "data.yml");
            if (oldDataFile.exists()) {
                Files.copy(oldDataFile.toPath(), currentDataFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Console.info("§a[DanaFly] data.yml migré avec succès !");
            }

            // 2. Migrate SuperiorSkyblock2 configs / data if present
            File oldSuperiorConfig = new File(oldFolder, "hooks/SkyBlock/SuperiorSkyblock2.yml");
            if (oldSuperiorConfig.exists()) {
                File targetSuperiorConfig = new File(dataFolder, "hooks/SkyBlock/SuperiorSkyblock2.yml");
                targetSuperiorConfig.getParentFile().mkdirs();
                Files.copy(oldSuperiorConfig.toPath(), targetSuperiorConfig.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            File oldSuperiorData = new File(oldFolder, "SuperiorSkyblock2_data.yml");
            if (oldSuperiorData.exists()) {
                File targetSuperiorData = new File(dataFolder, "SuperiorSkyblock2_data.yml");
                Files.copy(oldSuperiorData.toPath(), targetSuperiorData.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            // 3. Migrate and merge config.yml
            migrateAndMergeYaml(plugin, oldFolder, dataFolder, "config.yml");

            // 4. Migrate and merge lang.yml
            migrateAndMergeYaml(plugin, oldFolder, dataFolder, "lang.yml");

            Console.info("§a[DanaFly] Migration détectée : Les données de plugins/TempFly ont été importées avec succès dans plugins/DanaFly !");
            return true;

        } catch (IOException e) {
            Console.severe("Erreur lors de la migration automatique des données depuis TempFly : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static void migrateAndMergeYaml(Plugin plugin, File oldFolder, File dataFolder, String fileName) throws IOException {
        File oldFile = new File(oldFolder, fileName);
        File targetFile = new File(dataFolder, fileName);

        if (!oldFile.exists()) {
            return;
        }

        // Save fresh default 4.0.0 resource first
        plugin.saveResource(fileName, false);

        YamlConfiguration newConfig = YamlConfiguration.loadConfiguration(targetFile);
        YamlConfiguration oldConfig = YamlConfiguration.loadConfiguration(oldFile);

        // Merge old user settings into new config, skipping deprecated sections
        for (String key : oldConfig.getKeys(true)) {
            if (isDeprecatedKey(key)) {
                continue;
            }
            if (!oldConfig.isConfigurationSection(key)) {
                newConfig.set(key, oldConfig.get(key));
            }
        }

        newConfig.save(targetFile);
        Console.info("§a[DanaFly] " + fileName + " migré et fusionné avec les options 4.0.0.");
    }

    private static boolean isDeprecatedKey(String key) {
        String lower = key.toLowerCase();
        for (String deprecated : DEPRECATED_SECTIONS) {
            if (lower.startsWith(deprecated + ".") || lower.equals(deprecated)
                    || lower.contains("." + deprecated + ".") || lower.endsWith("." + deprecated)) {
                return true;
            }
        }
        return false;
    }
}
