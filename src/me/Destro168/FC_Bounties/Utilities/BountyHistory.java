package me.Destro168.FC_Bounties.Utilities;

public class BountyHistory
{
	public long refresh;
	public int i;
	public int x;
	public int y;
	public int z;
	
	public BountyHistory(int i_)
	{
		i = i_;
	}
	
	public boolean canRefresh()
	{
		if ((System.currentTimeMillis() - refresh) < 300000)
			return false;
		
		refresh = System.currentTimeMillis();
		return true;
	}
	
	public void updateLastCoordinates(int x_, int y_, int z_)
	{
		x = x_;
		y = y_;
		z = z_;
	}
}