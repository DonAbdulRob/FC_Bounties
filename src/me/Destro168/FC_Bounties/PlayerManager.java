package me.Destro168.FC_Bounties;

import me.Destro168.ConfigManagers.SharedPlayerProfileManager;

import org.bukkit.configuration.file.FileConfiguration;

public class PlayerManager
{
	private String playerPath;
	private String name;
	private SharedPlayerProfileManager profile;
	
	public void setCreated(boolean x) { profile.set(playerPath + "created",x); }
	public void setExempt(boolean x) { profile.set(playerPath + "exempt",x); }
	public void setKills(int x) { profile.set(playerPath + "kills",x); }
	public void setSurvives(int x) { profile.set(playerPath + "survives",x); }
	
	public boolean getCreated() { return profile.getBoolean(playerPath + "created"); }
	public boolean getExempt() { return profile.getBoolean(playerPath + "exempt"); }
	public int getKills() { return profile.getInt(playerPath + "kills"); }
	public int getSurvives() { return profile.getInt(playerPath + "survives"); }
	
	public PlayerManager(String name_)
	{
		//Store name
		name = name_;
		
		//Set the player path.
		playerPath = "FC_Bounties.";
		
		//New shared player profile
		profile = new SharedPlayerProfileManager(name, FC_Bounties.plugin.getDataFolder().getAbsolutePath());
		
	}
	
	//Handle transfer of everything from FC_Suite_Shared folder to seperate folders.
	public void transferPlayerData2()
	{
		SharedPlayerProfileManager oldProfile =  new SharedPlayerProfileManager(name, "");
		
		//Update old player data to new format.
		setCreated(oldProfile.getBoolean(playerPath + "created"));
		setExempt(oldProfile.getBoolean(playerPath + "exempt"));
		setKills(oldProfile.getInt(playerPath + "kills"));
		setSurvives(oldProfile.getInt(playerPath + "survives"));
		
		//Update old player data to new format.
		oldProfile.set("FC_Bounties",null);
	}
	
	public void transferPlayerData()
	{
		//Variable Declarations
		FileConfiguration config = FC_Bounties.plugin.getConfig();
		String oldPlayerPath = "Player." + name + ".";
		
		if (config.getString(oldPlayerPath + "created") == null)
			return;
		
		if (config.getString(oldPlayerPath + "created").equals(""))
			return;
		
		profile.set("FC_Bounties.created", config.getInt(oldPlayerPath + "created"));
		profile.set("FC_Bounties.exempt", config.getInt(oldPlayerPath + "exempt"));
		profile.set("FC_Bounties.kills", config.getBoolean(oldPlayerPath + "kills"));
		profile.set("FC_Bounties.survives", config.getBoolean(oldPlayerPath + "survives"));
		
		FC_Bounties.plugin.saveConfig();
	}
	
	public void checkPlayerData()
	{
		if (getCreated() == false)
		{
			setCreated(true);
			setExempt(false);
			setKills(0);
			setSurvives(0);
		}
	}
}









