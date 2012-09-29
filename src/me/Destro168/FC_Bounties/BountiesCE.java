package me.Destro168.FC_Bounties;

import java.util.Date;
import java.util.List;

import me.Destro168.ConfigManagers.ConfigManager;
import me.Destro168.FC_Suite_Shared.ArgParser;
import me.Destro168.Messaging.MessageLib;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import utilities.ConfigSettingsManager;
import utilities.FC_BountiesPermissions;

public class BountiesCE implements CommandExecutor
{
	private static Economy economy = null;
	
	BountyManager bountyHandler;
	Player playerSender;
	ConfigSettingsManager csm = new ConfigSettingsManager();
	MessageLib msgLib;
	PlayerManager playerManager;
	FC_BountiesPermissions perms;
	
	public BountiesCE(BountyManager bountyHandler_, Economy economy_) 
	{
		bountyHandler = bountyHandler_;
		economy = economy_;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args2)
    {
		playerSender = (Player) sender;
		perms = new FC_BountiesPermissions(playerSender);
		playerManager = new PlayerManager(playerSender.getName());
		
		ArgParser fap = new ArgParser(args2);
		msgLib = new MessageLib(playerSender);
		ConfigManager cm = new ConfigManager();
		
		Date now;
		String removeMessage;
		
		String[] args = fap.getArgs();
		int[] intArgs = new int[50];
		double difference = 0;
		boolean isExempt = false;
		boolean cont = true;
		long timeDifference;
		final int range = 15;
		int playerBountyCount = 0;
		int count = 0;
		double bountyCost;
		
		if (args.equals(""))
		{
			messagePlayerHelp(false);
			return true;
		}
		
		//Based on arguments, evaluate.
		if (args[0].equalsIgnoreCase("create"))
		{
			if (perms.commandCreate() == false)
				return msgLib.errorNoPermission();
			
			try
			{
				intArgs[2] = Integer.valueOf(args[2]);
			}
			catch (NumberFormatException e)
			{
				return msgLib.errorInvalidCommand();
			}
			
			//We count how many bounties a player has.
			for (int i = 0; i < FC_Bounties.MAX_BOUNTIES; i++)
			{
				if (bountyHandler.getCreator(i) != null)
				{
					if (bountyHandler.getCreator(i).equals(playerSender.getName()))
						playerBountyCount++;
				}
			}
			
			if (playerBountyCount > csm.getMaximumBountiesPerPlayer())
			{
				msgLib.standardMessage("You have already created 20 bounties. You must remove old bounties or wait for them to expire.");
				cont = false;
			}
			
			//Ensure the bounty entered is greater or equal to the minimum bounty value.
			if (intArgs[2] < csm.getMinimumBountyValue())
			{
				msgLib.standardMessage("You must ceate bounties worth $" + csm.getMinimumBountyValue() + " or more.");
				cont = false;
			}
			
			//Set how much the bounty cost is based on the tax percent.
			bountyCost = intArgs[2] + intArgs[2] * csm.getBountyCreationTaxPercent() * .01;
			
			//Only let players make the bounty if they can afford it.
			difference = economy.getBalance(playerSender.getName()) - bountyCost;
			
			if (difference < 0)
			{
				msgLib.standardMessage("You can't afford to create a bounty with that reward.");
				cont = false;
			}
			
			if (args[1].equals("[SERVER]"))
			{
				msgLib.standardMessage("[SERVER] is reserved and not a player name.");
				cont = false;
			}
			
			if (cont == true)
			{
				//Create empty location
				Location none = new Location(playerSender.getWorld(), 0,0,0);
				
				//Create the new bounty.
				bountyHandler.addNewBounty(playerSender.getName(), args[1], intArgs[2], none);
				
				//Charge money
				economy.withdrawPlayer(playerSender.getName(), bountyCost);
				
				if (csm.getAnnouncePlayerBountyCreation())
				{
					msgLib.standardBroadcast(playerSender.getName() + " created bounty to kill " + args[1] + " for " + msgLib.getFormattedMoney(intArgs[2], cm.primaryColor));
				}
				else
				{
					try
					{
						msgLib.standardMessage("Created bounty to kill " + bountyHandler.getTarget(intArgs[1]) + " for " + "$" + intArgs[2]);
					}
					catch (NumberFormatException e)
					{
						return msgLib.errorInvalidCommand();
					}
				}
				
				return msgLib.successCommand();
			}
			else
			{
				return true;
			}
		}
		
		//We remove the bounty by checking to see if the person attempting to remove the
		//bounty is the creator. Then we simply set the slot there to "".
		else if (args[0].equalsIgnoreCase("remove"))
		{
			if (perms.commandRemove() == false)
				return msgLib.errorNoPermission();
			
			if (args[1].equals(""))
				return msgLib.errorInvalidCommand();
			
			try
			{
				intArgs[1] = Integer.valueOf(args[1]);
			}
			catch (NumberFormatException e)
			{
				return msgLib.errorInvalidCommand();
			}
			
			//Make sure the enter a bounty to remove in a valid range.
			if (intArgs[1] > -1 && intArgs[1] <= FC_Bounties.MAX_BOUNTIES)
			{
				//Set the remove message
				removeMessage = "The bounty for " + bountyHandler.getTarget(intArgs[1]) + " has been removed and refunded.";
				
				//If the player is the creator or they are an admin
				if (playerSender.getName().equalsIgnoreCase(bountyHandler.getCreator(intArgs[1])) || perms.isAdmin())
				{
					//Refund money
					economy.bankDeposit(playerSender.getName(), bountyHandler.getAmount(intArgs[1]));
					
					//Delete the bounty.
					bountyHandler.deleteBounty(intArgs[1]);
				}
				else
				{
					msgLib.standardMessage("You can't remove this bounty because you did not create it!");
					return false;
				}
				
				//Display the bounty is removed if you announce bounty creation.
				if (csm.getAnnouncePlayerBountyCreation() == true)
					msgLib.standardBroadcast(removeMessage);
				else
					playerSender.sendMessage(removeMessage);
				
				return true;
			}
			else
			{
				msgLib.standardMessage("You must enter a number within the range of -1 to " + FC_Bounties.MAX_BOUNTIES);
				return true;
			}
		}
		
		else if (args[0].equalsIgnoreCase("list"))
		{
			if (perms.commandList() == false)
				return msgLib.errorNoPermission();
			
			if (args[2].equals(""))
			{
				args[2] = "0";
				intArgs[2] = 0;
				cont = true;
			}
			
			try
			{
				intArgs[2] = Integer.valueOf(args[2]);
			}
			catch (NumberFormatException e)
			{
				intArgs[2] = 0;
			}
			
			if (intArgs[2] > -1 && intArgs[2] < FC_Bounties.MAX_BOUNTIES - range)
			{
				cont = true;
			}
			else
			{
				args[2] = "0";
				intArgs[2] = 0;
				cont = true;
			}
				
			if (cont == true)
			{
				if (fap.getArg(1).equalsIgnoreCase("all") || fap.getArg(1).equals(""))
				{
					msgLib.standardHeader("Listing all bounties: ");
					
					for (int i = intArgs[2]; i < intArgs[2] + range; i++)
					{
						if (bountyHandler.getActive(i) == true)
						{
							if (!(bountyHandler.getCreator(i).equalsIgnoreCase("[SERVER]")))
							{
								msgLib.standardMessage(String.valueOf(i) + ": " + bountyHandler.getInformation(i,1,cm.primaryColor));
								count++;
							}
							else
							{
								msgLib.standardMessage(String.valueOf(i) + ": [Server Bounty]: " + bountyHandler.getInformation(i,2,cm.primaryColor));
								
								if (perms.isAdmin())
									msgLib.standardMessage("Current Server Bounty Target: " + bountyHandler.getTarget(bountyHandler.getServerBountyID()));
								
								count++;
							}
						}
					}
				}
				else if (fap.getArg(1).equalsIgnoreCase("mine"))
				{
					msgLib.standardMessage("Listing your bounties: ");
					
					for (int i = intArgs[2]; i < intArgs[2] + range; i++)
					{
						if (bountyHandler.getActive(i) == true)
						{
							if (bountyHandler.getCreator(i) == playerSender.getName())
							{
								msgLib.standardMessage(String.valueOf(i) + ": " + bountyHandler.getInformation(i,1,cm.primaryColor));
								count++;
							}
						}
					}
				}
				else
				{
					cont = false;
					return msgLib.errorInvalidCommand();
				}
			}
			
			if (cont == true)
			{
				if (count == 0)
				{
					msgLib.standardMessage("This list you requested to view is empty.");
				}
				else
					msgLib.standardMessage("Finished Listing.");
				
				return true;
			}
		}
		else if (args[0].equalsIgnoreCase("drop"))
		{
			if (perms.commandDrop() == false)
				return msgLib.errorNoPermission();
			
			if (bountyHandler.getServerBountyID() > -1)
			{
				now = new Date();
				
				timeDifference = now.getTime() - bountyHandler.getDate(bountyHandler.getServerBountyID());
				
				if (timeDifference > csm.getTimeBeforeDrop() || perms.isAdmin())
				{
					if (bountyHandler.getTarget(bountyHandler.getServerBountyID()).equalsIgnoreCase(playerSender.getName()))
					{
						//Announce they chickened out.
						msgLib.standardBroadcast("The person with the server bounty chickened out!");
						
						//Delete the bounty
						bountyHandler.deleteBounty(bountyHandler.getServerBountyID());
					}
					else
					{
						msgLib.standardMessage("You can't drop the bounty because you are not the target.");
					}
				}
				else
				{
					msgLib.standardMessage("Server bounties must be [" + csm.getTimeBeforeDrop() * .001 + "] seconds old before they can be dropped.");
				}
			}
			else
			{
				msgLib.standardMessage("There is no server bounty currently.");
			}
		}
		else if (args[0].equalsIgnoreCase("exempt"))
		{
			if (perms.commandExempt() == false)
				return msgLib.errorNoPermission();
			
			if (args[1].equals(""))
			{
				msgLib.standardMessage("Your exemption mode will be toggled!");
				
				args[1] = "toggle";
			}
			
			//Create a new record if a record doesn't exist.
			playerManager.checkPlayerData();
			
			//Store where player record is at.
			isExempt = playerManager.getExempt();
			
			//If the user wants to toggle, toggle the bounty exempt switch.
			if (args[1].equalsIgnoreCase("toggle"))
			{
				if (isExempt == true)
				{
					playerManager.setExempt(false);
					msgLib.standardMessage("Successfully made you targetable to bounties!");
				}
				else
				{
					playerManager.setExempt(true);
					msgLib.standardMessage("Successfully made you exempt to bounties!");
				}
			}
			
			//Turn the bounty on
			else if (args[1].equalsIgnoreCase("on"))
			{
				if (isExempt == true)
				{
					msgLib.standardMessage("You already are exempt from bounties.");
				}
				else
				{
					playerManager.setExempt(true);
					msgLib.standardMessage("Successfully made you exempt to bounties!");
				}
			}
			
			//Turn the bounty off
			else if (args[1].equalsIgnoreCase("off"))
			{
				if (isExempt == true)
				{
					playerManager.setExempt(false);
					msgLib.standardMessage("Successfully made you targetable to bounties.");
				}
				else
				{
					msgLib.standardMessage("You are already targetable by bounties!");
				}
			}
		}
		
		else if (args[0].equalsIgnoreCase("top"))
		{
			if (perms.commandTop() == false)
				return msgLib.errorNoPermission();
			
			sendTopKillersBoard();
			playerSender.sendMessage("");
			sendTopSurvivalBoard();
		}
		
		else if (args[0].equalsIgnoreCase("admin"))
		{
			if (perms.isAdmin() == false)
				return msgLib.errorNoPermission();
			
			if (args[1].equalsIgnoreCase("generate"))
			{
				count = bountyHandler.getPlayerCount();
				
				if (bountyHandler.getServerBountyID() == -1)
				{
					if (args[2].equals(""))
						args[2] = "random";
					else if (Bukkit.getServer().getPlayer(args[2]) != null)
					{
						if (Bukkit.getServer().getPlayer(args[2]).isOnline() == false)
						{
							args[2] = "random";
							msgLib.standardMessage("Player is not online so defaulting to random.");
						}
					}
					else
					{
						args[2] = "random";
						msgLib.standardMessage("Unable to detect player. Defaulting to random.");
					}
					
					if (args[2].equalsIgnoreCase("random"))
					{
						Player randomPlayer = bountyHandler.getRandomServerBountyTarget();
						
						if (randomPlayer == null)
						{
							msgLib.standardMessage("Not enough players online to generate a random bounty!");
							return true;
						}
						else
						{
							bountyHandler.generateServerBounty(randomPlayer);
							msgLib.standardMessage("Successfully generated a new random server bounty!");
						}
					}
					else
					{
						bountyHandler.generateServerBounty(Bukkit.getServer().getPlayer(args[2]));
						msgLib.standardMessage("Successfully generated a new server bounty for target " + Bukkit.getServer().getPlayer(args[2]).getName() + "!");
					}
				}
			}
			
			else if (args[1].equalsIgnoreCase("iterate"))
			{
				bountyHandler.manageServerBounty();
				msgLib.standardMessage("Successfully iterated through the server bounty manager.");
			}
			
			else if (args[1].equalsIgnoreCase("required"))
			{
				if (args[2].equals(""))
					return true;
				
				try
				{
					csm.setRequiredPlayers(Integer.valueOf(args[2]));
				}
				catch (NumberFormatException e)
				{
					return msgLib.errorInvalidCommand();
				}
				
				return msgLib.successCommand();
			}
			
			else if (args[1].equalsIgnoreCase("edit"))
			{
				//  /bounty edit [bounty] [feature] [newvalue]
				if (args[2].equals(""))
				{
					cont = false;
				}
				
				if (args[3].equals(""))
				{
					cont = false;
				}
				
				if (args[4].equals(""))
				{
					cont = false;
				}
				
				try
				{
					intArgs[2] = Integer.valueOf(args[2]);
					intArgs[4] = Integer.valueOf(args[4]);
				}
				catch (NumberFormatException e)
				{
					return msgLib.errorInvalidCommand();
				}
				
				if (cont == false)
				{
					playerSender.sendMessage("You need to enter addition arguments.");
					return true;
				}
				else
				{
					if (bountyHandler.getCreator(intArgs[2]) != null)
					{
						if (args[3].equalsIgnoreCase("value"))
						{
							//Have to get the bounty by name, code new method.
							bountyHandler.setAmount(bountyHandler.getBountyByName(args[2]), intArgs[4]);
							msgLib.standardMessage("Successfully modified bounty value.");
						}
					}
				}
			}
			else if (args[1].equalsIgnoreCase("toggle"))
			{
				if (csm.getBountyGeneration())
				{
					csm.setBountyGeneration(false);
					msgLib.standardMessage("Turned bounty generate off.");
				}
				else
				{
					csm.setBountyGeneration(true);
					msgLib.standardMessage("Turned bounty generate on.");
				}
			}
			else if (args[1].equalsIgnoreCase("ignoreWorlds"))
			{
				ArgParser ap = new ArgParser(args);
				ap.setLastArg(2);
				csm.setIgnoreWorlds(ap.getFinalArgList());
			}
			else if (args[1].equalsIgnoreCase("sb"))
			{
				msgLib.standardHeader("Server Bounty Information");
				
				if (bountyHandler.getServerBountyID() > -1)
					msgLib.standardMessage("Current Server Bounty Target: " + bountyHandler.getTarget(bountyHandler.getServerBountyID()));
				else
					msgLib.standardMessage("No Server Bounty Currently");
				
				count = bountyHandler.getPlayerCount();
				
				msgLib.standardMessage("Potential Candidates for a bounty online: " +
						String.valueOf(count) + " Needed Amount: " + csm.getRequiredPlayers());
			}
			else
				return messagePlayerHelp(true);
		}
		else
		{
			return messagePlayerHelp(false);
		}
		
		return true;
	}
	
	
	public boolean messagePlayerHelp(boolean isPage2)
	{
		//Send the help messages to the player.
		msgLib.standardHeader("FC_Bounties Help");
		
		if (isPage2 == false)
		{
			msgLib.standardMessage("/bounty","Show non-admin help.");

			//Begin showing commands if the person has permission to use them.
			if (perms.commandCreate())
				msgLib.standardMessage("/bounty create [name] [reward]","Create a bounty.");
			
			if (perms.commandRemove())
				msgLib.standardMessage("/bounty remove [bountyNumber]","Remove a bounty.");
			
			if (perms.commandList())
				msgLib.standardMessage("/bounty list [all,mine] [from starting point]","List bounties");
			
			if (perms.commandDrop())
				msgLib.standardMessage("/bounty drop","Removes server bounty from self.");
			
			if (perms.commandExempt())
				msgLib.standardMessage("/bounty exempt [toggle,on,off]","Changes if server will put random bounties on you or not.");
			
			if (perms.commandTop())
				msgLib.standardMessage("/bounty top","Displays leaderboards");
			
			if (perms.isAdmin())
				msgLib.standardMessage("/bounty admin","Shows admin commands and some useful information.");
		}
		else
		{
			//If the player is an op, then we want to send admin commands to him/her.
			if (perms.isAdmin())
			{
				msgLib.standardMessage("/bounty admin sb","See information related to server bounties.");
				msgLib.standardMessage("/bounty admin generate [name]","Generate a bounty on a given player. Also accepts 'random' as a name.");
				msgLib.standardMessage("/bounty admin toggle","Toggle whether server bounties should be enabled or disabled.");
				msgLib.standardMessage("/bounty admin iterate","Perform a server bounty iteration");
				msgLib.standardMessage("/bounty admin edit [bounty number] [value] [amount]","Change value of a bounty");
				msgLib.standardMessage("/bounty admin ignoreWorlds [world1] [world2] [...]","Set the list of worlds to disable bounties in.");
				msgLib.standardMessage("/bounty admin required [num]","Change number of players that must be online for server bounties to generate.");
			}
		}
		
		//Always return true.
		return true;
	}
	
