package com.phylogeny.extrabitmanipulation.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.Vec3d;
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
	
	public static ItemStack getHeldItemMainhand()
	{
		return getPlayer().getHeldItemMainhand();
	}
	
	public static void spawnParticle(World world, Vec3d particlePos, IParticleFactory particleFactory)
	{
		Minecraft.getMinecraft().effectRenderer.addEffect(particleFactory.createParticle(0, world, particlePos.x, particlePos.y, particlePos.z, 0, 0, 0));
	}
	
}