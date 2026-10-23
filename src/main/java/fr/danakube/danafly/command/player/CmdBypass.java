package fr.danakube.danafly.command.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.command.TempFlyCommand;
import fr.danakube.danafly.user.FlightUser;
import fr.danakube.danafly.util.U;
import fr.danakube.danafly.util.V;

public class CmdBypass extends TempFlyCommand {

	public CmdBypass(DanaFly tempfly, String[] args) {
		super(tempfly, args);
	}
	
	@Override
	public void executeAs(CommandSender s) {
		if (!U.hasPermission(s, "tempfly.bypass.toggle")) {
			U.m(s, V.invalidPermission);
			return;
		}
		if (!U.isPlayer(s)) {
			U.m(s, V.invalidSender);
		}
		FlightUser user = tempfly.getFlightManager().getUser((Player)s);
		boolean toggleVal = false;
		if (args.length > 1) {
			switch (args[1].toLowerCase()) {
			case "on": case "enable":
				toggleVal = true;
			case "off": case "disable":
				break;
			default:
				U.m(s, "&c/tempfly bypass [on/off]");
				return;
			}
		} else {
			toggleVal = !user.hasRequirementBypass();
		}
		U.m(s, toggleVal ? V.flyBypassEnabled : V.flyBypassDisabled);
		user.setRequirementBypass(toggleVal);
	}

	@Override
	public List<String> getPotentialArguments(CommandSender s) {
		if (args.length < 3 && U.hasPermission(s, "tempfly.bypass.toggle")) {
			return tempfly.getCommandManager().getToggleCompletions(true);
		}
		return new ArrayList<>();
	}
}
