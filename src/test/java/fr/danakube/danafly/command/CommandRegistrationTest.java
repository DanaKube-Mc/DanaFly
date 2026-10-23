package fr.danakube.danafly.command;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import fr.danakube.danafly.DanaFly;
import org.bukkit.command.PluginCommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandRegistrationTest {

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
    @DisplayName("DanaFly main command should be registered with all required aliases")
    void mainCommandShouldBeRegisteredWithAliases() {
        PluginCommand cmd = plugin.getCommand("danafly");
        assertThat(cmd).isNotNull();
        assertThat(cmd.getAliases()).contains("df", "tempfly", "tf");
    }

    @Test
    @DisplayName("Fly command should be registered and have an active executor")
    void flyCommandShouldBeRegistered() {
        PluginCommand cmd = plugin.getCommand("fly");
        assertThat(cmd).isNotNull();
        assertThat(cmd.getExecutor()).isNotNull();
    }

    @Test
    @DisplayName("Player with permission should execute /fly without error")
    void playerShouldExecuteFlyCommand() {
        PlayerMock player = server.addPlayer("FlyGuy");
        player.addAttachment(plugin, "danafly.toggle.self", true);

        // Give player some flight time so they can fly
        DanaFly.getAPI().addFlightTime(player.getUniqueId(), 3600.0);

        boolean executed = server.dispatchCommand(player, "fly");
        assertThat(executed).isTrue();
    }
}
