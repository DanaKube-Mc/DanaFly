package fr.danakube.danafly.migration;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.util.data.DataMigrationManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class DataMigrationTest {

    private ServerMock server;
    private DanaFly plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(DanaFly.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Should migrate data.yml and custom config values when TempFly folder exists")
    void shouldMigrateDataAndConfigs(@TempDir Path tempDir) throws IOException {
        File fakePlugins = tempDir.toFile();
        File fakeTempFly = new File(fakePlugins, "TempFly");
        fakeTempFly.mkdirs();

        // Create legacy data.yml
        File oldData = new File(fakeTempFly, "data.yml");
        Files.writeString(oldData.toPath(), "test_uuid:\n  time: 1234.5\n");

        // Create legacy config.yml with custom value
        File oldConfig = new File(fakeTempFly, "config.yml");
        Files.writeString(oldConfig.toPath(), "system:\n  save: 15\n");

        File fakeDanaFly = new File(fakePlugins, "DanaFly");

        // Create a mock plugin wrapper pointing to fakeDanaFly
        DanaFly mockPlugin = org.mockito.Mockito.spy(plugin);
        org.mockito.Mockito.doReturn(fakeDanaFly).when(mockPlugin).getDataFolder();

        boolean migrated = DataMigrationManager.checkAndPerformMigration(mockPlugin);
        assertThat(migrated).isTrue();

        // Verify data.yml copied
        File migratedData = new File(fakeDanaFly, "data.yml");
        assertThat(migratedData).exists();
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(migratedData);
        assertThat(yaml.getDouble("test_uuid.time")).isEqualTo(1234.5);

        // Verify config.yml migrated custom value
        File migratedConfig = new File(fakeDanaFly, "config.yml");
        assertThat(migratedConfig).exists();
        YamlConfiguration configYaml = YamlConfiguration.loadConfiguration(migratedConfig);
        assertThat(configYaml.getInt("system.save")).isEqualTo(15);
    }
}
