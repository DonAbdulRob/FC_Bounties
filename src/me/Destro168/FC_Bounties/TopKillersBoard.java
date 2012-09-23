package me.Destro168.FC_Bounties;

import java.util.List;

import me.Destro168.Messaging.BroadcastLib;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TopKillersBoard
{
	private FC_Bounties plugin;
	private FileConfiguration config;
	private List<String> names;
	private List<Integer> counts;
	BroadcastLib bLib = new BroadcastLib();
	
	public List<String> getTopKillersNames() { config = plugin.getConfig(); return config.getStringList("TopKillers.name"); }
	public List<Integer> getTopKillersCounts() { config = plugin.getConfig(); return config.getIntegerList("TopKillers.count"); }
	
	private void setTopKillersNames(List<String> x) { config = plugin.getConfig(); config.set("TopKillers.name",x); plugin.saveConfig(); }
	private void setTopKillersCounts(List<Integer> x) { config = plugin.getConfig(); config.set("TopKillers.count",x); plugin.saveConfig(); }
	
	public TopKillersBoard() 
	{
		plugin = FC_Bounties.plugin;
		
		names = getTopKillersNames();
		counts = getTopKillersCounts();
		
		//If the topkillers board hasn't been made yet, then....
		if (names.size() == 0 && counts.size() == 0)
		{
			//Create five default bounties.
			for (int i = 0; i < 5; i++)
			{
				names.add("[Nobody]");
				counts.add(-1);
			}
			
			//Update the names and counts.
			updateTopKillers();
		}
	}
	
	private void updateTopKillers() 
	{
		setTopKillersNames(names);
		setTopKillersCounts(counts);
	}
	
	public void attemptUpdateKillerLeaderBoard(String name, int totalKills)
	{
		int size = counts.size();
		
		//If the new entry exists in the list, then....
		for (int i = 0; i < size; i++)
		{
			if (names.get(i).equals(name))
			{
				//Shift everything up to fill the entry spot up.
				for (int j = i; j > 0; j--)
				{
					counts.set(j, counts.get(j-1));
					names.set(j, names.get(j-1));
				}
			}
		}
		
		//Going from size -> 0.
		for (int i = size - 1; i > -1; i--)
		{
			//If we reach a spot, store there immediately and end.
			if (counts.get(i) == 0)
			{
				counts.set(i, totalKills);
				names.set(i, name);
				
				//End the loop.
				i = -1;
			}
			
			//Else we want to only store names on the list if they are greater than a previous entry.
			else if (counts.get(i) < totalKills)
			{
				//Shift everything down at that spot by 1.
				for (int j = 0; j < i; j++)
				{
					counts.set(j, counts.get(j+1));
					names.set(j, names.get(j+1));
				}
				
				//Put the new entry at that location.
				counts.set(i, totalKills);
				names.set(i, name);
				
				//End the loop.
				i = -1;
			}
		}
		
		updateTopKillers();
	}
	
	public void returnKillerLeaderBoard(Player player)
	{
		
	}
}