	public void sendTopKillersBoard()
	{
		//Variable Declarations
		boolean oneListed = false;
		
		//Display that information to the player.
		msgLib.standardMessage("Top Killers:");
		
		//Create the leaderboard classes
		TopKillersBoard tkb = new TopKillersBoard();
		
		List<String> names = tkb.getTopKillersNames();
		List<Integer> counts = tkb.getTopKillersCounts();
		
		for (int j = names.size() - 1; j > names.size() - 6; j--)
		{
			if (counts.get(j) > 0)
			{
				msgLib.standardMessage(names.get(j) + " at " + counts.get(j));
				oneListed = true;
			}
			else
			{
				msgLib.standardMessage("[None]!");
			}
		}
		
		if (oneListed == false)
		{
			msgLib.standardMessage("There are no killers yet!");
		}
		else
			msgLib.standardMessage("Finished listing killers.");
	}
	
	public void sendTopSurvivalBoard()
	{
		//Variable Declarations
		boolean oneListed = false;
		
		//Display that information to the player.
		TopSurvivorsBoard tsb = new TopSurvivorsBoard();
		
		List<String> names = tsb.getTopSurvivorsNames();
		List<Integer> counts = tsb.getTopSurvivorsCounts();
		
		for (int j = names.size() - 1; j > names.size() - 6; j--)
		{
			if (counts.get(j) > 0)
			{
				msgLib.standardMessage(names.get(j) + " at " + counts.get(j));
				oneListed = true;
			}
			else
			{
				msgLib.standardMessage("[None]!");
			}
		}
		
		if (oneListed == false)
		{
			msgLib.standardMessage("There are no survivors yet!");
		}
		else
			msgLib.standardMessage("Finished listing survivors.");
	}
}












