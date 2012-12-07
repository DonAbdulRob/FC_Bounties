package me.Destro168.FC_Bounties;

import me.Destro168.FC_Suite_Shared.ConfigManagers.FileConfigurationWrapper;

public class PlayerManager
{
	private String playerPath;
	private String name;
	private FileConfigurationWrapper playerProfile;
	
	public void setCreated(boolean x) { playerProfile.set(playerPath + "created",x); }
	public void setExempt(boolean x) { playerProfile.set(playerPath + "exempt",x); }
	public void setKills(int x) { playerProfile.set(playerPath + "kills",x); }
	public void setSurvives(int x) { playerProfile.set(playerPath + "survives",x); }
	
	public boolean getCreated() { return playerProfile.getBoolean(playerPath + "created"); }
	public boolean getExempt() { return playerProfile.getBoolean(playerPath + "exempt"); }
	public int getKills() { return playerProfile.getInt(playerPath + "kills"); }
	public int getSurvives() { return playerProfile.getInt(playerPath + "survives"); }
	
	public PlayerManager(String name_)
	{
		//Store name
		name = name_;
		
		//Set the player path.
		playerPath = "FC_Bounties.";
		
		//New shared player profile
		playerProfile = new FileConfigurationWrapper(FC_Bounties.plugin.getDataFolder().getAbsolutePath() + "/userinfo", name);
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









