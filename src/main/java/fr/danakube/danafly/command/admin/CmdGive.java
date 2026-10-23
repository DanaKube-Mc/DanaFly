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
import fr.danakube.danafly.util.Console;
import fr.danakube.danafly.util.U;
import fr.danakube.danafly.util.V;

public class CmdGive extends TimeCommand {
	
	public CmdGive(DanaFly tempfly, String[] args) {
		super(tempfly, args);
	}
	
	@Override
	public void executeAs(CommandSender s) {
		if (!U.hasPermission(s, "tempfly.give")) {
			U.m(s, V.invalidPermission);
			return;
		}

		boolean force = hasForceFlag(args);
		String[] cleanArgsArr = stripForceFlag(args);

		if (cleanArgsArr.length < 3) {
			U.m(s, U.cc("&c/df give [player] [amount] [-force|-f]"));
			return;
		}
		
		@SuppressWarnings("deprecation")
		OfflinePlayer p = Bukkit.getOfflinePlayer(cleanArgsArr[1]);
		if (p == null || (p != null && !p.isOnline() && !p.hasPlayedBefore())) {
			U.m(s, V.invalidPlayer.replaceAll("\\{PLAYER}", cleanArgsArr[1]));
			return;
		}
		double amount = quantifyArguments(s, cleanArgsArr, 2);
		if (amount <= 0) {
			return;
		}
		AsyncTimeParameters params = new AsyncTimeParameters(tempfly, this, s, p, amount);
		params.setForce(force);
		params.runAsync();
	}

	@Override
	public void execute(AsyncTimeParameters parameters) {
		CommandSender s = parameters.getSender();
		double maxTime = parameters.getMaxTime();
		if (maxTime == -999) {
			U.m(s, s.isOp() ? V.vaultPermsRequired : V.invalidPlayer);
			return;
		}
		Console.debug(maxTime);
		
		TimeManager manager = tempfly.getTimeManager();
		OfflinePlayer p = parameters.getTarget();
		double currentTime = parameters.getCurrentTime();
		double amount = parameters.getAmount();

		if (!parameters.isForce() && maxTime > -1) {
			if (currentTime >= maxTime) {
				U.m(s, manager.regexString(V.maxPartialGiveAdmin != null ? V.maxPartialGiveAdmin : V.timeMaxOther, maxTime)
						.replaceAll("\\{PLAYER}", p.getName() != null ? p.getName() : "")
						.replaceAll("\\{MAX_TIME}", manager.regexString("{FORMATTED_TIME}", maxTime))
						.replaceAll("\\{ACCEPTED_TIME}", manager.regexString("{FORMATTED_TIME}", 0))
						.replaceAll("\\{REQUESTED_TIME}", manager.regexString("{FORMATTED_TIME}", amount)));
				return;
			}
			if (currentTime + amount > maxTime) {
				double accepted = maxTime - currentTime;
				double requested = amount;
				parameters.setAmount(accepted);
				amount = accepted;

				U.m(s, manager.regexString(V.maxPartialGiveAdmin != null ? V.maxPartialGiveAdmin : V.timeMaxOther, maxTime)
						.replaceAll("\\{PLAYER}", p.getName() != null ? p.getName() : "")
						.replaceAll("\\{MAX_TIME}", manager.regexString("{FORMATTED_TIME}", maxTime))
						.replaceAll("\\{ACCEPTED_TIME}", manager.regexString("{FORMATTED_TIME}", accepted))
						.replaceAll("\\{REQUESTED_TIME}", manager.regexString("{FORMATTED_TIME}", requested)));
				if (p.isOnline()) {
					U.m((Player)p, manager.regexString(V.maxReachedSelf, maxTime)
							.replaceAll("\\{MAX_TIME}", manager.regexString("{FORMATTED_TIME}", maxTime)));
				}
			}
		}

		manager.addTime(parameters.getTarget().getUniqueId(), parameters);
		if (p != s) {
			U.m(s, manager.regexString(V.timeGivenOther, amount)
					.replaceAll("\\{PLAYER}", p.getName() != null ? p.getName() : ""));
		}
		if (p.isOnline()) {
			U.m((Player)p, manager.regexString(V.timeGivenSelf, amount));	
		}
	}
	
	@Override
	public List<String> getPotentialArguments(CommandSender s) {
		if (!U.hasPermission(s, "tempfly.give")) {
			return new ArrayList<>();
		}
		if (args.length < 3) {
			return getPlayerArguments(args[1]);
		} else {
			return getTimeArguments(cleanArgs(args, 2));
		}
	}
}
