package fr.danakube.danafly.command.admin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.command.TempFlyCommand;
import fr.danakube.danafly.util.U;
import fr.danakube.danafly.util.V;

public class CmdReload extends TempFlyCommand {

	public CmdReload(DanaFly tempfly, String[] args) {
		super(tempfly, args);
	}
	
	@Override
	public void executeAs(CommandSender s) {
		if (!U.hasPermission(s, "tempfly.reload")) {
			U.m(s, V.invalidPermission);
			return;
		}
		tempfly.reloadTempfly();
		U.m(s, V.reload);
	}
	
	@Override
	public List<String> getPotentialArguments(CommandSender s) {
		return new ArrayList<>();
	}
}
