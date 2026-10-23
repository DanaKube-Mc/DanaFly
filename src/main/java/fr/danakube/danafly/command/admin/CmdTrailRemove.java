package fr.danakube.danafly.command.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.aesthetic.particle.Particles;
import fr.danakube.danafly.command.TempFlyCommand;
import fr.danakube.danafly.util.U;
import fr.danakube.danafly.util.V;

public class CmdTrailRemove extends TempFlyCommand {

	public CmdTrailRemove(DanaFly tempfly, String[] args) {
		super(tempfly, args);
	}
	
	// Invoked from command
	@Override
	public void executeAs(CommandSender s) {
		Player target = null;
		if (args.length == 2
				|| ((args.length > 2 && (target = Bukkit.getPlayerExact(args[3])) != null && target == s))) {
			if (!(s instanceof Player)) {
				U.m(s, V.invalidSender);
				return;
			}
			target = (Player) s;
			if (!target.hasPermission("tempfly.trails.remove.self")) {
				U.m(target, V.invalidPermission);
				return;
			}
			removeTrail(target);
			return;
		}
		if (!U.hasPermission(s, "tempfly.trails.remove.other")) {
			U.m(s, V.invalidPermission);
			return;
		}
		if (args.length < 3) {
			U.m(s, U.cc("&c/tf trail remove [player]"));
			return;
		}
		
		if (target == null) {
			U.m(s, V.invalidPlayer.replaceAll("\\{PLAYER}", args[3]));
			return;
		}
		removeTrail(target);
		if (s != target) {
			U.m(s, V.trailRemovedOther.replaceAll("\\{PLAYER}", target.getName()));
		}
	}
	
	// Invoked from the GUI because it's easier this way
	public void executeFromGui(Player target) {
		removeTrail(target);
	}

	private void removeTrail(Player target) {
		Particles.setTrail(target.getUniqueId(), "");
		U.m(target, V.trailRemovedSelf);
	}

	@Override
	public List<String> getPotentialArguments(CommandSender s) {
		if (args.length < 3 && U.hasPermission(s, "tempfly.trails.remove.other")) {
			return getPlayerArguments(args[1]);
		} else if (args.length < 3 && U.hasPermission(s, "tempfly.trails.remove.self")) {
			return Arrays.asList(((Player)s).getName());
		}
		return new ArrayList<>();
	}
	
}
