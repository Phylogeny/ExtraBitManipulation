package com.phylogeny.extrabitmanipulation.config;

import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;

import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class ConfigBitStack extends ConfigBitToolSettingBase
{
	private ItemStack bitStackDefault;
	private IBlockState stateDefault, stateDefaultDefault;
	
	public ConfigBitStack(IBlockState bitBlockDefault, IBlockState defaultDefaultBitBlock)
	{
		this(false, false, bitBlockDefault, defaultDefaultBitBlock);
	}
	
	public ConfigBitStack(boolean perTool, boolean displayInChat, IBlockState stateDefault, IBlockState stateDefaultDefault)
	{
		super(perTool, displayInChat);
		this.stateDefault = stateDefault;
		this.stateDefaultDefault = stateDefaultDefault;
	}
	
	public void init()
	{
		IChiselAndBitsAPI api = ChiselsAndBitsAPIAccess.apiInstance;
		if (api != null)
		{
			try
			{
				bitStackDefault = api.getBitItem(stateDefault != null ? stateDefault : stateDefaultDefault);
				if (bitStackDefault.getItem() == null)
					bitStackDefault = null;
			}
			catch (InvalidBitItem e) {}
		}
	}
	
	public ItemStack getDefaultValue()
	{
		return bitStackDefault;
	}
	
	public IBlockState getDefaultState()
	{
		return stateDefault != null ? stateDefault : stateDefaultDefault;
	}
	
}