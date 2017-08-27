package com.phylogeny.extrabitmanipulation.config;

public class ConfigBitToolSettingInt extends ConfigBitToolSettingBase<Integer>
{
	
	public ConfigBitToolSettingInt(String name, boolean perTool, boolean displayInChat, int defaultValue, int value)
	{
		super(name, perTool, displayInChat);
		this.defaultValue = defaultValue;
		setValue(value);
	}
	
}