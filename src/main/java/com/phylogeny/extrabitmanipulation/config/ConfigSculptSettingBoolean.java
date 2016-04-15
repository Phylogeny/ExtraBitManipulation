package com.phylogeny.extrabitmanipulation.config;

public class ConfigSculptSettingBoolean extends ConfigSculptSettingBase
{
	private boolean defaultValue;
	
	public ConfigSculptSettingBoolean(boolean perTool, boolean displayInChat, boolean defaultValue)
	{
		super(perTool, displayInChat);
		this.defaultValue = defaultValue;
	}
	
	public boolean getDefaultValue()
	{
		return defaultValue;
	}
	
}