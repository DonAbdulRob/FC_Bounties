package me.Destro168.FC_Bounties.Utilities;

import me.Destro168.FC_Suite_Shared.FileLineWriter;

public class BountyLogFile extends FileLineWriter
{
	public BountyLogFile(String pluginAbsolutePath)
	{
		super(pluginAbsolutePath);
	}
	
	public void logMoneyTransaction(String loggable)
	{
		//Variable Declarations
		ConfigSettingsManager csm = new ConfigSettingsManager();
		
		if (csm.getEnableMoneyLogging() == false)
			return;
		
		writeToFile("moneyLogging.txt", "[" + dfm.format(System.currentTimeMillis()) + "] " + loggable + "\n");
	}
}
