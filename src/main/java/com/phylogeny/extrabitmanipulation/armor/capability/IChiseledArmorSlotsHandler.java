package com.phylogeny.extrabitmanipulation.armor.capability;

import com.phylogeny.extrabitmanipulation.armor.ModelPartConcealer;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface IChiseledArmorSlotsHandler extends IItemHandlerModifiable
{
	void syncAllSlots(EntityPlayer player);
	
	void markAllSlotsDirty();
	
	void markSlotDirty(int index);
	
	void onContentsChanged(int slot);
	
	boolean hasArmor();
	
	boolean hasArmorSet(int indexSet);
	
	boolean hasArmorType(int indexType);
	
	ModelPartConcealer getAndApplyModelPartConcealer(ModelBiped model);
}