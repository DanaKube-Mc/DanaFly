package fr.danakube.danafly;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import fr.danakube.danafly.aesthetic.ActionBarAPI;
import fr.danakube.danafly.aesthetic.TitleAPI;
import fr.danakube.danafly.hook.PlaceholderAPIHook;
import fr.danakube.danafly.aesthetic.particle.Particles;
import fr.danakube.danafly.command.CommandManager;
import fr.danakube.danafly.fly.FlightManager;
import fr.danakube.danafly.gui.GuiManager;
import fr.danakube.danafly.gui.pages.PageShop;
import fr.danakube.danafly.gui.pages.PageTrails;
import fr.danakube.danafly.hook.HookManager;
import fr.danakube.danafly.hook.TempFlyHook;
import fr.danakube.danafly.time.TimeManager;
import fr.danakube.danafly.util.AutoSave;
import fr.danakube.danafly.util.Console;
import fr.danakube.danafly.util.ParticleTask;
import fr.danakube.danafly.util.V;
import fr.danakube.danafly.util.data.DataBridge;
import fr.danakube.danafly.util.data.Files;

public class DanaFly extends JavaPlugin {
	
	// static abusers unite
	private static DanaFlyAPI tfApi;
	public static DanaFlyAPI getAPI() {
		return tfApi;
	}

	
	private HookManager hooks;
	private DataBridge bridge;
	private FlightManager flight;
	private TimeManager time;
	private CommandManager commands;
	private GuiManager gui;
	private BukkitTask autosave;
	
	public HookManager getHookManager() {
		return hooks;
	}
	
	public DataBridge getDataBridge() {
		return bridge;
	}
	
	public FlightManager getFlightManager() {
		return flight;
	}
	
	public TimeManager getTimeManager() {
		return time;
	}
	
	public CommandManager getCommandManager() {
		return commands;
	}

	public GuiManager getGuiManager() {
		return gui;
	}
	
	@Override
	public void onEnable() {
		Console.setLogger(this.getLogger());
		
		Files.createFiles(this);
		V.loadValues();
		
		try {
			this.bridge   = new DataBridge(this);
		} catch (IOException | SQLException e1) {
			e1.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		tfApi = new DanaFlyAPI(this);
		this.flight   = new FlightManager(this);
		this.time     = new TimeManager(this);
		this.hooks    = new HookManager(this);
		this.commands = new CommandManager(this);
		this.gui      = new GuiManager(this);
		
		hooks.loadInternalGenres();
		initializeGui();
		initializeAesthetics();

		
		try {
			Metrics metrics = new Metrics(this, 8196);
			// Hooks
	        metrics.addCustomChart(new Metrics.DrilldownPie("gamemode_hooks", () -> {
	            Map<String, Map<String, Integer>> map = new HashMap<>();
	            Map<String, Integer> entry = new HashMap<>();
	            
	            for (TempFlyHook hook: hooks.getEnabled()) {
	            	entry.put(hook.getHookedPlugin(), 1);
	            	map.put(hook.getHookedPlugin(), entry);
	            }
	            if (map.size() == 0) {
	            	entry.put("No Hooks", 1);
	            	map.put("No Hooks", entry);
	            }
	            return map;
	        }));
		} catch (Exception e) {e.printStackTrace();}
		
		autosave = new AutoSave(bridge).runTaskTimerAsynchronously(this, V.save * 20 * 60, V.save * 20 * 60);
		
		// Support "/reload"
		for (Player p: Bukkit.getOnlinePlayers()) {
			flight.addUser(p);
		}
	}
	
	private void initializeAesthetics() {
		Particles.initialize(this);
		
		if (V.particles) {
			new ParticleTask(this).runTaskTimer(this, 0, 5);
		}
		if (V.actionBar) {
			ActionBarAPI.initialize(this);
		}
		
		TitleAPI.initialize(this);
		
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			Console.info("Initializing PlaceholderAPI Hook (danafly & tempfly)");
			PlaceholderAPIHook.initialize(this);
		}
	}
	
	private void initializeGui() {
		PageTrails.initialize(this);
		PageShop.initialize(this);
	}
	
	@Override
	public void onDisable() {
		flight.onDisable();
		gui.endAllSessions();
		bridge.commitAll();
	}
	
	/*
	 * Reload the plugin, this is the method called upon command /tempfly reload
	 */
	//TODO reload hooks
	public void reloadTempfly() {
		gui.endAllSessions();
		
		bridge.commitAll();
		Files.createFiles(this);
		V.loadValues();
		initializeGui();
		
		flight.onTempflyReload();
		hooks.onTempflyReload();
		
		if (autosave != null) {
			autosave.cancel();
			autosave = new AutoSave(bridge).runTaskTimerAsynchronously(this, 0, V.save * 20 * 60);
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("fly")) {
			return commands.getTabCompleter().onTabComplete(s, cmd, label, args);
		} else {
			return Arrays.asList(args);
		}
	}
	
}
