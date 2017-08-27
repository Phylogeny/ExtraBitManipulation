package com.phylogeny.extrabitmanipulation.capability;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class Storage<T> implements IStorage<T>
{
	
	@Override
	@Nullable
	public NBTBase writeNBT(Capability capability, T instance, EnumFacing side)
	{
		return null;
	}
	
	@Override
	public void readNBT(Capability capability, T instance, EnumFacing side, NBTBase nbt) {}
	
}