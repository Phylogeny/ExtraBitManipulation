package com.phylogeny.extrabitmanipulation.config;

public class ConfigBitToolSettingInt extends ConfigBitToolSettingBase
{
	private int value, defaultValue;
	
	public ConfigBitToolSettingInt(String name, boolean perTool, boolean displayInChat, int defaultValue, int value)
	{
		super(name, perTool, displayInChat);
		this.defaultValue = defaultValue;
		setValue(value);
	}
	
	public int getDefaultValue()
	{
		return defaultValue;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public void setValue(int value)
	{
		this.value = value;
	}
	
}