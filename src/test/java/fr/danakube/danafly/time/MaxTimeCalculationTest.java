package fr.danakube.danafly.time;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.util.V;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MaxTimeCalculationTest {

    private ServerMock server;
    private DanaFly plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(DanaFly.class);

        // Configure test base and groups
        V.maxTimeBase = 10800.0; // 3h
        V.maxTimeGroups.clear();
        V.maxTimeGroups.put("vip", 18000.0);  // 5h
        V.maxTimeGroups.put("mvp", 36000.0);  // 10h
        V.maxTimeGroups.put("infinite", -1.0); // illimité
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Default player without permissions should receive maxTimeBase")
    void defaultPlayerShouldGetBase() {
        PlayerMock player = server.addPlayer("StandardPlayer");
        double maxTime = plugin.getTimeManager().getMaxTime(player.getUniqueId());
        assertThat(maxTime).isEqualTo(10800.0);
    }

    @Test
    @DisplayName("Player with modern danafly.max.vip should receive VIP limit")
    void playerWithVipPermissionShouldGetVipLimit() {
        PlayerMock player = server.addPlayer("VipPlayer");
        player.addAttachment(plugin, "danafly.max.vip", true);

        double maxTime = plugin.getTimeManager().getMaxTime(player.getUniqueId());
        assertThat(maxTime).isEqualTo(18000.0);
    }

    @Test
    @DisplayName("Player with legacy tempfly.max.mvp should receive MVP limit")
    void playerWithLegacyPermissionShouldGetMvpLimit() {
        PlayerMock player = server.addPlayer("LegacyMvpPlayer");
        player.addAttachment(plugin, "tempfly.max.mvp", true);

        double maxTime = plugin.getTimeManager().getMaxTime(player.getUniqueId());
        assertThat(maxTime).isEqualTo(36000.0);
    }

    @Test
    @DisplayName("Player with multiple groups should get the highest limit")
    void playerWithMultipleGroupsShouldGetHighest() {
        PlayerMock player = server.addPlayer("MultiGroupPlayer");
        player.addAttachment(plugin, "danafly.max.vip", true);
        player.addAttachment(plugin, "danafly.max.mvp", true);

        double maxTime = plugin.getTimeManager().getMaxTime(player.getUniqueId());
        assertThat(maxTime).isEqualTo(36000.0);
    }

    @Test
    @DisplayName("Player with infinite group should receive -1 (unlimited)")
    void playerWithInfiniteGroupShouldGetUnlimited() {
        PlayerMock player = server.addPlayer("InfinitePlayer");
        player.addAttachment(plugin, "danafly.max.vip", true);
        player.addAttachment(plugin, "danafly.max.infinite", true);

        double maxTime = plugin.getTimeManager().getMaxTime(player.getUniqueId());
        assertThat(maxTime).isEqualTo(-1.0);
    }

    @Test
    @DisplayName("Player with bypass or op should receive -1 (unlimited)")
    void playerWithBypassShouldGetUnlimited() {
        PlayerMock player = server.addPlayer("BypassPlayer");
        player.addAttachment(plugin, "danafly.max.bypass", true);

        double maxTime = plugin.getTimeManager().getMaxTime(player.getUniqueId());
        assertThat(maxTime).isEqualTo(-1.0);

        PlayerMock opPlayer = server.addPlayer("OpPlayer");
        opPlayer.setOp(true);
        assertThat(plugin.getTimeManager().getMaxTime(opPlayer.getUniqueId())).isEqualTo(-1.0);
    }
}
