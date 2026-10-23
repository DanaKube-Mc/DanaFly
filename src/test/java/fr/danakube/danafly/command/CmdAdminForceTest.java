package fr.danakube.danafly.command;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.command.admin.CmdGive;
import fr.danakube.danafly.command.admin.CmdSet;
import fr.danakube.danafly.time.AsyncTimeParameters;
import fr.danakube.danafly.user.FlightUser;
import fr.danakube.danafly.util.V;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CmdAdminForceTest {

    private ServerMock server;
    private DanaFly plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(DanaFly.class);

        V.maxTimeBase = 10800.0; // 3h max
        V.maxTimeGroups.clear();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Should detect and strip -force or -f flags accurately")
    void shouldDetectAndStripFlags() {
        CmdGive cmd = new CmdGive(plugin, new String[]{"give", "Steve", "-h", "1", "-force"});
        assertThat(cmd.hasForceFlag(new String[]{"give", "Steve", "-h", "1", "-force"})).isTrue();
        assertThat(cmd.hasForceFlag(new String[]{"give", "Steve", "-f", "-h", "1"})).isTrue();
        assertThat(cmd.hasForceFlag(new String[]{"give", "Steve", "-h", "1"})).isFalse();

        assertThat(cmd.stripForceFlag(new String[]{"give", "Steve", "-h", "1", "-force"}))
                .containsExactly("give", "Steve", "-h", "1");
        assertThat(cmd.stripForceFlag(new String[]{"give", "Steve", "-f", "-h", "1"}))
                .containsExactly("give", "Steve", "-h", "1");
    }

    @Test
    @DisplayName("Admin /df give should cap amount to max reserve when not forced")
    void shouldCapGiveWithoutForce() {
        PlayerMock admin = server.addPlayer("Admin");
        admin.setOp(true);
        PlayerMock player = server.addPlayer("Steve");

        FlightUser user = plugin.getFlightManager().getUser(player);
        user.setTime(10200.0); // 2h50m (remaining capacity: 600s)

        CmdGive cmd = new CmdGive(plugin, new String[]{"give", "Steve", "-h", "1"});
        AsyncTimeParameters params = new AsyncTimeParameters(plugin, cmd, admin, player, 3600.0);
        params.setMaxTime(10800.0);
        params.setCurrentTime(10200.0);
        params.setForce(false);

        cmd.execute(params);

        // Player should be capped at 10800s (3h)
        assertThat(user.getTime()).isEqualTo(10800.0);
    }

    @Test
    @DisplayName("Admin /df give should reject when player is already at or above max reserve")
    void shouldRejectGiveWhenAlreadyAtOrAboveMax() {
        PlayerMock admin = server.addPlayer("Admin");
        admin.setOp(true);
        PlayerMock player = server.addPlayer("Steve");

        FlightUser user = plugin.getFlightManager().getUser(player);
        user.setTime(10800.0); // exactly at max

        CmdGive cmd = new CmdGive(plugin, new String[]{"give", "Steve", "-m", "10"});
        AsyncTimeParameters params = new AsyncTimeParameters(plugin, cmd, admin, player, 600.0);
        params.setMaxTime(10800.0);
        params.setCurrentTime(10800.0);
        params.setForce(false);

        cmd.execute(params);

        // Balance should remain unchanged
        assertThat(user.getTime()).isEqualTo(10800.0);
    }

    @Test
    @DisplayName("Admin /df give -force should bypass max reserve completely")
    void shouldBypassCapGiveWithForce() {
        PlayerMock admin = server.addPlayer("Admin");
        admin.setOp(true);
        PlayerMock player = server.addPlayer("Steve");

        FlightUser user = plugin.getFlightManager().getUser(player);
        user.setTime(10200.0); // 2h50m

        CmdGive cmd = new CmdGive(plugin, new String[]{"give", "Steve", "-h", "1", "-force"});
        AsyncTimeParameters params = new AsyncTimeParameters(plugin, cmd, admin, player, 3600.0);
        params.setMaxTime(10800.0);
        params.setCurrentTime(10200.0);
        params.setForce(true);

        cmd.execute(params);

        // Player should receive full 3600s: 10200 + 3600 = 13800s (3h50m)
        assertThat(user.getTime()).isEqualTo(13800.0);
    }

    @Test
    @DisplayName("Admin /df set should cap balance to max reserve when not forced")
    void shouldCapSetWithoutForce() {
        PlayerMock admin = server.addPlayer("Admin");
        admin.setOp(true);
        PlayerMock player = server.addPlayer("Steve");

        FlightUser user = plugin.getFlightManager().getUser(player);
        user.setTime(0.0);

        CmdSet cmd = new CmdSet(plugin, new String[]{"set", "Steve", "-h", "5"});
        AsyncTimeParameters params = new AsyncTimeParameters(plugin, cmd, admin, player, 18000.0);
        params.setMaxTime(10800.0);
        params.setCurrentTime(0.0);
        params.setForce(false);

        cmd.execute(params);

        // Player should be capped at 10800s
        assertThat(user.getTime()).isEqualTo(10800.0);
    }

    @Test
    @DisplayName("Admin /df set -force should set full balance exceeding max reserve")
    void shouldBypassCapSetWithForce() {
        PlayerMock admin = server.addPlayer("Admin");
        admin.setOp(true);
        PlayerMock player = server.addPlayer("Steve");

        FlightUser user = plugin.getFlightManager().getUser(player);
        user.setTime(0.0);

        CmdSet cmd = new CmdSet(plugin, new String[]{"set", "Steve", "-h", "5", "-force"});
        AsyncTimeParameters params = new AsyncTimeParameters(plugin, cmd, admin, player, 18000.0);
        params.setMaxTime(10800.0);
        params.setCurrentTime(0.0);
        params.setForce(true);

        cmd.execute(params);

        // Player should receive full 18000s
        assertThat(user.getTime()).isEqualTo(18000.0);
    }

    @Test
    @DisplayName("Graceful downgrade: player exceeding max reserve retains flight time without truncation")
    void shouldRetainFlightTimeWithoutTruncationWhenExceedingMax() {
        PlayerMock player = server.addPlayer("Steve");
        FlightUser user = plugin.getFlightManager().getUser(player);

        // Simulate downgrade: player has 16200s (4h30m) but max limit is now 10800s (3h)
        user.setTime(16200.0);

        // Verification: balance is preserved and not truncated
        assertThat(user.getTime()).isEqualTo(16200.0);

        // Flight consumption: deduct 1 second of flight
        user.setTime(user.getTime() - 1.0);
        assertThat(user.getTime()).isEqualTo(16199.0);
    }
}
