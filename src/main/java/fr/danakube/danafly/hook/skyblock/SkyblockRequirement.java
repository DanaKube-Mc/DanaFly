package fr.danakube.danafly.hook.skyblock;

import javax.annotation.Nullable;

import fr.danakube.danafly.hook.skyblock.SkyblockHook.SkyblockRequirementType;
import fr.danakube.danafly.util.Console;
import fr.danakube.danafly.util.U;
import fr.danakube.danafly.util.V;

public class SkyblockRequirement {

	private SkyblockRequirementType type;
	
	private double playerLevel;
	private double islandLevel;
	@Nullable
	private SkyblockChallenge[] playerChallenges;
	private SkyblockChallenge[] islandChallenges;
	@Nullable
	private String name;
	
	public SkyblockRequirement(
			SkyblockChallenge[] playerChallenges, SkyblockChallenge[] islandChallenges,
			double playerLevel, double islandLevel,
			String name, SkyblockRequirementType type) {
		this.type = type;
		this.name = name;
		
		this.playerChallenges = playerChallenges == null ? new SkyblockChallenge[0] : playerChallenges;
		this.islandChallenges = islandChallenges == null ? new SkyblockChallenge[0] : islandChallenges;
		
		this.playerLevel = playerLevel;
		this.islandLevel = islandLevel;
		
		if (V.debug) {
			Console.debug("---< Name: " + name,
			"---< Player challenges: " + U.arrayToString(this.playerChallenges, " |--| "),
			"---< Owner_challenges: " + U.arrayToString(this.islandChallenges, " |--| "),
			"---< Player_level: " + playerLevel,
			"---< Owner_level: " + islandLevel,
			"");
		}
	}
	
	@Nullable
	public String getName() {
		return name;
	}
	
	public double getPlayerLevel() {
		return playerLevel;
	}
	
	public double getOwnerLevel() {
		return islandLevel;
	}
	
	@Nullable
	public SkyblockChallenge[] getPlayerChallenges() {
		return playerChallenges;
	}
	
	public SkyblockChallenge[] getOwnerChallenges() {
		return islandChallenges;
	}
	
	public SkyblockRequirementType getType() {
		return type;
	}
	
}
