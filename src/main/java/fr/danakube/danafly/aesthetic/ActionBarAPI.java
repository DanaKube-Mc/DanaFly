package fr.danakube.danafly.aesthetic;

import fr.danakube.danafly.aesthetic.actionbar.ActionBar;
import fr.danakube.danafly.aesthetic.actionbar.LegacyActionBar;
import fr.danakube.danafly.aesthetic.actionbar.ModernActionBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.danakube.danafly.DanaFly;

public class ActionBarAPI {
	
	private static ActionBar actionBar;
    
    public static void initialize(DanaFly tempfly) {
        // Check if we're on 1.12 or above
    	  if (Bukkit.getServer().getVersion().matches(".*1\\.(?!10|11)\\d{2,}.*")) {
    	      actionBar = new ModernActionBar(tempfly);
        } else {
    	      actionBar = new LegacyActionBar(tempfly);
    	}
    }
    
    public static void sendActionBar(final Player player, final String message) {
    	actionBar.sendActionBar(player, message);
    }
    
    public static void sendActionBar(final Player player, final String message, int duration) {
    	actionBar.sendActionBar(player, message, duration);
    }
}
