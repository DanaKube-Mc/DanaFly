package fr.danakube.danafly.time;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import fr.danakube.danafly.DanaFly;

public class AsyncTimeParameters implements Runnable {

	private DanaFly tempfly;
	private CommandSender s;
	private OfflinePlayer p;
	private double amount;
	private AsyncTimeExecutor executor;
	
	public AsyncTimeParameters(DanaFly tempfly, AsyncTimeExecutor executor, CommandSender s, OfflinePlayer p, double amount) {
		this.tempfly = tempfly;
		this.executor = executor;
		this.s = s;
		this.p = p;
		this.amount = amount;
	}
	
	public AsyncTimeParameters(DanaFly tempfly, AsyncTimeExecutor executor, OfflinePlayer p, double amount) {
		this.tempfly = tempfly;
		this.executor = executor;
		this.p = p;
		this.amount = amount;
	}
	
	private double maxTime;
	private double currentTime;
	private boolean force;
	
	
	public void runAsync() {
		Bukkit.getScheduler().runTaskAsynchronously(tempfly, this);
	}
	
	@Override
	public void run() {
		if (Bukkit.isPrimaryThread()) {
			this.runAsync();
			return;
		}
		maxTime = tempfly.getTimeManager().getMaxTime(p.getUniqueId());
		currentTime = tempfly.getTimeManager().getTime(p.getUniqueId());
		
		Bukkit.getScheduler().runTask(tempfly, () -> {
			executor.execute(this);
		});
	}
	
	public DanaFly getTempfly() {
		return tempfly;
	}

	public double getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(double maxTime) {
		this.maxTime = maxTime;
	}

	public double getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(double currentTime) {
		this.currentTime = currentTime;
	}
	
	public CommandSender getSender() {
		return s;
	}
	
	public OfflinePlayer getTarget() {
		return p;
	}
	
	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public boolean isForce() {
		return force;
	}

	public void setForce(boolean force) {
		this.force = force;
	}
} 
