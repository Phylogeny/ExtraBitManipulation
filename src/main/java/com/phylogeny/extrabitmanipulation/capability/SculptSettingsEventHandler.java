package com.phylogeny.extrabitmanipulation.capability;

import com.phylogeny.extrabitmanipulation.reference.Reference;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SculptSettingsEventHandler
{
	
	@SubscribeEvent
	public void onEntityConstruct(AttachCapabilitiesEvent event)
	{
		event.addCapability(new ResourceLocation(Reference.MOD_ID, "SculptSettingsHandler"), new SculptSettingsHandler());
	}
	
	@SubscribeEvent
	public void syncDataForNewPlayers(EntityJoinWorldEvent event)
	{
		Entity player = event.getEntity();
		if (!player.worldObj.isRemote && player instanceof EntityPlayerMP)
		{
			ISculptSettingsHandler cap = SculptSettingsHandler.getCapability((EntityPlayer) player);
			if (cap != null)
			{
				cap.syncAllData((EntityPlayerMP) player);
			}
		}
	}
	
	@SubscribeEvent
	public void syncDataForClonedPlayers(PlayerEvent.Clone event)
	{
		if (event.isWasDeath())
		{
			ISculptSettingsHandler capOld = SculptSettingsHandler.getCapability(event.getOriginal());
			if (capOld != null)
			{
				ISculptSettingsHandler capNew = SculptSettingsHandler.getCapability((EntityPlayer) event.getEntity());
				if (capNew != null)
				{
					NBTTagCompound nbt = ((SculptSettingsHandler) capOld).serializeNBT();
					((SculptSettingsHandler) capNew).deserializeNBT(nbt);
				}
			}
		}
	}
	
}