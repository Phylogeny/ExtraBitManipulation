package com.phylogeny.extrabitmanipulation.config;

import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;

import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ConfigSculptSettingBitStack extends ConfigSculptSettingBase
{
	private ItemStack defaultBitStack;
	private Block defaultBitBlock;
	
	public ConfigSculptSettingBitStack(boolean perTool, boolean displayInChat, Block defaultBitBlock)
	{
		super(perTool, displayInChat);
		this.defaultBitBlock = defaultBitBlock;
	}
	
	public void init()
	{
		IChiselAndBitsAPI api = ChiselsAndBitsAPIAccess.apiInstance;
		if (api != null && defaultBitBlock != null)
		{
			try
			{
				defaultBitStack = api.getBitItem(defaultBitBlock.getDefaultState());
				if (defaultBitStack.getItem() == null)
				{
					defaultBitStack = null;
				}
			}
			catch (InvalidBitItem e) {}
		}
	}
	
	public ItemStack getDefaultValue()
	{
		return defaultBitStack;
	}
	
}