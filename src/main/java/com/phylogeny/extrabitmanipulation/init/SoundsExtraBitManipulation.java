package com.phylogeny.extrabitmanipulation.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.phylogeny.extrabitmanipulation.reference.Reference;

public class SoundsExtraBitManipulation
{
	public static SoundEvent boxCheck, boxUncheck;
	
	public static void registerSounds()
	{
		boxCheck = registerSound("box_check");
		boxUncheck = registerSound("box_uncheck");
	}
	
	private static SoundEvent registerSound(String soundName)
	{
		ResourceLocation soundNameResLoc = new ResourceLocation(Reference.MOD_ID + ":" + soundName);
		return GameRegistry.register(new SoundEvent(soundNameResLoc).setRegistryName(soundNameResLoc));
	}
	
	public static void playSound(SoundEvent sound)
	{
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(sound, 1.0F));
	}
	
}