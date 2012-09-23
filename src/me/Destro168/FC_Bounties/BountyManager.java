package me.Destro168.FC_Bounties;

import java.util.Date;
import java.util.List;
import java.util.Random;

import me.Destro168.ConfigManagers.ConfigManager;
import me.Destro168.Messaging.BroadcastLib;
import me.Destro168.Messaging.MessageLib;
import me.Destro168.Messaging.StringFormatter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import utilities.ConfigSettingsManager;
import utilities.FC_BountiesPermissions;

public class BountyManager
{
	private FC_Bounties plugin;
	private FileConfiguration config;
	ConfigSettingsManager csm = new ConfigSettingsManager();
	BroadcastLib bLib = new BroadcastLib();
	ConfigManager cm = new ConfigManager();
	
	public void setCreator(int x, String y) { config = plugin.getConfig(); config.set("Bounty" + x + ".creator", y); plugin.saveConfig(); }
	public void setTarget(int x, String y) { config = plugin.getConfig(); config.set("Bounty" + x + ".target", y); plugin.saveConfig(); }
	public void setActive(int x, boolean y) { config = plugin.getConfig(); config.set("Bounty" + x + ".active", y); plugin.saveConfig(); }
	public void setAmount(int x, int y) { config = plugin.getConfig(); config.set("Bounty" + x + ".amount", y); plugin.saveConfig(); }
	public void setDate(int x, long l) { config = plugin.getConfig(); config.set("Bounty" + x + ".date", l); plugin.saveConfig(); }
	
	public void setPosX(int x, int y) { config = plugin.getConfig(); config.set("Bounty" + x + ".posX", y); plugin.saveConfig(); }
	public void setPosY(int x, int y) { config = plugin.getConfig(); config.set("Bounty" + x + ".posY", y); plugin.saveConfig(); }
	public void setPosZ(int x, int y) { config = plugin.getConfig(); config.set("Bounty" + x + ".posZ", y); plugin.saveConfig(); }
	public void setTier(int x, int y) { config = plugin.getConfig(); config.set("Bounty" + x + ".tier", y); plugin.saveConfig(); }
	
	public String getCreator(int x) { config = plugin.getConfig(); return config.getString("Bounty" + x + ".creator"); }
	public String getTarget(int x) { config = plugin.getConfig(); return config.getString("Bounty" + x + ".target"); }
	public boolean getActive(int x) { config = plugin.getConfig(); return config.getBoolean("Bounty" + x + ".active"); }
	public int getAmount(int x) { config = plugin.getConfig(); return config.getInt("Bounty" + x + ".amount"); }
	public long getDate(int x) { config = plugin.getConfig(); return config.getLong("Bounty" + x + ".date"); }
	
	public int getPosX(int x) { config = plugin.getConfig(); return config.getInt("Bounty" + x + ".posX"); }
	public int getPosY(int x) { config = plugin.getConfig(); return config.getInt("Bounty" + x + ".posY"); }
	public int getPosZ(int x) { config = plugin.getConfig(); return config.getInt("Bounty" + x + ".posZ"); }
	public int getTier(int x) { config = plugin.getConfig(); return config.getInt("Bounty" + x + ".tier"); }
	
	//Non-get/sets
	public void deleteBounty(int removeNumber) { config.set("Bounty" + String.valueOf(removeNumber), null); plugin.saveConfig(); }
	
	//Empty constructor
	public BountyManager()
	{
		plugin = FC_Bounties.plugin;
	}
	
	public int addNewBounty(String creator, String target, int amount, Location loc)
	{
		Date now = new Date();
		
		for (int i = 0; i < FC_Bounties.MAX_BOUNTIES; i++)
		{
			if (getActive(i) == false)
			{
				//We want to set the bounties stats.
				setActive(i, true);
				setCreator(i, creator);
				setTarget(i, target);
				setAmount(i, amount);
				setDate(i, now.getTime());
				
				if (loc == null)
				{
					setPosX(i,0);
					setPosY(i,0);
					setPosZ(i,0);
				}
				else
				{
					setPosX(i, (int) loc.getX());
					setPosY(i, (int) loc.getY());
					setPosZ(i, (int) loc.getZ());
				}
				
				setTier(i, 0);
				
				i = FC_Bounties.MAX_BOUNTIES;
			}
		}
		
		return -1;
	}
	
