package me.Destro168.FC_Bounties.Utilities;

import java.util.Date;
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
		Date now = new Date();
		ConfigSettingsManager csm = new ConfigSettingsManager();
		
		if (csm.getEnableMoneyLogging() == false)
			return;
		
		writeToFile("moneyLogging.txt", "[" + dfm.format(now.getTime()) + "] " + loggable + "\n");
	}
}
