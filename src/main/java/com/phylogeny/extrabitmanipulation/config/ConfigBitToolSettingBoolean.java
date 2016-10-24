package com.phylogeny.extrabitmanipulation.config;

public class ConfigBitToolSettingBoolean extends ConfigBitToolSettingBase
{
	private boolean value, defaultValue;
	
	public ConfigBitToolSettingBoolean(String name, boolean perTool, boolean displayInChat, boolean defaultValue, boolean value)
	{
		super(name, perTool, displayInChat);
		this.defaultValue = defaultValue;
		setValue(value);
	}
	
	public boolean getDefaultValue()
	{
		return defaultValue;
	}
	
	public boolean getValue()
	{
		return value;
	}
	
	public void setValue(boolean value)
	{
		this.value = value;
	}
	
}