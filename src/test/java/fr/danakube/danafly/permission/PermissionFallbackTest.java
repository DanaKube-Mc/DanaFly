package fr.danakube.danafly.permission;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.util.U;
import org.bukkit.command.ConsoleCommandSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PermissionFallbackTest {

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
    @DisplayName("Console should always have permission")
    void consoleShouldAlwaysHavePermission() {
        ConsoleCommandSender console = server.getConsoleSender();
        assertThat(U.hasPermission(console, "toggle.self")).isTrue();
        assertThat(U.hasPermission(console, "danafly.toggle.self")).isTrue();
        assertThat(U.hasPermission(console, "tempfly.toggle.self")).isTrue();
    }

    @Test
    @DisplayName("Player with modern permission danafly.* should have access")
    void playerWithModernPermissionShouldHaveAccess() {
        PlayerMock player = server.addPlayer("DanaModern");
        player.addAttachment(plugin, "danafly.toggle.self", true);

        assertThat(U.hasPermission(player, "toggle.self")).isTrue();
        assertThat(U.hasPermission(player, "danafly.toggle.self")).isTrue();
        assertThat(U.hasPermission(player, "tempfly.toggle.self")).isTrue();
    }

    @Test
    @DisplayName("Player with legacy permission tempfly.* should have access via fallback")
    void playerWithLegacyPermissionShouldHaveAccessViaFallback() {
        PlayerMock player = server.addPlayer("DanaLegacy");
        player.addAttachment(plugin, "tempfly.toggle.self", true);

        assertThat(U.hasPermission(player, "toggle.self")).isTrue();
        assertThat(U.hasPermission(player, "danafly.toggle.self")).isTrue();
        assertThat(U.hasPermission(player, "tempfly.toggle.self")).isTrue();
    }

    @Test
    @DisplayName("Player without either permission should be denied")
    void playerWithoutPermissionShouldBeDenied() {
        PlayerMock player = server.addPlayer("DanaNoob");
        assertThat(U.hasPermission(player, "toggle.self")).isFalse();
        assertThat(U.hasPermission(player, "danafly.toggle.self")).isFalse();
        assertThat(U.hasPermission(player, "tempfly.toggle.self")).isFalse();
    }
}
