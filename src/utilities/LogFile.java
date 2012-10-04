package utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFile 
{
	private String pluginAbsolutePath;
	
	public LogFile(String pluginAbsolutePath_)
	{
		pluginAbsolutePath = pluginAbsolutePath_;
	}
	
	public void logMoneyTransaction(String loggable)
	{
		//Variable Declarations
		Date now = new Date();
		String truePath = pluginAbsolutePath + "\\moneyLogging.txt";
		ConfigSettingsManager csm = new ConfigSettingsManager();
		DateFormat dfm = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		if (csm.getEnableMoneyLogging() == false)
			return;
		
		//Attempt to create file if it doesn't exist.
		File f = new File(truePath);
		
		if (!f.exists())
		{
			try {
				f.createNewFile();
				f.setWritable(true);
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
		}
		
		//Create a writer stream.
		FileWriter fstream;
		
		try {
			fstream = new FileWriter(truePath,true);
		} catch (IOException e) 
		{
			e.printStackTrace();
			return;
		}
		
		//Create a buffered out stream.
		BufferedWriter out = new BufferedWriter(fstream);
		
		try {
			out.append("[" + dfm.format(now.getTime()) + "] " + loggable + "\n");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}













