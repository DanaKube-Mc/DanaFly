package fr.danakube.danafly.hook;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import fr.danakube.danafly.DanaFly;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PlaceholderAPIHookTest {

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
    @DisplayName("DanaFly and TempFly expansions should have correct identifiers and metadata")
    void shouldHaveCorrectIdentifiers() {
        PlaceholderAPIHook.initialize(plugin);

        PlaceholderAPIHook.DanaFlyExpansion danaExpansion = new PlaceholderAPIHook.DanaFlyExpansion();
        assertThat(danaExpansion.getIdentifier()).isEqualTo("danafly");
        assertThat(danaExpansion.getAuthor()).isEqualTo("DanaKube");
        assertThat(danaExpansion.persist()).isTrue();

        PlaceholderAPIHook.TempFlyLegacyExpansion tempExpansion = new PlaceholderAPIHook.TempFlyLegacyExpansion();
        assertThat(tempExpansion.getIdentifier()).isEqualTo("tempfly");
        assertThat(tempExpansion.getAuthor()).isEqualTo("DanaKube");
        assertThat(tempExpansion.persist()).isTrue();
    }

    @Test
    @DisplayName("Placeholder resolution should return time values for player")
    void shouldResolvePlaceholdersForPlayer() {
        PlaceholderAPIHook.initialize(plugin);
        PlayerMock player = server.addPlayer("DanaTester");

        // Player initially has default flight time or 0
        String formatted = PlaceholderAPIHook.resolvePlaceholder(player, "time-formatted");
        assertThat(formatted).isNotNull();

        String days = PlaceholderAPIHook.resolvePlaceholder(player, "time-days");
        assertThat(days).isNotNull();

        String seconds = PlaceholderAPIHook.resolvePlaceholder(player, "time-seconds");
        assertThat(seconds).isNotNull();

        String invalid = PlaceholderAPIHook.resolvePlaceholder(player, "unknown-placeholder");
        assertThat(invalid).isNull();
    }

    @Test
    @DisplayName("Both expansions should return identical placeholder results")
    void shouldReturnIdenticalResultsAcrossExpansions() {
        PlaceholderAPIHook.initialize(plugin);
        PlayerMock player = server.addPlayer("DanaTester2");

        PlaceholderAPIHook.DanaFlyExpansion danaExpansion = new PlaceholderAPIHook.DanaFlyExpansion();
        PlaceholderAPIHook.TempFlyLegacyExpansion tempExpansion = new PlaceholderAPIHook.TempFlyLegacyExpansion();

        String danaRes = danaExpansion.onPlaceholderRequest(player, "time-seconds");
        String tempRes = tempExpansion.onPlaceholderRequest(player, "time-seconds");

        assertThat(danaRes).isEqualTo(tempRes);
    }

    @Test
    @DisplayName("Should resolve all max time and reserve placeholders")
    void shouldResolveMaxTimePlaceholders() {
        PlaceholderAPIHook.initialize(plugin);
        fr.danakube.danafly.util.V.maxTimeBase = 10800.0; // 3h
        fr.danakube.danafly.util.V.maxTimeGroups.clear();

        PlayerMock player = server.addPlayer("ReserveTester");
        plugin.getFlightManager().getUser(player).setTime(5400.0); // 1h30m (50%)

        assertThat(PlaceholderAPIHook.resolvePlaceholder(player, "max_time")).isEqualTo("10800");
        assertThat(PlaceholderAPIHook.resolvePlaceholder(player, "max_time_formatted")).isNotNull();
        assertThat(PlaceholderAPIHook.resolvePlaceholder(player, "time_percent")).isEqualTo("50%");
        assertThat(PlaceholderAPIHook.resolvePlaceholder(player, "time_ratio")).contains("/");
        assertThat(PlaceholderAPIHook.resolvePlaceholder(player, "time_remaining_reserve")).isEqualTo("5400");

        // Bypass / unlimited player
        PlayerMock admin = server.addPlayer("AdminReserve");
        admin.setOp(true);
        assertThat(PlaceholderAPIHook.resolvePlaceholder(admin, "max_time")).isEqualTo("-1");
        assertThat(PlaceholderAPIHook.resolvePlaceholder(admin, "time_remaining_reserve")).isEqualTo("-1");
    }
}
