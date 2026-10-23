package fr.danakube.danafly.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.danakube.danafly.DanaFly;

public abstract class TempFlyCommand {

	protected final DanaFly tempfly;
	protected final String[] args;
	
	public TempFlyCommand(DanaFly tempfly, String[] args) {
		this.tempfly = tempfly;
		this.args = args;
	}
	
	public DanaFly getTempFly() {
		return this.tempfly;
	}
	
	public String[] getArguments() {
		return args;
	}
	
	public String[] cleanArgs(String[] arguments, int skip) {
		List<String> temp = new ArrayList<>();
		for (int i = 0; i < arguments.length; i++) {
			if (i <= skip-1) {
				continue;
			}
			temp.add(arguments[i]);
		}
		return temp.toArray(new String[temp.size()]);
	}
	
	public List<String> getPlayerArguments(String partial) {
		List<String> matches = new ArrayList<>();
		for (Player p: Bukkit.getOnlinePlayers()) {
			char[] baseChars = p.getName().toCharArray();
			if (partial.length() > baseChars.length) {
				continue;
			}
			for (int i = 0; i < partial.length(); i++) {
				char partialChar = partial.charAt(i);
				if (partialChar != baseChars[i]) {
					continue;
				}
			}
			matches.add(p.getName());
		}
		return matches;
	}
	
	public List<String> getRange(int min, int max) {
		List<String> numbers = new ArrayList<>();
		for (int i = min; i <= max; i++) {
			numbers.add(String.valueOf(i));
		}
		return numbers;
	}
	
	public abstract List<String> getPotentialArguments(CommandSender s);
	
	public abstract void executeAs(CommandSender s);

}
