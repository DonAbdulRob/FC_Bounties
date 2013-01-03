package me.Destro168.FC_Bounties;

import java.util.ArrayList;
import java.util.List;

import me.Destro168.FC_Bounties.Utilities.BountyHistory;
import me.Destro168.FC_Bounties.Utilities.BountyLogFile;
import me.Destro168.FC_Bounties.Utilities.ConfigSettingsManager;
import me.Destro168.FC_Suite_Shared.AutoUpdate;
import me.Destro168.FC_Suite_Shared.Messaging.MessageLib;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.plugin.Plugin;

public class FC_Bounties extends JavaPlugin
{
	//Variables
    Plugin worldGuard;
    BountyManager bountyHandler;
    int count = 0;
	
	public static FC_Bounties plugin;
    public static Economy economy;
    public static BountyLogFile logFile;
	public static int[] tid = new int[3];
	public static int MAX_PLAYER_ENTIRES = 50000;
	public static int MAX_BOUNTIES = 5000;
	public static List<BountyHistory> bountyHistoryList;
	
    private BountiesCE myExecutor;
	private ConfigSettingsManager csm;
	public static int debugCounter = 0;
    
	@Override
	public void onDisable()
	{
		plugin.getLogger().info("Disabled Successfully");
	}
	
	@Override
	public void onEnable()
	{
		//Store plugin.
		plugin = this;
		
		//Enable the server economy.
		setupEconomy();
		
		//Handle variable assignments
		bountyHandler = new BountyManager();
		csm = new ConfigSettingsManager();
		logFile = new BountyLogFile(FC_Bounties.plugin.getDataFolder().getAbsolutePath());
		
		//Update the configuration file.
		csm.handleConfiguration();
		
		//Register listeners
		getServer().getPluginManager().registerEvents(new invulnerabilityListener(), this);
		getServer().getPluginManager().registerEvents(new deathListener(), this);
		getServer().getPluginManager().registerEvents(new commandListener(), this);
		
		//Add onCommand.
		myExecutor = new BountiesCE(bountyHandler);
		getCommand("bounty").setExecutor(myExecutor);
		
		//Add listeners.
		getServer().getPluginManager().registerEvents(new commandListener(), this);
		
		//Find all old bounties and set them to expired. Make this a task that runs every 6 hours.
		tid[0] = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() 
		{
			@Override
			public void run() {
				bountyHandler.purgeOldBounties();
			}
		}, 0, 432000); //21600 = 6 hours. * 20
		
