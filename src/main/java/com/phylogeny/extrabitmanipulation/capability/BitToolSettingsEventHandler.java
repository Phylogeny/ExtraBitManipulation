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

public class BitToolSettingsEventHandler
{
	
	@SubscribeEvent
	public void onEntityConstruct(AttachCapabilitiesEvent.Entity event)
	{
		if (event.getEntity() instanceof EntityPlayer)
			event.addCapability(new ResourceLocation(Reference.MOD_ID, "BitToolSettingsHandler"), new BitToolSettingsHandler());
	}
	
	@SubscribeEvent
	public void syncDataForNewPlayers(EntityJoinWorldEvent event)
	{
		Entity player = event.getEntity();
		if (!player.worldObj.isRemote && player instanceof EntityPlayerMP)
		{
			IBitToolSettingsHandler cap = BitToolSettingsHandler.getCapability((EntityPlayer) player);
			if (cap != null)
				cap.syncAllData((EntityPlayerMP) player);
		}
	}
	
	@SubscribeEvent
	public void syncDataForClonedPlayers(PlayerEvent.Clone event)
	{
		if (event.isWasDeath())
		{
			IBitToolSettingsHandler capOld = BitToolSettingsHandler.getCapability(event.getOriginal());
			if (capOld != null)
			{
				IBitToolSettingsHandler capNew = BitToolSettingsHandler.getCapability((EntityPlayer) event.getEntity());
				if (capNew != null)
				{
					NBTTagCompound nbt = ((BitToolSettingsHandler) capOld).serializeNBT();
					((BitToolSettingsHandler) capNew).deserializeNBT(nbt);
				}
			}
		}
	}
	
}