package com.phylogeny.extrabitmanipulation.extendedproperties;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SculptSettingsPlayerPropertiesHandler
{
	
	@SubscribeEvent
	public void registerDataForNewPlayers(EntityConstructing event)
	{
		Entity player = event.entity;
		if (player instanceof EntityPlayer && SculptSettingsPlayerProperties.get(player) == null)
		{
			SculptSettingsPlayerProperties.register(player);
		}
	}
	
	@SubscribeEvent
	public void syncDataForNewPlayers(EntityJoinWorldEvent event)
	{
		Entity player = event.entity;
		if (!player.worldObj.isRemote && player instanceof EntityPlayerMP)
		{
			SculptSettingsPlayerProperties.get(player).syncAllData((EntityPlayerMP) player);
		}
	}
	
	@SubscribeEvent
	public void copyClonedPlayerData(PlayerEvent.Clone event)
	{
		if (event.wasDeath)
		{
			SculptSettingsPlayerProperties settingsOld = SculptSettingsPlayerProperties.get(event.original);
			if (settingsOld != null)
			{
				SculptSettingsPlayerProperties settingsNew = SculptSettingsPlayerProperties.get(event.entity);
				if (settingsNew != null)
				{
					NBTTagCompound nbt = new NBTTagCompound();
					settingsOld.saveNBTData(nbt);
					settingsNew.loadNBTData(nbt);
				}
			}
		}
	}
	
}