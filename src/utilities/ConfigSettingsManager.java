package utilities;

import java.util.ArrayList;
import java.util.List;

import me.Destro168.FC_Bounties.FC_Bounties;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigSettingsManager
{
	private FC_Bounties plugin;
	private FileConfiguration config;
	
	public void setVersion(double x) { config = plugin.getConfig(); config.set("Version", x); plugin.saveConfig(); }
	
	public void setInvulnerabilityEnabled(boolean x) { config = plugin.getConfig(); config.set("Setting.invulnerabilityEnabled", x); plugin.saveConfig(); }
	public void setEnableCreatorName(boolean x) { config = plugin.getConfig(); config.set("Setting.enableCreatorName", x); plugin.saveConfig(); }
	public void setEnablePlayerTargetName(boolean x) { config = plugin.getConfig(); config.set("Setting.enablePlayerTargetName", x); plugin.saveConfig(); }
	public void setEnableServerTargetName(boolean x) { config = plugin.getConfig(); config.set("Setting.enableServerTargetName", x); plugin.saveConfig(); }
	public void setEnablePlayerCoordinates(boolean x) { config = plugin.getConfig(); config.set("Setting.enablePlayerCoordinates", x); plugin.saveConfig(); }
	public void setEnableServerCoordinates(boolean x) { config = plugin.getConfig(); config.set("Setting.enableServerCoordinates", x); plugin.saveConfig(); }
	public void setEnableRandomCoordinates(boolean x) { config = plugin.getConfig(); config.set("Setting.enableRandomCoordinates", x); plugin.saveConfig(); }
	public void setServerCoordinateUpdate(boolean x) { config = plugin.getConfig(); config.set("Setting.serverCoordinateUpdate", x); plugin.saveConfig(); }
	public void setPlayerCoordinateUpdate(boolean x) { config = plugin.getConfig(); config.set("Setting.playerCoordinateUpdate", x); plugin.saveConfig(); }
	public void setForcePvp(boolean x) { config = plugin.getConfig(); config.set("Setting.forcePvp", x); plugin.saveConfig(); }
	public void setBountyGeneration(boolean x) { config = plugin.getConfig(); config.set("Setting.bountyGeneration", x); plugin.saveConfig(); }
	public void setBlockCommands(boolean x) {  }
	public void setAnnouncePlayerBountyCreation(boolean x) { config = plugin.getConfig(); config.set("Setting.announcePlayerBountyCreation", x); plugin.saveConfig(); }
	public void setBountyIntervalLength(int x) { config = plugin.getConfig(); config.set("Setting.bountyIntervalLength", x); plugin.saveConfig(); }
	public void setTierBaseMultiplier(int tier, int multi) { config = plugin.getConfig(); config.set("Setting.tier" + tier + "BaseMultiplier", multi); plugin.saveConfig(); }
	public void setTierBaseBonus(int tier, int multi) { config = plugin.getConfig(); config.set("Setting.tier" + tier + "BaseBonus", multi); plugin.saveConfig(); }
	public void setTierLength(int tier, int length) { config = plugin.getConfig(); config.set("Setting.tier" + tier + "Length", length); plugin.saveConfig(); }
	public void setRequiredPlayers(int x) { config = plugin.getConfig(); config.set("Setting.requiredPlayers", x); plugin.saveConfig(); }
	public void setSurvivalBonusAmount(int x) { config = plugin.getConfig(); config.set("Setting.survivalBonusAmount", x); plugin.saveConfig(); }
	public void setKillerBonusAmount(int x) { config = plugin.getConfig(); config.set("Setting.killerBonusAmount", x); plugin.saveConfig(); }
	public void setMinimumBountyValue(int x) { config = plugin.getConfig(); config.set("Setting.minimumBountyValue", x); plugin.saveConfig(); }
	public void setMaximumBountiesPerPlayer(int x) { config = plugin.getConfig(); config.set("Setting.maximumBountiesPerPlayer", x); plugin.saveConfig(); }
	public void setRandomOffsetAmount(int x) { config = plugin.getConfig(); config.set("Setting.randomOffsetAmount", x); plugin.saveConfig(); }
	public void setTimeBeforeDrop(int x) { config = plugin.getConfig(); config.set("Setting.timeBeforeDrop", x); plugin.saveConfig(); }
	public void setLastBounty(String x) { config = plugin.getConfig(); config.set("Setting.lastBounty", x); plugin.saveConfig(); }
	public void setBlockedCommandUseCost(int x) { config = plugin.getConfig(); config.set("Setting.blockedCommandUseCost", x); plugin.saveConfig(); }
	public void setSurvivalBonusPercent(int x) { config = plugin.getConfig(); config.set("Setting.survivalBonusPercent", x); plugin.saveConfig(); }
	public void setKillerBonusPercent(int x) { config = plugin.getConfig(); config.set("Setting.killerBonusPercent", x); plugin.saveConfig(); }
	public void setGeneratedBountyBase(int x)  { config = plugin.getConfig(); config.set("Setting.generatedBountyBase", x); plugin.saveConfig(); }
	public void setIgnorePlayerExemptionSetting(boolean x) { config = plugin.getConfig(); config.set("Setting.ignorePlayerExemptionSetting", x); plugin.saveConfig(); }
	public void setIgnoreWorlds(List<String> x) { config = plugin.getConfig(); config.set("Setting.ignoreWorlds", x); plugin.saveConfig(); }
	public void setEnableIgnoreWorlds(boolean x) { config = plugin.getConfig(); config.set("Setting.enableIgnoreWorlds", x); plugin.saveConfig(); }
	public void setBountyCreationTaxPercent(double x) { config = plugin.getConfig(); config.set("Setting.bountyCreationTaxPercent", x); plugin.saveConfig(); }
	public void setBountyStealPercent(double x) { config = plugin.getConfig(); config.set("Setting.bountyStealPercent", x); plugin.saveConfig(); }
	public void setBountyDeathPercent(double x) { config = plugin.getConfig(); config.set("Setting.bountyDeathPercent", x); plugin.saveConfig(); }
	public void setEnableMoneyLogging(boolean x) { config = plugin.getConfig(); config.set("Setting.enableMoneyLogging", x); plugin.saveConfig(); }
	
	public double getVersion() 
	{
		config = plugin.getConfig();
		return config.getDouble("Version"); 
	}
	
	public boolean getInvulnerabilityEnabled() { config = plugin.getConfig(); return config.getBoolean("Setting.invulnerabilityEnabled"); }
	public boolean getEnableCreatorName() { config = plugin.getConfig(); return config.getBoolean("Setting.enableCreatorName"); }
	public boolean getEnablePlayerTargetName() { config = plugin.getConfig(); return config.getBoolean("Setting.enablePlayerTargetName"); }
	public boolean getEnableServerTargetName() { config = plugin.getConfig(); return config.getBoolean("Setting.enableServerTargetName"); }
	public boolean getEnablePlayerCoordinates() { config = plugin.getConfig(); return config.getBoolean("Setting.enablePlayerCoordinates"); }
	public boolean getEnableServerCoordinates() { config = plugin.getConfig(); return config.getBoolean("Setting.enableServerCoordinates"); }
	public boolean getEnableRandomCoordinates() { config = plugin.getConfig(); return config.getBoolean("Setting.enableRandomCoordinates"); }
	public boolean getServerCoordinateUpdate() { config = plugin.getConfig(); return config.getBoolean("Setting.serverCoordinateUpdate"); }
	public boolean getPlayerCoordinateUpdate() { config = plugin.getConfig(); return config.getBoolean("Setting.playerCoordinateUpdate"); }
	public boolean getForcePvp() { config = plugin.getConfig(); return config.getBoolean("Setting.forcePvp"); }
	public boolean getBountyGeneration() { config = plugin.getConfig(); return config.getBoolean("Setting.bountyGeneration"); }
	public boolean getBlockCommands() { config = plugin.getConfig(); return config.getBoolean("Setting.blockCommands"); }
	public boolean getAnnouncePlayerBountyCreation() { config = plugin.getConfig(); return config.getBoolean("Setting.announcePlayerBountyCreation"); }
	public int getBountyIntervalLength() { config = plugin.getConfig(); return config.getInt("Setting.bountyIntervalLength"); }
	public int getTierBaseMultiplier(int tier) { config = plugin.getConfig(); return config.getInt("Setting.tier" + tier + "BaseMultiplier"); }
	public int getTierBaseBonus(int tier) { config = plugin.getConfig(); return config.getInt("Setting.tier" + tier + "BaseBonus"); }
	public int getTierLength(int tier) { config = plugin.getConfig(); return config.getInt("Setting.tier" + tier + "Length"); }
	public int getRequiredPlayers() { config = plugin.getConfig(); return config.getInt("Setting.requiredPlayers"); }
	public int getSurvivalBonusAmount() { config = plugin.getConfig(); return config.getInt("Setting.survivalBonusAmount"); }
	public int getKillerBonusAmount() { config = plugin.getConfig(); return config.getInt("Setting.killerBonusAmount"); }
	public int getMinimumBountyValue() { config = plugin.getConfig(); return config.getInt("Setting.minimumBountyValue"); }
	public int getMaximumBountiesPerPlayer() { config = plugin.getConfig(); return config.getInt("Setting.maximumBountiesPerPlayer"); }
	public int getRandomOffsetAmount() { config = plugin.getConfig(); return config.getInt("Setting.randomOffsetAmount"); }
	public int getTimeBeforeDrop() { config = plugin.getConfig(); return config.getInt("Setting.timeBeforeDrop"); }
	public String getLastBounty() { config = plugin.getConfig(); return config.getString("Setting.lastBounty"); }
	public int getBlockedCommandUseCost() { config = plugin.getConfig(); return config.getInt("Setting.blockedCommandUseCost"); }
	public int getSurvivalBonusPercent() { config = plugin.getConfig(); return config.getInt("Setting.survivalBonusPercent"); }
	public int getKillerBonusPercent() { config = plugin.getConfig(); return config.getInt("Setting.killerBonusPercent"); }
	public int getGeneratedBountyBase()  { config = plugin.getConfig(); return config.getInt("Setting.generatedBountyBase"); }
	public boolean getIgnorePlayerExemptionSetting() { config = plugin.getConfig(); return config.getBoolean("Setting.ignorePlayerExemptionSetting"); }
	public boolean getEnableIgnoreWorlds() { config = plugin.getConfig(); return config.getBoolean("Setting.enableIgnoreWorlds"); }
	public List<String> getIgnoreWorlds() { config = plugin.getConfig(); return config.getStringList("Setting.ignoreWorlds"); }
	public double getBountyCreationTaxPercent() { config = plugin.getConfig(); return config.getDouble("Setting.bountyCreationTaxPercent"); }
	public double getBountyStealPercent() { config = plugin.getConfig(); return config.getDouble("Setting.bountyStealPercent"); }
	public double getBountyDeathPercent() { config = plugin.getConfig(); return config.getDouble("Setting.bountyDeathPercent"); }
	public boolean getEnableMoneyLogging() { config = plugin.getConfig(); return config.getBoolean("Setting.enableMoneyLogging"); }
	
	public ConfigSettingsManager()
	{
		plugin = FC_Bounties.plugin;
	}
	
	public void handleConfiguration()
	{
		//Load settings from configuration file.
		if (getVersion() < 2.31)
		{
			//Set the version.
			setVersion(2.31);
			
			setInvulnerabilityEnabled(true);
			setEnableCreatorName(true);
			setEnablePlayerTargetName(true);
			setEnablePlayerCoordinates(false);
			setEnablePlayerCoordinates(false);
			setEnableRandomCoordinates(false);
			setServerCoordinateUpdate(true);
			setPlayerCoordinateUpdate(false);
			setForcePvp(true);
			setBountyGeneration(true);
			setBlockCommands(true);
			setBountyIntervalLength(60);
			setTierBaseMultiplier(0,3);
			setTierBaseMultiplier(1,6);
			setTierBaseMultiplier(2,9);
			setTierBaseMultiplier(3,12);
			setTierLength(0,450000);
			setTierLength(1,900000);
			setTierLength(2,1350000);
			setTierLength(3,1800000);
			setRequiredPlayers(5);
			setSurvivalBonusAmount(50);
			setKillerBonusAmount(0);
			setMinimumBountyValue(10);
			setMaximumBountiesPerPlayer(20);
			setRandomOffsetAmount(250);
			setTimeBeforeDrop(300000);
			setLastBounty("");
			setBlockedCommandUseCost(1000);
			setSurvivalBonusPercent(0);
			setKillerBonusPercent(0);
			setAnnouncePlayerBountyCreation(true);
			setIgnorePlayerExemptionSetting(true);
			setEnableIgnoreWorlds(true);
			
			List<String> ignoreWorlds = new ArrayList<String>();
			ignoreWorlds.add("creative");
			setIgnoreWorlds(ignoreWorlds);
			
			//Set bounty base.
			setGeneratedBountyBase(25);
			
			//Set the new tier base bonuses.
			setTierBaseBonus(0,10);
			setTierBaseBonus(1,20);
			setTierBaseBonus(2,30);
			setTierBaseBonus(3,40);

			//Restore player target name option.
			setEnableServerTargetName(true);
			
			//Set new setting.
			setBountyCreationTaxPercent(5);
		}
		
		if (getVersion() < 3.0)
		{
			setVersion(3.0);

			setBountyStealPercent(0);
			setBountyDeathPercent(0);
			setEnableMoneyLogging(true);
		}
		
		if (getVersion() < 3.11)
		{
			setVersion(3.11);
		}
	}
}
























