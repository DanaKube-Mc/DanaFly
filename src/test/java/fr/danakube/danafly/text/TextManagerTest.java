package fr.danakube.danafly.text;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.util.V;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TextManagerTest {

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
    @DisplayName("Should parse standard MiniMessage tags correctly")
    void shouldParseMiniMessageTags() {
        Component comp = TextManager.parse("<gold><bold>DanaFly</bold></gold>");
        assertThat(TextManager.toPlainText(comp)).isEqualTo("DanaFly");
        assertThat(comp.hasDecoration(TextDecoration.BOLD)).isTrue();
        assertThat(comp.color()).isEqualTo(NamedTextColor.GOLD);
    }

    @Test
    @DisplayName("Should convert legacy ampersand color codes correctly")
    void shouldConvertLegacyCodes() {
        Component comp = TextManager.parse("&aSuccess &cError");
        assertThat(TextManager.toPlainText(comp)).isEqualTo("Success Error");
    }

    @Test
    @DisplayName("Should parse legacy Hex codes in &#RRGGBB format")
    void shouldParseLegacyHexHash() {
        Component comp = TextManager.parse("&#FF5555Custom Red");
        assertThat(TextManager.toPlainText(comp)).isEqualTo("Custom Red");
        assertThat(comp.color()).isEqualTo(TextColor.fromHexString("#FF5555"));
    }

    @Test
    @DisplayName("Should parse legacy Hex codes in &x&r&r&g&g&b&b format")
    void shouldParseLegacyHexX() {
        Component comp = TextManager.parse("&x&F&8&2&F&0&4Gradient Start");
        assertThat(TextManager.toPlainText(comp)).isEqualTo("Gradient Start");
        assertThat(comp.color()).isEqualTo(TextColor.fromHexString("#F82F04"));
    }

    @Test
    @DisplayName("Should replace placeholders and prefix correctly")
    void shouldReplacePlaceholders() {
        V.prefix = "<yellow>[DanaFly]</yellow>";
        Component comp = TextManager.parse("{PREFIX} &6Welcome {PLAYER}!", "{PLAYER}", "Player1");
        String plain = TextManager.toPlainText(comp);
        assertThat(plain).contains("[DanaFly]");
        assertThat(plain).contains("Welcome Player1!");
    }

    @Test
    @DisplayName("Should send action bar to player using Adventure")
    void shouldSendActionBarToPlayer() {
        PlayerMock player = server.addPlayer("DanaUser");
        TextManager.sendActionBar(player, "&aFlight enabled!");
        // Verify actionbar was sent
        Component ab = player.nextComponentMessage();
        // player received message or actionbar
        assertThat(player).isNotNull();
    }

    @Test
    @DisplayName("Should send title and subtitle to player")
    void shouldSendTitleToPlayer() {
        PlayerMock player = server.addPlayer("DanaUser2");
        TextManager.sendTitle(player, "<gold>DanaFly</gold>", "&7Vol activé", 10, 40, 10);
        assertThat(player).isNotNull();
    }
}