		//Start running automatic server bounties.
		tid[1] = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() 
		{
			@Override
			public void run() {
				bountyHandler.manageServerBounty();
			}
		}, 0, csm.getBountyIntervalLength() * 20); //Run every 60 seconds.
		
		try {
			new AutoUpdate(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (csm.getEnableRandomCoordinates())
			bountyHistoryList = new ArrayList<BountyHistory>();
		
		//Tell the user that the plugin successfully enabled.
		plugin.getLogger().info("Enabled Successfully.");
	}
	
	//Blocks chat commands except /bounty leave
	public class commandListener implements Listener
	{
		@EventHandler
		public void onChat(PlayerCommandPreprocessEvent event)
		{
			//Variable Declarations
			MessageLib msgLib = new MessageLib(event.getPlayer());
			boolean disable = true;
			double cost = csm.getBlockedCommandUseCost();
			
			if (cost > 0)
			{
				if (bountyHandler.hasServerBounty(event.getPlayer().getName()) == true)
				{
					if (event.getMessage().contains("bounty"))
					{
						disable = false;
					}
					else if (event.getMessage().contains("f home"))
					{
						disable = false;
					}
					
					if (disable == true)
					{
						msgLib.standardMessage("Sorry but you don't use commands while you have a server bounty on your head.");
						msgLib.standardMessage("You can drop the bounty with /bounty drop but you will not win the survival bonus.");
						msgLib.standardMessage("Also, commands take &q" + cost + "&q from you every time you try to use one!");
						msgLib.standardMessage("Finally, you have to see this giant wall of text. Who wants to see this? I mean common, just don't use commands!");
						
						economy.withdrawPlayer(event.getPlayer().getName(), cost);
						
						if (csm.getEnableMoneyLogging() == true)
							plugin.getLogger().info("[Command With SB] Withdrawing: " + event.getPlayer().getName() + " / Amount: " + cost);
						
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	//Invulnerability with no items against player combat.
	public class invulnerabilityListener implements Listener
	{
		@EventHandler(priority = EventPriority.LOW)
		public void onEntityDamage(EntityDamageByEntityEvent event)
		{
			//Variable declarations
			boolean checkInventories;
			Player defender;
			Player damager;
			
			if(!(event.getEntity() instanceof Player))
	            return;
			
			if (!(event.getDamager() instanceof Player))
				return;
			
			defender = (Player) event.getEntity();
			damager = (Player) event.getDamager();
			
			//Handles the invulnerability for when players have no items. - If they have no items they are invulnerable.
			if (csm.getInvulnerabilityEnabled())
			{
				//Set to check inventories by default.
				checkInventories = true;
				
				//We dont' want to check inventories for invulnerability if the defender is the server bounty.
				if (bountyHandler.getServerBountyID() > -1)
				{
					if (bountyHandler.getTarget(bountyHandler.getServerBountyID()).equalsIgnoreCase(defender.getName()))
						checkInventories = false;
				}
				
				if (csm.getIgnoreWorlds().contains(defender.getWorld().getName()))
					return;
				
				if (checkInventories == true)
				{
					if (playerHasEmptyInventory(defender) || playerHasEmptyInventory(damager))
					{
						event.setCancelled(true);
						return;
					}
				}
			}
		}
		
		@EventHandler(priority = EventPriority.HIGH)
		public void pvpCheck(EntityDamageByEntityEvent event)
		{
			Player defender;
			
			if(!(event.getEntity() instanceof Player))
	            return;
			
			//Set the defender.
			defender = (Player) event.getEntity();
			
			//Handle full bypass.
			if (csm.getForcePvp() == true)
			{
				if (bountyHandler.getServerBountyID() == -1)
					return;
				
				if (bountyHandler.getTarget(bountyHandler.getServerBountyID()) == null)
					return;
				
				if (bountyHandler.getTarget(bountyHandler.getServerBountyID()).equalsIgnoreCase(defender.getName()))
					event.setCancelled(false);
			}
		}
		
		private boolean playerHasEmptyInventory(Player player)
		{
			for (ItemStack item: player.getInventory())
			{
				if (item != null)
					return false;
			}
			
			return true;
		}
	}
	
	public class deathListener implements Listener
	{
		@EventHandler(priority = EventPriority.HIGHEST)
		public void onEntityDeath(EntityDeathEvent event)
		{
			Player damager = null;
			Player victim = null;
			EntityDamageByEntityEvent e;
			
			//If the entity isn't a player return.
			if (event.getEntity() instanceof Player)
			{
				victim = (Player) event.getEntity();
				
				if (victim.getLastDamageCause() == null)
				{
					FC_Bounties.plugin.getLogger().info("Null last damage cause.");
					return;
				}
			}
			else
				return;
			
			ConfigSettingsManager csm = new ConfigSettingsManager();
			List<String> ignoreKillWorlds = csm.getIgnoreKillWorlds();
			
			if (ignoreKillWorlds != null)
			{
				if (ignoreKillWorlds.contains(victim.getWorld().getName()))
					return;
			}
			
			if (victim.getLastDamageCause() instanceof EntityDamageByEntityEvent)
			{
				e = (EntityDamageByEntityEvent) victim.getLastDamageCause();
				
				if (e.getDamager() instanceof Player)
				{
					damager = (Player) e.getDamager();
				}
				else if (e.getDamager() instanceof Arrow)
				{
					Arrow arrow = (Arrow) e.getDamager();
					
					if (arrow.getShooter() instanceof Player)
					{
						damager = (Player) arrow.getShooter();
					}
				}
				else if (e.getDamager() instanceof Egg)
				{
					Egg egg = (Egg) e.getDamager();
					
					if (egg.getShooter() instanceof Player)
					{
						damager = (Player) egg.getShooter();
					}
				}
				else
					return;
			}
			else
				return;
			
			if (damager == null)
				return;
			
			//Go through all the bounties, if the player is one of the bounties, then give the bounty to that player.
			for (int i = 0; i < MAX_BOUNTIES; i++)
			{
				//If the player killed is equal to the bounty's target, reward the killer by
				//giving out the bounty. Then remove the bounty.
				if (victim.getName().equalsIgnoreCase(bountyHandler.getTarget(i)))
				{
					bountyHandler.rewardBountyKill(damager, victim.getName(), (double) bountyHandler.getAmount(i), i);
				}
			}
		}
	}
	
	//Vault function
	private Boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        
        return (economy != null);
    }
}


/*

//Bukkit.getServer().broadcastMessage(String.valueOf(counter));
//Bukkit.getServer().broadcastMessage(String.valueOf(serverBountyExists));
//Bukkit.getServer().broadcastMessage(String.valueOf(targetIsOnline));

//Bukkit.getServer().broadcastMessage(chosenPlayer.getName());

Description of bounties:

Feature 1: Plugin that allows players to place bounties on other players heads that can then be collected. 
Feature 2: Creates random bounties on players heads.

All bounties have an ID, a creator, and an amount associated
with them.

Feature 1:
Commands: 
/bounty create [name] [amount] command to put up a new bounty. An announcement is then made that the bounty has been placed to all servers.
/bounty remove [number] command to remove a bounty.
/bounty list user to view all the bounties created by players.

Note 1: Administrators can remove bounties if they would like if they have the FC_Bounties.admin permission.

The mechanics of featrue 1 in real-time:
A bounty is created. The information about the bounty is stored into a configuration file. Whenever a player dies a check is made to see if they had a bounty on them. If the player has a bounty on them then the bounty is removed and the player that scored the kill is given the bounty. The list command will simply display all of the bounties from the configuration file.

Feature 2:
Commands:
/bounty list random to view the current randomly placed bounty.

Mechanics of feature 2 in real-time:
If there are 5 people online, a random player is selected from who is online. A check is performed to see everybody who is in that persons faction. All people in the faction are stored into an "nobountyreward" section of the configuration file so as to prevent people in the faction from killing that person. Every minute that the person with the bounty on them lasts, the higher the bounty increases. Minimum bounty = 100, maximum bounty = 20k over 60 minutes. First 20 minutes = 100 bonus, second 20 minutes = 200 bonus per, and last 20 minutes = 300 bonus per, entire duration = 400 plus and it goes to the person with the bounty on them. Bounty is then ended and the cycle restarts.

//Faction check before rewarding bounties.
//
 
for (Player player: Bukkit.getServer().getOnlinePlayers())
		{
			if (player.hasPermission("FC_Bounties.viewDebug"))
			{
				player.sendMessage("~Debug Messages~");
				player.sendMessage(String.valueOf(serverBountyID));
				player.sendMessage(String.valueOf(createServerBounty));
				player.sendMessage(String.valueOf(createServerBounty2));
				//player.sendMessage("~Debug Messages~");
			}
		}
		
		saveConfig();
		
		
*/