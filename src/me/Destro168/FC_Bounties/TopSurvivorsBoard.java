package me.Destro168.FC_Bounties;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TopSurvivorsBoard
{
	private FC_Bounties plugin;
	private FileConfiguration config;
	List<String> names;
	List<Integer> counts;
	
	public List<String> getTopSurvivorsNames() { config = plugin.getConfig(); return config.getStringList("TopSurvivors.name"); }
	public List<Integer> getTopSurvivorsCounts() { config = plugin.getConfig(); return config.getIntegerList("TopSurvivors.count"); }
	
	public void setTopSurvivorsNames(List<String> x) { config = plugin.getConfig(); config.set("TopSurvivors.name",x); plugin.saveConfig(); }
	public void setTopSurvivorsCounts(List<Integer> x) { config = plugin.getConfig(); config.set("TopSurvivors.count",x); plugin.saveConfig(); }
	
	public TopSurvivorsBoard() 
	{
		plugin = FC_Bounties.plugin;
		
		names = getTopSurvivorsNames();
		counts = getTopSurvivorsCounts();
		
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
			updateTopSurvivors();
		}
	}
	
	public void updateTopSurvivors() 
	{
		setTopSurvivorsNames(names);
		setTopSurvivorsCounts(counts);
	}
	
	public void attemptUpdateSurvivorLeaderBoard(String name, int totalSurvives)
	{
		int size = counts.size();
		
		//If the new entry exists in the list, then....
		for (int i = 0; i < size; i++)
		{
			if (names.get(i).equals(name))
			{
				//Remove the entry, shift everything up to fill the spot.
				counts.set(i, 0);
				names.set(i, "");
				
				//Shift everything up to fill the spot.
				for (int j = i; j > 0; j--)
				{
					counts.set(j, counts.get(j-1));
					names.set(j, names.get(j-1));
				}
			}
		}
		
		//Going from 10 -> -1.
		for (int i = size - 1; i > -1; i--)
		{
			//If we reach a spot, store there immediately and end.
			if (counts.get(i) == 0)
			{
				counts.set(i, totalSurvives);
				names.set(i, name);
				
				//End the loop.
				i = -1;
			}
			
			//Else we want to only store names on the list if they are greater than a previous entry.
			else if (counts.get(i) < totalSurvives)
			{
				//Shift everything down at that spot by 1.
				for (int j = i; j > 0; j--)
				{
					counts.set(j, counts.get(j+1));
					names.set(j, names.get(j+1));
				}
				
				//Put the new entry at that location.
				counts.set(i, totalSurvives);
				names.set(i, name);
				
				//End the loop.
				i = -1;
			}
		}
		
		updateTopSurvivors();
	}
	
	public void returnSurvivorLeaderBoard(Player player)
	{
		
	}
}