	public int generateServerBounty(Player target)
	{
		int newID = 0;
		MessageLib msgLib = new MessageLib(target);
		FC_BountiesPermissions perms = new FC_BountiesPermissions(target);
		
		addNewBounty("[SERVER]", target.getName(), csm.getGeneratedBountyBase(), target.getLocation());
		
		//Broadcast a message.
		if (csm.getEnableServerTargetName() == true)
			msgLib.standardBroadcast("A Server Bounty Has Been Created. The Target Is: " + target.getName());
		else
			msgLib.standardBroadcast("A Server Bounty Has Been Created.");
		
		//Tell all online admins who the server bounty is.
		msgLib.broadcastToAdmins("New Server Bounty Target: " + getTarget(getServerBountyID()));
		
		//Tell the target they are the server bounty.
		if (csm.getEnableServerTargetName() == false)
		msgLib.standardMessage("Congrutalations, You Have Been Picked To Be The Server Bounty Target!");
		
		if (perms.commandDrop())
		{
			if (csm.getTimeBeforeDrop() > 0)
				msgLib.standardMessage("Tip: You Can Drop The Bounty With: /bounty drop after " + csm.getTimeBeforeDrop() * .001 + "seconds.");
			else
				msgLib.standardMessage("Tip: You Can Drop The Bounty With: /bounty drop.");
		}
		
		if (csm.getBlockedCommandUseCost() > 0)
			msgLib.standardMessage("Tip: Do NOT Use Commands. Each Use Will Cost You " + msgLib.getFormattedMoney(csm.getBlockedCommandUseCost(), cm.primaryColor) + ". This CAN and WILL make your balance go negative.");
		
		return newID;
	}
	
	public String getInformation(int i, int contentLevel, String standardMessageColor)
	{
		StringFormatter sf = new StringFormatter();
		Random rand = new Random();
		int offset;
		int x;
		int y;
		int z;
		
		String message = "";
		
		if (contentLevel == 1)
		{	
			if (csm.getEnableCreatorName())
			{
				message = message + "[Creator]: " + getCreator(i) + " ";
			}
			
			if (csm.getEnablePlayerTargetName())
			{
				message = message + "[Target]: " + getTarget(i) + " ";
			}
			
			message = message + "[Reward]: " + sf.getFormattedMoney(getAmount(i), standardMessageColor);
			
			if (csm.getEnablePlayerCoordinates() == true)
			{
				x = getPosX(i);
				y = getPosY(i);
				z = getPosZ(i);
				
				if (csm.getEnableRandomCoordinates())
				{
					//Get the offset from configuration.
					offset = csm.getRandomOffsetAmount();
					
					//Apply the random offset.
					x = x + rand.nextInt(offset);
					y = y + rand.nextInt(offset);
					z = z + rand.nextInt(offset);
				}
				
				message = message + "[X]: " + String.valueOf(x) + " [Y]: " + String.valueOf(y) + " [Z]: " + String.valueOf(z);
			}
		}
		else if (contentLevel == 2)
		{
			if (csm.getEnableServerTargetName())
			{
				message = message + "[Target]: " + getTarget(i) + " ";
			}
			
			message = message + "[Reward]: " + sf.getFormattedMoney(getAmount(i), standardMessageColor) + " ";
			
			if (csm.getEnableServerCoordinates() == true)
			{
				x = getPosX(i);
				y = getPosY(i);
				z = getPosZ(i);
				
				if (csm.getEnableRandomCoordinates())
				{
					//Get the offset from configuration.
					offset = csm.getRandomOffsetAmount();
					
					//Apply the random offset.
					x = x + rand.nextInt(offset);
					y = y + rand.nextInt(offset);
					z = z + rand.nextInt(offset);
				}
				
				message = message + "[X]: " + String.valueOf(x) + " [Y]: " + String.valueOf(y) + " [Z]: " + String.valueOf(z);
			}
		}
		
		return message;
	}
	
