package com.acidnom.yellrage;

public class AudioChunkMarker {
	
	private int position;
	
	public AudioChunkMarker()
	{
		
	}
	
	public AudioChunkMarker(int pos)
	{
		position = pos;
	}
	
	public void createChunk(int pos)
	{
		position = pos;
	}

	public int getPosition() {
		return position;
	}
	
	

}