package fr.danakube.danafly.hook;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.hook.region.CompatRegion;
import fr.danakube.danafly.hook.region.plugins.WorldGuardHook;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WorldGuardHookTest {

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
    @DisplayName("WorldGuardHook should handle absent WorldGuard plugin gracefully")
    void shouldHandleDisabledWorldGuardGracefully() {
        WorldGuardHook hook = new WorldGuardHook(plugin);
        assertThat(hook.isEnabled()).isFalse();

        World world = server.addSimpleWorld("world");
        Location loc = new Location(world, 100, 64, 100);

        CompatRegion[] regions = hook.getApplicableRegions(loc);
        assertThat(regions).isNotNull();
        assertThat(regions).isEmpty();
        assertThat(hook.getRegionSet(loc)).isNull();
    }

    @Test
    @DisplayName("WorldGuardHook should return empty regions array when location is null")
    void shouldReturnEmptyWhenLocationIsNull() {
        WorldGuardHook hook = new WorldGuardHook(plugin);
        CompatRegion[] regions = hook.getApplicableRegions(null);
        assertThat(regions).isNotNull();
        assertThat(regions).isEmpty();
    }
}
