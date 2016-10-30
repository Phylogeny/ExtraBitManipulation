package com.phylogeny.extrabitmanipulation.init;

import com.phylogeny.extrabitmanipulation.reference.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class SoundsExtraBitManipulation
{
	public static ResourceLocation boxCheck, boxUncheck;
	
	public static void registerSounds()
	{
		boxCheck = registerSound("box_check");
		boxUncheck = registerSound("box_uncheck");
	}
	
	private static ResourceLocation registerSound(String soundName)
	{
		return new ResourceLocation(Reference.MOD_ID + ":" + soundName);
	}
	
	public static void playSound(ResourceLocation sound)
	{
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(sound, 1.0F));
	}
	
}