package com.phylogeny.extrabitmanipulation.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.phylogeny.extrabitmanipulation.reference.Reference;

public class SoundsExtraBitManipulation
{
	public static SoundEvent boxCheck, boxUncheck;
	
	@SubscribeEvent
	public void registerSounds(RegistryEvent.Register<SoundEvent> event)
	{
		boxCheck = registerSound(event, "box_check");
		boxUncheck = registerSound(event, "box_uncheck");
	}
	
	private static SoundEvent registerSound(RegistryEvent.Register<SoundEvent> event, String soundName)
	{
		ResourceLocation soundNameResLoc = new ResourceLocation(Reference.MOD_ID + ":" + soundName);
		SoundEvent sound = new SoundEvent(soundNameResLoc).setRegistryName(soundNameResLoc);
		event.getRegistry().register(sound);
		return sound;
	}
	
	public static void playSound(SoundEvent sound)
	{
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(sound, 1.0F));
	}
	
}