package com.phylogeny.extrabitmanipulation.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class Storage implements IStorage<IBitToolSettingsHandler>
{
	
	@Override
	public NBTBase writeNBT(Capability<IBitToolSettingsHandler> capability, IBitToolSettingsHandler instance, EnumFacing side)
	{
		return null;
	}
	
	@Override
	public void readNBT(Capability<IBitToolSettingsHandler> capability, IBitToolSettingsHandler instance, EnumFacing side, NBTBase nbt) {}
	
}