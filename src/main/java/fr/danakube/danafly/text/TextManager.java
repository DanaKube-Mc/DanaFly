package fr.danakube.danafly.text;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.danakube.danafly.util.V;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;

public class TextManager {

    private static final Pattern HEX_HASH_PATTERN = Pattern.compile("[&§]#([A-Fa-f0-9]{6})");
    private static final Pattern HEX_X_PATTERN = Pattern.compile("[&§]x([&§][A-Fa-f0-9]){6}");
    private static final Pattern LEGACY_CODE_PATTERN = Pattern.compile("[&§]([0-9a-fk-orA-FK-OR])");

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /**
     * Converts legacy formatting codes (& / § and Hex) to MiniMessage tags.
     */
    public static String legacyToMiniMessage(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        // 1. Convert &#RRGGBB format
        Matcher hashMatcher = HEX_HASH_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (hashMatcher.find()) {
            hashMatcher.appendReplacement(sb, "<#" + hashMatcher.group(1) + ">");
        }
        hashMatcher.appendTail(sb);
        text = sb.toString();

        // 2. Convert &x&r&r&g&g&b&b format
        Matcher xMatcher = HEX_X_PATTERN.matcher(text);
        sb = new StringBuilder();
        while (xMatcher.find()) {
            String hexGroup = xMatcher.group().replaceAll("[&§xX]", "");
            xMatcher.appendReplacement(sb, "<#" + hexGroup + ">");
        }
        xMatcher.appendTail(sb);
        text = sb.toString();

        // 3. Convert standard &0-&f, &k-&r
        Matcher codeMatcher = LEGACY_CODE_PATTERN.matcher(text);
        sb = new StringBuilder();
        while (codeMatcher.find()) {
            char code = Character.toLowerCase(codeMatcher.group(1).charAt(0));
            String replacement;
            switch (code) {
                case '0': replacement = "<black>"; break;
                case '1': replacement = "<dark_blue>"; break;
                case '2': replacement = "<dark_green>"; break;
                case '3': replacement = "<dark_aqua>"; break;
                case '4': replacement = "<dark_red>"; break;
                case '5': replacement = "<dark_purple>"; break;
                case '6': replacement = "<gold>"; break;
                case '7': replacement = "<gray>"; break;
                case '8': replacement = "<dark_gray>"; break;
                case '9': replacement = "<blue>"; break;
                case 'a': replacement = "<green>"; break;
                case 'b': replacement = "<aqua>"; break;
                case 'c': replacement = "<red>"; break;
                case 'd': replacement = "<light_purple>"; break;
                case 'e': replacement = "<yellow>"; break;
                case 'f': replacement = "<white>"; break;
                case 'k': replacement = "<obfuscated>"; break;
                case 'l': replacement = "<bold>"; break;
                case 'm': replacement = "<strikethrough>"; break;
                case 'n': replacement = "<underlined>"; break;
                case 'o': replacement = "<italic>"; break;
                case 'r': replacement = "<reset>"; break;
                default: replacement = ""; break;
            }
            codeMatcher.appendReplacement(sb, replacement);
        }
        codeMatcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Parses a message string into an Adventure Component with MiniMessage and legacy fallback.
     */
    public static Component parse(String input, String... replacements) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }

        String formatted = input;
        if (V.prefix != null && !V.prefix.isEmpty() && formatted.contains("{PREFIX}")) {
            formatted = formatted.replace("{PREFIX}", V.prefix);
        }

        if (replacements != null && replacements.length >= 2) {
            for (int i = 0; i < replacements.length - 1; i += 2) {
                String target = replacements[i];
                String repl = replacements[i + 1];
                if (target != null && repl != null) {
                    formatted = formatted.replace(target, repl);
                }
            }
        }

        String miniMessageFormatted = legacyToMiniMessage(formatted);
        return MINI_MESSAGE.deserialize(miniMessageFormatted);
    }

    /**
     * Sends a parsed Component to a CommandSender.
     */
    public static void send(CommandSender sender, String message, String... replacements) {
        if (sender == null || message == null || message.isEmpty()) {
            return;
        }
        sender.sendMessage(parse(message, replacements));
    }

    /**
     * Sends an action bar message to a player.
     */
    public static void sendActionBar(Player player, String message, String... replacements) {
        if (player == null || message == null || message.isEmpty()) {
            return;
        }
        player.sendActionBar(parse(message, replacements));
    }

    /**
     * Shows a title and subtitle to a player.
     */
    public static void sendTitle(Player player, String titleText, String subtitleText, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        if (player == null) {
            return;
        }
        Component titleComp = titleText != null ? parse(titleText) : Component.empty();
        Component subtitleComp = subtitleText != null ? parse(subtitleText) : Component.empty();

        Title.Times times = Title.Times.times(
                Duration.ofMillis(fadeInTicks * 50L),
                Duration.ofMillis(stayTicks * 50L),
                Duration.ofMillis(fadeOutTicks * 50L)
        );

        player.showTitle(Title.title(titleComp, subtitleComp, times));
    }

    /**
     * Converts a Component to plain text.
     */
    public static String toPlainText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
}
