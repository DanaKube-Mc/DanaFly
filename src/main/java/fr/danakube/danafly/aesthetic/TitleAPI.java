package fr.danakube.danafly.aesthetic;

import org.bukkit.entity.Player;
import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.text.TextManager;

public class TitleAPI {

    public static void initialize(DanaFly plugin) {
        // Modern Paper Adventure handles Titles natively without NMS
    }

    public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        int in = fadeIn != null ? fadeIn : 10;
        int s = stay != null ? stay : 40;
        int out = fadeOut != null ? fadeOut : 10;
        TextManager.sendTitle(player, title, subtitle, in, s, out);
    }

    public static void clearTitle(Player player) {
        if (player != null) {
            player.clearTitle();
        }
    }
}
