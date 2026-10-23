package fr.danakube.danafly.command.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.command.TempFlyCommand;
import fr.danakube.danafly.util.U;
import fr.danakube.danafly.util.V;

public class CmdHelp extends TempFlyCommand {

	public CmdHelp(DanaFly tempfly, String[] args) {
		super(tempfly, args);
	}
	
	@Override
	public void executeAs(CommandSender s) {
		if (!U.hasPermission(s, "tempfly.help")) {
			U.m(s, V.invalidPermission);
			return;
		}
		for (String line: V.help) {
			U.m(s, line);
		}
		if (U.hasPermission(s, "tempfly.help.admin")) {
			for (String line: V.helpExtended) {
				U.m(s, line);
			}
		}
	}

	@Override
	public List<String> getPotentialArguments(CommandSender s) {
		return new ArrayList<>();
	}

}
