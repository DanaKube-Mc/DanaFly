package fr.danakube.danafly.util;

import org.bukkit.scheduler.BukkitRunnable;

import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.user.FlightUser;

public class ParticleTask extends BukkitRunnable {

	private DanaFly tempfly;
	
	public ParticleTask(DanaFly tempfly) {
		this.tempfly = tempfly;
	}
	
	@Override
	public void run() {
		for (FlightUser user: tempfly.getFlightManager().getUsers()) {
			if (user.hasFlightEnabled() && user.getPlayer().isFlying()) {
				user.playTrail();
			}
		}
	}

}
