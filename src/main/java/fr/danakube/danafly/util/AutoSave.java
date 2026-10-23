package fr.danakube.danafly.util;

import org.bukkit.scheduler.BukkitRunnable;

import fr.danakube.danafly.util.data.DataBridge;

public class AutoSave extends BukkitRunnable {

	private DataBridge bridge;
	
	public AutoSave(DataBridge bridge) {
		this.bridge = bridge;
	}
	
	@Override
	public void run() {
		bridge.commitAll();
	}

}
