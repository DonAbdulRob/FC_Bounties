package me.Destro168.FC_Bounties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import me.Destro168.ConfigManagers.CustomConfigurationManager;
import me.Destro168.Messaging.BroadcastLib;
import me.Destro168.Messaging.MessageLib;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import utilities.ConfigSettingsManager;
import utilities.FC_BountiesPermissions;

public class BountyManager
{
	private FC_Bounties plugin;
	private CustomConfigurationManager ccm;
	private ConfigSettingsManager csm = new ConfigSettingsManager();
	private BroadcastLib bLib = new BroadcastLib();
	
	public void setCreator(int x, String y) { ccm.set("Bounty" + x + ".creator", y); }
	public void setTarget(int x, String y) { ccm.set("Bounty" + x + ".target", y); }
	public void setActive(int x, boolean y) { ccm.set("Bounty" + x + ".active", y); }
	public void setAmount(int x, int y) { ccm.set("Bounty" + x + ".amount", y); }
	public void setDate(int x, long l) { ccm.set("Bounty" + x + ".date", l); }
	
	public void setPosX(int x, int y) { ccm.set("Bounty" + x + ".posX", y); plugin.saveConfig(); }
	public void setPosY(int x, int y) { ccm.set("Bounty" + x + ".posY", y); plugin.saveConfig(); }
	public void setPosZ(int x, int y) { ccm.set("Bounty" + x + ".posZ", y); plugin.saveConfig(); }
	public void setTier(int x, int y) { ccm.set("Bounty" + x + ".tier", y); plugin.saveConfig(); }
	
	public String getCreator(int x) { return ccm.getString("Bounty" + x + ".creator"); }
	public String getTarget(int x) { return ccm.getString("Bounty" + x + ".target"); }
	public boolean getActive(int x) { return ccm.getBoolean("Bounty" + x + ".active"); }
	public int getAmount(int x) { return ccm.getInt("Bounty" + x + ".amount"); }
	public long getDate(int x) { return ccm.getLong("Bounty" + x + ".date"); }
	
	public int getPosX(int x) { return ccm.getInt("Bounty" + x + ".posX"); }
	public int getPosY(int x) { return ccm.getInt("Bounty" + x + ".posY"); }
	public int getPosZ(int x) { return ccm.getInt("Bounty" + x + ".posZ"); }
	public int getTier(int x) { return ccm.getInt("Bounty" + x + ".tier"); }
	
	//Non-get/sets
	public void deleteBounty(int removeNumber) { ccm.set("Bounty" + String.valueOf(removeNumber), null); }
	
	//Empty constructor
	public BountyManager()
	{
		//Assign key variables.
		plugin = FC_Bounties.plugin;
		ccm = new CustomConfigurationManager(plugin.getDataFolder().getAbsolutePath(), "bounties");
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
		String targetName = target.getName();
		
		addNewBounty("[SERVER]", targetName, csm.getGeneratedBountyBase(), target.getLocation());
		
		//Broadcast a message.
		if (csm.getEnableServerTargetName() == true)
			msgLib.standardBroadcast("A Server Bounty Has Been Created. The Target Is: &p" + targetName + "&p");
		else
			msgLib.standardBroadcast("A Server Bounty Has Been Created.");
		
		//Tell all online admins who the server bounty is.
		msgLib.broadcastToAdmins("New Server Bounty Target: &p" + targetName + "&p");
		
		//Tell the target they are the server bounty.
		if (csm.getEnableServerTargetName() == false)
			msgLib.standardMessage("Congrutalations, You Have Been Picked To Be The Server Bounty Target!");
		
		if (perms.commandDrop())
		{
			if (csm.getTimeBeforeDrop() > 0)
				msgLib.standardMessage("Tip: You Can Drop The Bounty With: /bounty drop after &r" + csm.getTimeBeforeDrop() * .001 + "&r seconds.");
			else
				msgLib.standardMessage("Tip: You Can Drop The Bounty With: /bounty drop.");
		}
		
		if (csm.getBlockedCommandUseCost() > 0)
			msgLib.standardMessage("Tip: Do NOT Use Commands. Each Use Will Cost You &q" + csm.getBlockedCommandUseCost() + "&q. This CAN and WILL make your balance go negative.");
		
		return newID;
	}
	
