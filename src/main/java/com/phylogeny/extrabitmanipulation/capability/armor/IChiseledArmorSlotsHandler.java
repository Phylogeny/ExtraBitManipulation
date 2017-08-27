package com.phylogeny.extrabitmanipulation.capability.armor;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface IChiseledArmorSlotsHandler extends IItemHandlerModifiable
{
	public void syncAllData(EntityPlayerMP player);
	
}