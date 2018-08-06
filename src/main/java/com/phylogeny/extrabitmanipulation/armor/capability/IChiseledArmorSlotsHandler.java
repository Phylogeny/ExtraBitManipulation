package com.phylogeny.extrabitmanipulation.armor.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface IChiseledArmorSlotsHandler extends IItemHandlerModifiable
{
	void syncAllSlots(EntityPlayer player);
	
	void markAllSlotsDirty();
	
	void markSlotDirty(int index);
	
	boolean hasArmor();
	
	boolean hasArmorSet(int indexSet);
	
	boolean hasArmorType(int indexType);
}