package fr.danakube.danafly.command.player;

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

public class CmdPay extends TimeCommand {

	public CmdPay(DanaFly tempfly, String[] args) {
		super(tempfly, args);
	}
	
	@Override @SuppressWarnings("deprecation")
	public void executeAs(CommandSender s) {
		if (!V.payable) {
			U.m(s, V.invalidCommand);
			return;
		}
		if (!U.isPlayer(s)) {
			U.m(s, V.invalidSender);
			return;
		}
		if (!U.hasPermission(s, "tempfly.pay")) {
			U.m(s, V.invalidPermission);
			return;
		}
		if (args.length < 3) {
			U.m(s, U.cc("&c/tf pay [player] [amount-> {args=[-s][-m][-h][-d]}]"));
			return;
		}
		OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
		if (p == null || (p != null && !p.isOnline() && !p.hasPlayedBefore())) {
			U.m(s, V.invalidPlayer.replaceAll("\\{PLAYER}", args[1]));
			return;
		}
		
		if ((Player)s == p) {
			U.m(s, V.invalidReciever);
			return;
		}
		double amount = 0;
		amount = quantifyArguments(s, 2);
		if (amount <= 0) {
			U.m(s, V.invalidNumber.replaceAll("\\{NUMBER}", String.valueOf(amount)));
			return;
		}
		amount = Math.floor(amount);
		TimeManager manager = tempfly.getTimeManager();
		Player sender = (Player)s;
		//This doens't need to be async as the player needs to be online to pay, so their data is already loaded.
		double bal = manager.getTime(sender.getUniqueId());
		if (bal < amount) {
			U.m(s, V.invalidTimeSelf);
			return;
		}
		new AsyncTimeParameters(tempfly, this, s, p, amount).runAsync();
	}
	
	@Override
	public void execute(AsyncTimeParameters parameters) {
		CommandSender s = parameters.getSender();
		double maxTime = parameters.getMaxTime();
		if (maxTime == -999) {
			U.m(s, s.isOp() ? V.vaultPermsRequired : V.invalidPlayer);
			return;
		}
		
		TimeManager manager = tempfly.getTimeManager();
		OfflinePlayer p = parameters.getTarget();
		double targetCurrentTime = manager.getTime(p.getUniqueId());
		double requestedAmount = parameters.getAmount();

		if (maxTime > -1 && targetCurrentTime >= maxTime) {
			String maxFormatted = fr.danakube.danafly.time.TimeParser.formatDuration(maxTime);
			U.m(s, V.maxReachedOther.replace("{PLAYER}", p.getName() != null ? p.getName() : "Inconnu")
					.replace("{MAX_TIME}", maxFormatted));
			U.m(p, V.maxReachedSelf.replace("{MAX_TIME}", maxFormatted));
			return;
		}

		double acceptedAmount = requestedAmount;
		double refundAmount = 0.0;
		boolean isPartial = false;

		if (maxTime > -1 && (targetCurrentTime + requestedAmount > maxTime)) {
			acceptedAmount = maxTime - targetCurrentTime;
			refundAmount = requestedAmount - acceptedAmount;
			isPartial = true;
		}

		// Set accepted amount for debit and credit
		parameters.setAmount(acceptedAmount);
		manager.removeTime(((Player) s).getUniqueId(), parameters);
		manager.addTime(p.getUniqueId(), parameters);

		if (isPartial) {
			String maxFormatted = fr.danakube.danafly.time.TimeParser.formatDuration(maxTime);
			String acceptedFormatted = fr.danakube.danafly.time.TimeParser.formatDuration(acceptedAmount);
			String refundFormatted = fr.danakube.danafly.time.TimeParser.formatDuration(refundAmount);

			U.m(s, V.maxPartialPaySender.replace("{PLAYER}", p.getName() != null ? p.getName() : "Inconnu")
					.replace("{ACCEPTED_TIME}", acceptedFormatted)
					.replace("{REFUND_TIME}", refundFormatted)
					.replace("{MAX_TIME}", maxFormatted));

			if (p.isOnline()) {
				U.m((Player) p, V.maxPartialPayReceiver.replace("{PLAYER}", s.getName())
						.replace("{ACCEPTED_TIME}", acceptedFormatted)
						.replace("{MAX_TIME}", maxFormatted));
			}
		} else {
			U.m(s, manager.regexString(V.timeSentOther, acceptedAmount)
					.replaceAll("\\{PLAYER}", p.getName() != null ? p.getName() : "Inconnu"));
			if (p.isOnline()) {
				U.m((Player) p, manager.regexString(V.timeSentSelf, acceptedAmount)
						.replaceAll("\\{PLAYER}", s.getName()));
			}
		}
	}

	@Override
	public List<String> getPotentialArguments(CommandSender s) {
		if (!U.hasPermission(s, "tempfly.pay") || !V.payable) {
			return new ArrayList<>();
		}
		if (args.length < 3) {
			return getPlayerArguments(args[1]);
		} else {
			return getTimeArguments(cleanArgs(args, 2));
		}
	}


}
