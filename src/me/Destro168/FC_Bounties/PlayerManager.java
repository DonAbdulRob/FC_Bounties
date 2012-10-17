package me.Destro168.FC_Bounties;

import me.Destro168.ConfigManagers.CustomConfigurationManager;

public class PlayerManager
{
	private String playerPath;
	private String name;
	private CustomConfigurationManager profile;
	
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
		profile = new CustomConfigurationManager(FC_Bounties.plugin.getDataFolder().getAbsolutePath() + "\\userinfo", name);
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









