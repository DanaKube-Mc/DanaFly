package fr.danakube.danafly;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DanaFlyLoadTest {

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
    @DisplayName("Should successfully load and enable DanaFly plugin in MockBukkit")
    void shouldLoadPluginSuccessfully() {
        assertThat(plugin).isNotNull();
        assertThat(plugin.isEnabled()).isTrue();
        assertThat(DanaFly.getAPI()).isNotNull();
        assertThat(plugin.getName()).isEqualTo("DanaFly");
    }
}
