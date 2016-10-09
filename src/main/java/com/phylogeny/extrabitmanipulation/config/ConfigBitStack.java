package com.phylogeny.extrabitmanipulation.config;

import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;

import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ConfigBitStack extends ConfigSculptSettingBase
{
	private ItemStack bitStackDefault;
	private Block bitBlockDefault, bitBlockDefaultDefault;
	
	public ConfigBitStack(Block bitBlockDefault, Block defaultDefaultBitBlock)
	{
		this(false, false, bitBlockDefault, defaultDefaultBitBlock);
	}
	
	public ConfigBitStack(boolean perTool, boolean displayInChat, Block bitBlockDefault, Block bitBlockDefaultDefault)
	{
		super(perTool, displayInChat);
		this.bitBlockDefault = bitBlockDefault;
		this.bitBlockDefaultDefault = bitBlockDefaultDefault;
	}
	
	public void init()
	{
		IChiselAndBitsAPI api = ChiselsAndBitsAPIAccess.apiInstance;
		if (api != null)
		{
			try
			{
				bitStackDefault = api.getBitItem((bitBlockDefault != null ? bitBlockDefault : bitBlockDefaultDefault).getDefaultState());
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
	
	public Block getDefaultBitBlock()
	{
		return bitBlockDefault != null ? bitBlockDefault : bitBlockDefaultDefault;
	}
	
}