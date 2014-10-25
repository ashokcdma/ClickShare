package com.xchanging.settings;

import java.util.ArrayList;

import android.content.Context;

import com.debasmita.clickshare.FrogUtility;
import com.google.gson.Gson;

public class AlbumArrayList extends ArrayList<AlbumNode>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//ArrayList albumList;
	public AlbumArrayList()
	{
		//albumList = new ArrayList<AlbumNode>();
	}

	
	public CharSequence[] toArray()
	{
		CharSequence[] list = new CharSequence[this.size()];
		for (int i=0, j=this.size(); i < j; i++)
		{
			AlbumNode node = this.get(i);
			//allocate the index. its the proper time to sync the arraylist and array index.
			node.setIndex(i);
			list[i] = node.getAlbumName();
		}
		return list;
	}
	
	public void save(Context context)
	{
		//convert to perference string and store.
		Gson gson = new Gson();
		new FrogUtility(context).setAlbumNodeList( gson.toJson(this));
	}
	//call it in preference settings oncreate.
	public static AlbumArrayList getAlbumListFromPref(Context context)
	{
		Gson gson = new Gson();
		AlbumArrayList obj = 
				(AlbumArrayList) gson.fromJson(new FrogUtility(context).getAlbumNodeListFromPref(), AlbumArrayList.class);
		return obj;
	}
}
