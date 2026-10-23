package fr.danakube.danafly.hook.skyblock;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.command.TempFlyCommand;
import fr.danakube.danafly.util.U;
import fr.danakube.danafly.util.V;

public class CmdIslandSettings extends TempFlyCommand {

	public CmdIslandSettings(DanaFly tempfly, String[] args) {
		super(tempfly, args);
	}
	
	public void executeAs(CommandSender s) {
		if (!U.isPlayer(s)) {
			U.m(s, V.invalidSender);
			return;
		}
		Player p = (Player) s;
		if (!p.hasPermission("tempfly.skyblock.island.settings")) {
			U.m(s, V.invalidPermission);
			return;
		}
		new PageIslandSettings(tempfly.getGuiManager().createSession(p));
	}

	@Override
	public List<String> getPotentialArguments(CommandSender s) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
