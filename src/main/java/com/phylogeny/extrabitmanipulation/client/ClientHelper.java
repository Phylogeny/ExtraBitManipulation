package com.phylogeny.extrabitmanipulation.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;

public class ClientHelper
{
	
	public static IThreadListener getThreadListener()
	{
		return Minecraft.getMinecraft();
	}
	
	public static World getWorld()
	{
		return Minecraft.getMinecraft().world;
	}
	
	public static EntityPlayer getPlayer()
	{
		return Minecraft.getMinecraft().player;
	}
	
}