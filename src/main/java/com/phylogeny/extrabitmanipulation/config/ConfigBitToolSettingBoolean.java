package com.phylogeny.extrabitmanipulation.config;

public class ConfigBitToolSettingBoolean extends ConfigBitToolSettingBase<Boolean>
{
	
	public ConfigBitToolSettingBoolean(String name, boolean perTool, boolean displayInChat, boolean defaultValue, boolean value)
	{
		super(name, perTool, displayInChat);
		this.defaultValue = defaultValue;
		setValue(value);
	}
	
}