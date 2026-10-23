package fr.danakube.danafly.aesthetic.actionbar;

import fr.danakube.danafly.DanaFly;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class ActionBar {
  protected final DanaFly tempfly;
  
  public ActionBar(DanaFly tempfly) {
    this.tempfly = tempfly;
  }
  
  public abstract void sendActionBar(final Player player, final String message);

  public void sendActionBar(final Player player, final String message, int duration) {
    sendActionBar(player, message);

    if (duration >= 0) {
      new BukkitRunnable() {
        @Override
        public void run() {
          sendActionBar(player, "");
        }
      }.runTaskLater(tempfly, duration + 1);
    }

    while (duration > 40) {
      duration -= 40;
      new BukkitRunnable() {
        @Override
        public void run() {
          sendActionBar(player, message);
        }
      }.runTaskLater(tempfly, (long) duration);
    }
  }
}
