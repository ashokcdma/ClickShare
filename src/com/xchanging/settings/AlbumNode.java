package com.xchanging.settings;

public class AlbumNode {
	String albumName;
	String albumId;
	int index;
	
	public AlbumNode (String albumName,	String albumId)
	{
		this.albumName = albumName;
		this.albumId = albumId;
	}
	
	public String getAlbumName()
	{
		return this.albumName;
	}
	
	public String getAlbumId()
	{
		return this.albumId;
	}
	
	public void setIndex(int index)
	{
		this.index = index;
	}
}
