package fr.danakube.danafly.aesthetic;

import org.bukkit.entity.Player;
import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.text.TextManager;

public class ActionBarAPI {

    public static void initialize(DanaFly plugin) {
        // Modern Paper Adventure handles ActionBars natively without NMS
    }

    public static void sendActionBar(final Player player, final String message) {
        TextManager.sendActionBar(player, message);
    }

    public static void sendActionBar(final Player player, final String message, int duration) {
        TextManager.sendActionBar(player, message);
    }
}
