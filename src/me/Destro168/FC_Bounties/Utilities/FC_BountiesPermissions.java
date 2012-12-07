package me.Destro168.FC_Bounties.Utilities;

import me.Destro168.FC_Suite_Shared.PermissionManager;

import org.bukkit.entity.Player;

public class FC_BountiesPermissions extends PermissionManager
{
	public FC_BountiesPermissions(Player player)
	{ 
		super(player);
	}
	
	public FC_BountiesPermissions(boolean isConsole)
	{
		super(isConsole);
	}
	
	public boolean isAdmin()
	{
		if (isGlobalAdmin() == true)
			return true;
		
		if (permission.has(player, "FC_Bounties.admin"))
			return true;
		
		return isConsole;
	}
	
	public boolean isUntargetable()
	{
		if (permission.has(player, "FC_Bounties.unTargetable"))
			return true;
		
		return isConsole;
	}
	
	public boolean commandCreate()
	{
		if (isAdmin() == true)
			return true;
		
		if (permission.has(player, "FC_Bounties.command.create"))
			return true;
		
		return isConsole;
	}
	
	public boolean commandRemove()
	{
		if (isAdmin() == true)
			return true;
		
		if (permission.has(player, "FC_Bounties.command.remove"))
			return true;
		
		return isConsole;
	}
	
	public boolean commandList()
	{
		if (isAdmin() == true)
			return true;
		
		if (permission.has(player, "FC_Bounties.command.list"))
			return true;
		
		return isConsole;
	}
	
	public boolean commandDrop()
	{
		if (isAdmin() == true)
			return true;
		
		if (permission.has(player, "FC_Bounties.command.drop"))
			return true;
		
		return isConsole;
	}
	
	public boolean commandExempt()
	{
		if (isAdmin() == true)
			return true;
		
		if (permission.has(player, "FC_Bounties.command.exempt"))
			return true;
		
		return isConsole;
	}
	
	public boolean commandTop()
	{
		if (isAdmin() == true)
			return true;
		
		if (permission.has(player, "FC_Bounties.command.top"))
			return true;
		
		return isConsole;
	}
}





