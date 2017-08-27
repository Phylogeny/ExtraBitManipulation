package com.phylogeny.extrabitmanipulation.config;

import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;

public class ConfigBitStack extends ConfigBitToolSettingBase<ItemStack>
{
	private IBlockState valueDefault, stateDefault, stateDefaultDefault;
	private String stringDeafult;
	
	public ConfigBitStack(ItemStack bitStackDefault)
	{
		super("", false, false);
		defaultValue = bitStackDefault;
	}
	
	public ConfigBitStack(String name, IBlockState bitBlockDefault, IBlockState defaultDefaultBitBlock, String stringDefault, IBlockState valueDefault)
	{
		this(name, false, false, bitBlockDefault, defaultDefaultBitBlock, stringDefault, valueDefault);
	}
	
	public ConfigBitStack(String name, boolean perTool, boolean displayInChat, IBlockState stateDefault,
			IBlockState stateDefaultDefault, String stringDefault, IBlockState valueDefault)
	{
		super(name, perTool, displayInChat);
		this.stateDefault = stateDefault;
		this.stateDefaultDefault = stateDefaultDefault;
		this.stringDeafult = stringDefault;
		this.valueDefault = valueDefault;
	}
	
	public void init()
	{
		defaultValue = getBitStack(stateDefault);
		value = getBitStack(valueDefault);
	}
	
	private ItemStack getBitStack(IBlockState defaultState)
	{
		ItemStack bitStack = ItemStack.EMPTY;
		IChiselAndBitsAPI api = ChiselsAndBitsAPIAccess.apiInstance;
		if (api != null)
		{
			try
			{
				bitStack = api.getBitItem(defaultState != null ? defaultState : stateDefaultDefault);
			}
			catch (InvalidBitItem e) {}
		}
		return bitStack;
	}
	
	public IBlockState getDefaultState()
	{
		return stateDefault != null ? stateDefault : stateDefaultDefault;
	}
	
	public String getStringDeafult()
	{
		return stringDeafult;
	}
	
	@Override
	public boolean isAtDefaultValue()
	{
		return super.isAtDefaultValue() || ItemStack.areItemStacksEqual(value, defaultValue);
	}
	
}