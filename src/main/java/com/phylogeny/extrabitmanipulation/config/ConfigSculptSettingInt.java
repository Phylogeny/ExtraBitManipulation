package com.phylogeny.extrabitmanipulation.config;

public class ConfigSculptSettingInt extends ConfigSculptSettingBase
{
	private int defaultValue;
	
	public ConfigSculptSettingInt(boolean perTool, boolean displayInChat, int defaultValue)
	{
		super(perTool, displayInChat);
		this.defaultValue = defaultValue;
	}
	
	public int getDefaultValue()
	{
		return defaultValue;
	}
	
}