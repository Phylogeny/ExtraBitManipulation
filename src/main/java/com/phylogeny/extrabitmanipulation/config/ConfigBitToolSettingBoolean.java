package com.phylogeny.extrabitmanipulation.config;

public class ConfigBitToolSettingBoolean extends ConfigBitToolSettingBase
{
	private boolean defaultValue;
	
	public ConfigBitToolSettingBoolean(boolean perTool, boolean displayInChat, boolean defaultValue)
	{
		super(perTool, displayInChat);
		this.defaultValue = defaultValue;
	}
	
	public boolean getDefaultValue()
	{
		return defaultValue;
	}
	
}