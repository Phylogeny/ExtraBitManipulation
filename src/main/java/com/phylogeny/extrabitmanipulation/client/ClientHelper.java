package com.phylogeny.extrabitmanipulation.client;

import com.phylogeny.extrabitmanipulation.init.KeyBindingsExtraBitManipulation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
		return Minecraft.getMinecraft().theWorld;
	}
	
	public static EntityPlayer getPlayer()
	{
		return Minecraft.getMinecraft().thePlayer;
	}
	
	public static ItemStack getHeldItemMainhand()
	{
		return getPlayer().getHeldItemMainhand();
	}
	
	public static KeyBinding getChiselsAndBitsMenuKeyBind()
	{
		for (KeyBinding keyBind : Minecraft.getMinecraft().gameSettings.keyBindings)
		{
			if (keyBind.getKeyDescription().equals("mod.chiselsandbits.other.mode"))
				return keyBind;
		}
		return null;
	}
	
	public static boolean isChiselsAndBitsMenuKeyBindPressed()
	{
		KeyBinding keyBind = getChiselsAndBitsMenuKeyBind();
		return keyBind == null ? false : KeyBindingsExtraBitManipulation.isKeyDown(keyBind);
	}
	
}