package com.phylogeny.extrabitmanipulation.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class Storage implements IStorage<ISculptSettingsHandler>
{
	
	@Override
	public NBTBase writeNBT(Capability<ISculptSettingsHandler> capability,
			ISculptSettingsHandler instance, EnumFacing side)
	{
		return null;
	}
	
	@Override
	public void readNBT(Capability<ISculptSettingsHandler> capability,
			ISculptSettingsHandler instance, EnumFacing side, NBTBase nbt) {}
	
}