	public List<String> getInformation(int i, int contentLevel, String standardMessageColor)
	{
		Random rand = new Random();
		int offset;
		int x;
		int y;
		int z;
		
		List<String> message = new ArrayList<String>();
		
		if (contentLevel == 1)
		{	
			if (csm.getEnableCreatorName())
			{
				message.add("[C]: ");
				message.add(getCreator(i) + " ");
			}
			
			if (csm.getEnablePlayerTargetName())
			{
				message.add("[T]: ");
				message.add(getTarget(i) + " ");
			}
			
			message.add("[R]: ");
			message.add("&q" + getAmount(i) + "&q");
			
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
				
				message.add("[X]: ");
				message.add(String.valueOf(x) + " ");
				
				message.add("[Y]: ");
				message.add(String.valueOf(y) + " ");
				
				message.add("[Z]: ");
				message.add(String.valueOf(z));
			}
		}
		else if (contentLevel == 2)
		{
			if (csm.getEnableServerTargetName())
			{
				message.add("[T]: ");
				message.add(getTarget(i) + " ");
			}
			
			message.add("[R]: ");
			message.add("&q" + getAmount(i) + "&q");
			
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
				
				message.add("[X]: ");
				message.add(String.valueOf(x) + " ");
				
				message.add("[Y]: ");
				message.add(String.valueOf(y) + " ");
				
				message.add("[Z]: ");
				message.add(String.valueOf(z));
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
			bLib.standardBroadcast("&p" + csm.getLastBounty() + "&p Chickened Out!");
			
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
						bLib.standardBroadcast("Warning, The Server Bounty Has Dropped Due To Target Entering Blocked World.");
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
					msgLib.standardMessage("Warning, The Server Bounty Has Been Dropped Due To Insufficient Online Players.");
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
				
				try
				{
					if (perms.isUntargetable() == true)
						ignorePlayer = true;
				}
				catch (NullPointerException e)
				{
					ignorePlayer = true;
				}
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
		int serverBountyID = getServerBountyID();
		int amount = 0;
		String playerString = "";
		
		//If the server bounty id is -1, we return.
		if (serverBountyID == -1)
			return;
		
		//Calculate bounty worth.
		amount = getAmount(serverBountyID) + getPlayerCount() * csm.getTierBaseBonus(x) + csm.getTierBaseBonus(x);
		
		//Set the value of the bounty.
		setAmount(serverBountyID, amount);
		
		if (getTier(serverBountyID) == x - 1)
		{
			//If the plyaer name is enabled, then we want to display it.
			if (csm.getEnableServerTargetName())
				playerString = " For &p" + getTarget(serverBountyID) + "&p";
			
			bLib.standardBroadcast("The Server Bounty Tier " + String.valueOf(getTier(serverBountyID) + 1) + playerString + " Has Increased! New Value: &q" + amount + "&q!");
		}
		
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
			perms = new FC_BountiesPermissions(player);
			
			if (ignoreExempt == true)
			{
				if (perms.permissionsEnabled() == true)
				{
					if (perms.isUntargetable() == false)
						count = count + 1;
				}
			}
			else
			{
				playerManager = new PlayerManager(player.getName());
				playerManager.checkPlayerData();

				if (perms.permissionsEnabled() == true)
				{
					if (perms.isUntargetable() == false && playerManager.getExempt() == false)
						count = count + 1;
				}
				else
				{
					if (playerManager.getExempt() == false)
						count = count + 1;
				}
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
		//Variable Declarations
		TopKillersBoard tkb = new TopKillersBoard();
		String name = killer.getName();
		PlayerManager playerManager = new PlayerManager(name);
		String broadcastString = "&p" + name + "&p Has Killed &p" + killedName + "&p To Win &q" + amount + "&q";
		double percentAmount;
		
		//If the current bounty is the server bounty, then...
		if (bountyID == getServerBountyID())
		{
			//If there is a flat amount of money to reward, calculate it and announce.
			if (csm.getKillerBonusAmount() > 0)
			{
				broadcastString = broadcastString + " And The Kill Bonus Of &q" + csm.getKillerBonusAmount() + "&q";
				
				//Add configurable bonus
				amount = amount + csm.getKillerBonusAmount();
			}
			
			//If there is a percent amount to reward, calculate it and announce.
			if (csm.getKillerBonusPercent() > 0) 
			{
				percentAmount = getPercent(amount, csm.getKillerBonusPercent());
				
				broadcastString = broadcastString + " And The Kill Bonus Of &q" + String.valueOf(percentAmount) + "&q";
				
				//Add configurable bonus
				amount = amount + percentAmount;
			}
		}
		
		//Broadcast the message.
		bLib.standardBroadcast(broadcastString + "!");
		
		if (csm.getBountyDeathPercent() > 0)
		{
			percentAmount = getPercent(csm.getBountyDeathPercent(), FC_Bounties.economy.getBalance(killedName));
			
			FC_Bounties.economy.withdrawPlayer(killedName, percentAmount);
			
			FC_Bounties.logFile.logMoneyTransaction("[Death %] Withdrawing: " + killedName + " / Amount: " + percentAmount);
		}
		
		if (csm.getBountyStealPercent() > 0)
		{
			percentAmount = getPercent(csm.getBountyStealPercent(), FC_Bounties.economy.getBalance(killedName));
			
			FC_Bounties.economy.withdrawPlayer(killedName, percentAmount);
			FC_Bounties.economy.depositPlayer(killer.getName(), percentAmount);
			
			if (csm.getEnableMoneyLogging() == true)
			{
				FC_Bounties.logFile.logMoneyTransaction("[Steal %] Withdrawing: " + killedName + " / Amount: " + percentAmount);
				FC_Bounties.logFile.logMoneyTransaction("[Steal %] Depositing: " + killer.getName() + " / Amount: " + percentAmount);
			}
		}
		
		FC_Bounties.logFile.logMoneyTransaction("[Bounty Kill Reward] Depositing: " + name + " / Amount: " + amount);
		
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
		String broadcastString = "&p" + name + "&p Has Survived To Win &q" + amount + "&q";
		
		if (csm.getSurvivalBonusAmount() > 0)
		{
			broadcastString = broadcastString + " And The Survival Bonus Of &q" + csm.getSurvivalBonusAmount() + "&q";
			
			//Add survival flat bonus
			amount = amount + csm.getSurvivalBonusAmount();
		}
		//Create a percent amount if the survival bonus isn't a flat amount.
		if (csm.getSurvivalBonusPercent() > 0)
		{
			percentAmount = getPercent(csm.getSurvivalBonusPercent(),amount);
			
			broadcastString = broadcastString + " And The Survival Bonus Of &q" + String.valueOf(percentAmount) + "&q";
			
			//Add survival percent bonus.
			amount = amount + percentAmount;
		}
		
		bLib.standardBroadcast(broadcastString + "!");
		
		//Log the event
		FC_Bounties.logFile.logMoneyTransaction("[Survivor Reward] Depositing: " + name + " / Amount: " + amount);
		
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
	
	public double getPlayerWorth(String name)
	{
		double totalWorth = 0;
		
		for (int i = 0; i < FC_Bounties.MAX_BOUNTIES; i++)
		{
			if (getTarget(i) != null)
			{
				if (getTarget(i).equals(name))
				{
					totalWorth += getAmount(i);
				}
			}
		}
		
		return totalWorth;
	}
	
	//Returns x% of y.
	private double getPercent(double x, double y)
	{
		double percentAmount;
		
		percentAmount = x * y * .01;
		
		return percentAmount;
	}
}