	public boolean hasServerBounty(String name)
	{	
		int id = getServerBountyID();
		
		if (id > -1)
		{
			if (getTarget(id).equals(name))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public int getServerBountyID()
	{
		for (int i = 0; i < FC_Bounties.MAX_BOUNTIES; i++)
		{
			if (getCreator(i) != null)
			{
				if (getCreator(i).equals("[SERVER]"))
				{
					return i;
				}
			}
		}
		
		return -1;
	}
	
	public int getBountyIDByName(String name)
	{
		for (int i = 0; i < FC_Bounties.MAX_BOUNTIES; i++)
		{
			if (getTarget(i).equals(name))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public void manageServerBounty()
	{
		Player chosenPlayer;
		Date date;
		boolean targetIsOnline = false;
		int playerCount = getPlayerCount();
		int serverBounty;
		MessageLib msgLib;
		Player target = null;
		
		if (csm.getBountyGeneration() == false)
			return;
		
		//Find the server bounty.
		serverBounty = getServerBountyID();
		
		//Update server bounty coordinates.
		if (csm.getServerCoordinateUpdate())
		{
			if (serverBounty > -1)
			{
				//Update the server bounty if the target is still online or else delete it and create a new one.
				for (Player player: Bukkit.getServer().getOnlinePlayers())
				{
					if (player.getName() == getTarget(serverBounty)) 
					{
						//Store the target.
						target = player;
						
						//If online store that he is online and update location
						targetIsOnline = true;
						
						setPosX(serverBounty, (int) player.getLocation().getX());
						setPosY(serverBounty, (int) player.getLocation().getY());
						setPosZ(serverBounty, (int) player.getLocation().getZ());
					}
				}
			}
		}
		
		//Update player coordinates
		if (csm.getPlayerCoordinateUpdate())
		{
			for (int i = 0; i < FC_Bounties.MAX_BOUNTIES; i++)
			{
				if (i != serverBounty)
				{
					for (Player player: Bukkit.getServer().getOnlinePlayers())
					{
						if (player.getName() == getTarget(i))
						{
							setPosX(i, (int) player.getLocation().getX());
							setPosY(i, (int) player.getLocation().getY());
							setPosZ(i, (int) player.getLocation().getZ());
						}
					}
				}
			}
		}
		
		if (targetIsOnline == false && serverBounty > -1)
		{
			//Announce who chickened out on last server bounty.
			bLib.standardBroadcast(csm.getLastBounty() + " Chickened Out!");
			
			//If not online delete the bounty.
			deleteBounty(serverBounty);
		}
		
		//Execute if the required amount of players are online.
		if (playerCount >= csm.getRequiredPlayers())
		{
			if ((targetIsOnline == false) && serverBounty == -1) 
			{
				//Get a random player
				chosenPlayer = getRandomServerBountyTarget();
				
				if (chosenPlayer == null)
					return;
				
				generateServerBounty(chosenPlayer);
			}
			
			else if (targetIsOnline == true && serverBounty > -1)
			{
				date = new Date();
				serverBounty = getServerBountyID();
				
				if (csm.getEnableIgnoreWorlds() == true)
				{
					if (csm.getIgnoreWorlds().contains(target.getWorld()))
					{
						bLib.standardBroadcast("The server bounty target went to a forbidden world! Server bounty dropped.");
						deleteBounty(serverBounty);
						return;
					}
				}
				
				//Now that we know the player is online still...
				//Check how old the bounty is and based on that increase the amount/broadcast it.
				//If an hour has passed reward the person with a bounty with the bounty amount.
				if (date.getTime() - getDate(serverBounty) >= csm.getTierLength(3))
				{
					rewardBountySurvive(Bukkit.getServer().getPlayer(getTarget(serverBounty)), getAmount(serverBounty), getServerBountyID());
				}
				else if (date.getTime() - getDate(serverBounty) >= csm.getTierLength(2))
				{
					updateServerBounty(3);
				}
				else if (date.getTime() - getDate(serverBounty) >= csm.getTierLength(1))
				{
					updateServerBounty(2);
				}
				else if (date.getTime() - getDate(serverBounty) >= csm.getTierLength(0))
				{
					updateServerBounty(1);
				}
				else
				{
					setAmount(0, playerCount * csm.getTierBaseBonus(0) + csm.getTierBaseBonus(0));
				}
			}
		}
		else
		{
			if (getServerBountyID() > -1)
			{
				//If the server bounty is coming up as null, we want to generate a new server bounty.
				if (Bukkit.getServer().getPlayer(getTarget(getServerBountyID())) == null)
				{
					//Get a random player
					chosenPlayer = getRandomServerBountyTarget();
					
					if (chosenPlayer == null)
						return;
					
					//Then create a server bounty.
					generateServerBounty(chosenPlayer);
					
					return;
				}
				
				if (target.isOnline() == true)
				{
					msgLib = new MessageLib(target);
					msgLib.standardMessage("Warning! There aren't enough players online so the bounty was dropped.");
					deleteBounty(getServerBountyID());
				}
			}
		}
	}
	
	public Player getRandomServerBountyTarget()
	{
		Random newRandom = new Random();
		Player chosenPlayer = null;
		FC_BountiesPermissions perms;
		ConfigSettingsManager csm = new ConfigSettingsManager();
		int randomNumber = 0;
		int chosenPlayerNumber = 0;
		PlayerManager pm;
		int truePlayerCount = Bukkit.getServer().getOnlinePlayers().length;
		boolean ignorePlayer;
		List<String> ignoreWorlds = csm.getIgnoreWorlds();
		
		//Choose a random player to be the server bounty target
		for (Player player : Bukkit.getServer().getOnlinePlayers())
		{
			//Reset ignore player.
			ignorePlayer = false;
			
			//First we check based on the true player count.
			if (truePlayerCount > 1)
			{
				if (player.getName().equals(csm.getLastBounty()))
					ignorePlayer = true;
			}
			
			//If we aren't ignoring the player, then...
			if (ignorePlayer == false)
			{
				//Check permissions
				perms = new FC_BountiesPermissions(player);
				
				if (perms.isUntargetable() == true)
					ignorePlayer = true;
			}
			
			//If we aren't ignoring the player, then...
			if (ignorePlayer == false)
			{
				//Check exemption
				pm = new PlayerManager(player.getName());
				
				pm.checkPlayerData();
				
				if (csm.getIgnorePlayerExemptionSetting() == false)
				{
					if (pm.getExempt() == true)
						ignorePlayer = true;
				}
			}
			
			if (ignorePlayer == false)
			{
				if (ignoreWorlds.contains(player.getWorld().getName()) == true)
					ignorePlayer = true;
			}
			
			if (ignorePlayer == false)
			{
				//If all of that passes, continue to 
				randomNumber = newRandom.nextInt(10000) + 1;
				
				if (randomNumber > chosenPlayerNumber)
				{
					chosenPlayerNumber = randomNumber;
					chosenPlayer = player;
				}
			}
		}
		
		return chosenPlayer;
	}
	
	public void updateServerBounty(int x)
	{
		StringFormatter sf = new StringFormatter();
		int serverBountyID = getServerBountyID();
		int amount;
		
		//If the server bounty id is -1, we return.
		if (serverBountyID == -1)
			return;
		
		//Set the value of the bounty.
		amount = getAmount(serverBountyID) + getPlayerCount() * csm.getTierBaseBonus(x) + csm.getTierBaseBonus(x);
		
		setAmount(serverBountyID, amount);
		
		if (getTier(serverBountyID) == x - 1)
			bLib.standardBroadcast("The server bounty has increased a tier! It is now worth: " + sf.getFormattedMoney(amount, cm.secondaryColor) + "!");
		
		setTier(serverBountyID, getTier(serverBountyID) + 1);
	}
	
	public int getPlayerCount()
	{
		int count = 0;
		FC_BountiesPermissions perms;
		PlayerManager playerManager;
		boolean ignoreExempt = csm.getIgnorePlayerExemptionSetting();
		
		//Count total players online.
		for (Player player: Bukkit.getServer().getOnlinePlayers())
		{
			playerManager = new PlayerManager(player.getName());
			
			playerManager.checkPlayerData();
			
			perms = new FC_BountiesPermissions(player);
			
			if (ignoreExempt == true)
			{
				if (perms.isUntargetable() == false)
					count = count + 1;
			}
			else
			{
				if (perms.isUntargetable() == false && playerManager.getExempt() == false)
					count = count + 1;
			}
		}
		
		return count;
	}
	
	public void deleteAllBounties()
	{
		for (int i = 0; i < FC_Bounties.MAX_BOUNTIES; i++)
		{
			deleteBounty(i);
		}
	}
	
	public void purgeOldBounties()
	{
		Date dateNow = new Date();
		long timeDifference;
		
		for (int i = 0; i < FC_Bounties.MAX_BOUNTIES; i++)
		{
			if (getActive(i) == true)
			{
				timeDifference = dateNow.getTime() - getDate(i);
				
				if (timeDifference >= 259200000) // 3 days
				{
					deleteBounty(i);
				}
			}
		}
	}
	
	public int getBountyByName(String name)
	{
		for (int i = 0; i < FC_Bounties.MAX_BOUNTIES; i++)
		{
			if (getActive(i) == true)
			{
				if (getCreator(i).equals(name))
				{
					return i;
				}
			}
		}
		
		return -1;
	}
	
	public void rewardBountyKill(Player killer, String killedName, double amount, int bountyID)
	{
		TopKillersBoard tkb = new TopKillersBoard();
		String name = killer.getName();
		PlayerManager playerManager = new PlayerManager(name);
		double percentAmount;
		
		if (bountyID == getServerBountyID())
		{
			if (csm.getKillerBonusAmount() > 0)
			{
				bLib.standardBroadcast(name + " has killed " + killedName + " to win " + bLib.getFormattedMoney(amount, cm.secondaryColor) + " and the kill bonus of $" + csm.getKillerBonusAmount() + "!");
				
				//Add configurable bonus
				amount = amount + csm.getKillerBonusAmount();
			}
			//Create a percent amount if the survival bonus isn't a flat amount.
			else if (csm.getKillerBonusAmount() > 0) 
			{
				percentAmount =  amount / csm.getKillerBonusAmount();
				
				bLib.standardBroadcast(name + " has killed " + killedName + " to win " + bLib.getFormattedMoney(amount, cm.secondaryColor) + " and the kill bonus of $" + String.valueOf(percentAmount) + "!");
				
				//Add configurable bonus
				amount = amount + percentAmount;
			}
			else
			{
				bLib.standardBroadcast(name + " has killed " + killedName + " to win " + bLib.getFormattedMoney(amount, cm.secondaryColor) + "!");
			}
		}
		else
		{
			bLib.standardBroadcast(name + " has killed " + killedName + " to win " + bLib.getFormattedMoney(amount, cm.secondaryColor) + "!");
		}
		
		//Log the event
		plugin.getLogger().info("Killer: " + name + " won amount: " + amount);
		
		//Give the player the reward
		FC_Bounties.economy.depositPlayer(name, amount);
		
		//Remove the bounty
		deleteBounty(bountyID);
		
		//Add a kill to the players record
		playerManager.setKills(playerManager.getKills() + 1);
		
		//Update the leaderboard.
		tkb.attemptUpdateKillerLeaderBoard(name, playerManager.getKills());
		
		//Prevent bounties from being placed on the same person.
		csm.setLastBounty(killedName);
		
		//Try to generate another bounty.
		manageServerBounty();
	}
	
	public void rewardBountySurvive(Player winner, double amount, int bountyID)
	{
		TopSurvivorsBoard tsb = new TopSurvivorsBoard();
		String name = winner.getName();
		PlayerManager playerManager = new PlayerManager(name);
		double percentAmount;
		
		if (csm.getSurvivalBonusAmount() > 0)
		{
			bLib.standardBroadcast(name + " has survived to win " + bLib.getFormattedMoney(amount, cm.secondaryColor) + " and the survival bonus of $" + csm.getSurvivalBonusAmount() + "!");
			
			//Add survival bonus
			amount = amount + csm.getSurvivalBonusAmount();
		}
		//Create a percent amount if the survival bonus isn't a flat amount.
		else if (csm.getSurvivalBonusPercent() > 0)
		{
			percentAmount =  amount / csm.getSurvivalBonusPercent();
			
			bLib.standardBroadcast(name + " has survived to win " + bLib.getFormattedMoney(amount, cm.secondaryColor) + " and the survival bonus of $" + String.valueOf(percentAmount) + "!");
			
			//Add percent bonus.
			amount = amount + percentAmount;
		}
		else
		{
			bLib.standardBroadcast(name + " has survived to win " + bLib.getFormattedMoney(amount, cm.secondaryColor) + "!");
		}
		
		//Log the event
		plugin.getLogger().info("Survivor: " + name + " won amount: " + bLib.getFormattedMoney(amount, cm.secondaryColor));
		
		//Give the player the reward
		FC_Bounties.economy.depositPlayer(name, amount);
		
		//Remove the bounty
		deleteBounty(bountyID);
		
		//Add a kill to the players record
		playerManager.setSurvives(playerManager.getSurvives() + 1);
		
		//Update the leaderboard.
		tsb.attemptUpdateSurvivorLeaderBoard(name, playerManager.getKills());
		
		//Prevent bounties from being placed on the same person.
		csm.setLastBounty(name);
		
		//Try to generate another bounty.
		manageServerBounty();
	}
}






