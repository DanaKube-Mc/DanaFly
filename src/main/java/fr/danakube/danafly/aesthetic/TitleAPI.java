package fr.danakube.danafly.aesthetic;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.aesthetic.title.LegacyTitle;
import fr.danakube.danafly.aesthetic.title.ModernTitle;
import fr.danakube.danafly.aesthetic.title.Title;

public class TitleAPI {

	private static Title title;
	
    public static void initialize(DanaFly tempfly) {
    	  if (Bukkit.getServer().getVersion().matches(".*1\\.(?!10|11)\\d{2,}.*")) {
    	      title = new ModernTitle();
        } else {
    	      title = new LegacyTitle();
    	}
    }
    
    public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
    	TitleAPI.title.sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
    }

    public static void clearTitle(Player player) {
    	TitleAPI.title.clearTitle(player);
    }
}
