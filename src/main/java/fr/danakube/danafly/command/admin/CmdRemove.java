package fr.danakube.danafly.command.admin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.command.TimeCommand;
import fr.danakube.danafly.time.AsyncTimeParameters;
import fr.danakube.danafly.time.TimeManager;
import fr.danakube.danafly.util.U;
import fr.danakube.danafly.util.V;

public class CmdRemove extends TimeCommand {

	public CmdRemove(DanaFly tempfly, String[] args) {
		super(tempfly, args);
	}
	
	@Override @SuppressWarnings("deprecation")
	public void executeAs(CommandSender s) {
		if (!U.hasPermission(s, "tempfly.remove")) {
			U.m(s, V.invalidPermission);
			return;
		}
		if (args.length < 3) {
			U.m(s, U.cc("&c/tf remove [player] [amount-> {args=[-s][-m][-h][-d]}]"));
			return;
		}
		
		OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
		if (p == null || (p != null && !p.isOnline() && !p.hasPlayedBefore())) {
			U.m(s, V.invalidPlayer.replaceAll("\\{PLAYER}", args[1]));
			return;
		}
		double amount = quantifyArguments(s, 2);
		if (amount <= 0) {
			U.m(s, V.invalidNumber.replaceAll("\\{NUMBER}", String.valueOf(amount)));
			return;
		}
		new AsyncTimeParameters(tempfly, this, s, p, amount).runAsync();
	}
	
	@Override
	public void execute(AsyncTimeParameters parameters) {
		TimeManager manager = tempfly.getTimeManager();
		CommandSender s = parameters.getSender();
		double amount = parameters.getAmount();
		double time = parameters.getCurrentTime();
		OfflinePlayer p = parameters.getTarget();
		if (time == 0) {
			U.m(s, V.invalidTimeOther
					.replaceAll("\\{PLAYER}", p.getName()));
			return;
		}
		double remove = time-amount < 0 ? time : amount;
		manager.removeTime(parameters.getTarget().getUniqueId(), parameters);
		if (p != s) {
			U.m(s, manager.regexString(V.timeRemovedOther, remove)
					.replaceAll("\\{PLAYER}", p.getName()));	
		}
		if (p.isOnline() && time > 0) {
			U.m((Player)p, manager.regexString(V.timeRemovedSelf, remove));
		}
	}

	@Override
	public List<String> getPotentialArguments(CommandSender s) {
		if (!U.hasPermission(s, "tempfly.remove")) {
			return new ArrayList<>();
		}
		if (args.length < 3) {
			return getPlayerArguments(args[1]);
		} else {
			return getTimeArguments(cleanArgs(args, 2));
		}
	}
}
