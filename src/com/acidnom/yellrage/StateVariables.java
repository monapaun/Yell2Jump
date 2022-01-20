package com.acidnom.yellrage;

public class StateVariables {
	
	public float player_x;					// player stuff
	public float player_y;
	public float player_velocity;
	public int player_life;
	public boolean player_isalive;
	public boolean player_isinair;
	
	public boolean[] wheel_existence;		// wheel stuff
	public float[] wheel_x;
	public float[] wheel_y;
	public boolean[] wheel_hasbeenclicked;
	public boolean[] wheel_isready;
	
	public boolean[] wall_existence;		// wall stuff
	public float[] wall_x;
	public float[] wall_y;
	public int wall_velocity;				
	
	public int world_secondselapsed;		// world stuff
	public int world_q;						// holder for wheel iterator
	public int world_k;						// holder for wall iterator
	
	private boolean isSet;
	
	public StateVariables()
	{
		isSet = false;
	}
	
	public boolean isSet()
	{
		return isSet;
	}
	
	public void set()
	{
		isSet = true;
	}
	
	public void unset()
	{
		isSet = false;
	}

}
