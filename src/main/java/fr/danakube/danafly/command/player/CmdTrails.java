package fr.danakube.danafly.command.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.command.TempFlyCommand;
import fr.danakube.danafly.gui.pages.PageTrails;
import fr.danakube.danafly.util.U;
import fr.danakube.danafly.util.V;

public class CmdTrails extends TempFlyCommand {

	public CmdTrails(DanaFly tempfly, String[] args) {
		super(tempfly, args);
	}
	
	@Override
	public void executeAs(CommandSender s) {
		if (!U.isPlayer(s)) {
			U.m(s, V.invalidSender);
			return;
		}
		if (!U.hasPermission(s, "tempfly.trails")) {
			U.m(s, V.invalidPermission);
			return;
		}
		new PageTrails(tempfly.getGuiManager().createSession((Player)s), 0, true);
	}

	@Override
	public List<String> getPotentialArguments(CommandSender s) {
		return new ArrayList<>();
	}
}
