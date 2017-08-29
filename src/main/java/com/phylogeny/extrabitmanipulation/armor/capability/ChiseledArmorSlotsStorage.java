package com.phylogeny.extrabitmanipulation.armor.capability;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class ChiseledArmorSlotsStorage implements IStorage<IChiseledArmorSlotsHandler>
{
	
	@Override
	@Nullable
	public NBTBase writeNBT(Capability capability, IChiseledArmorSlotsHandler instance, EnumFacing side)
	{
		return null;
	}
	
	@Override
	public void readNBT(Capability capability, IChiseledArmorSlotsHandler instance, EnumFacing side, NBTBase nbt) {}
	
}