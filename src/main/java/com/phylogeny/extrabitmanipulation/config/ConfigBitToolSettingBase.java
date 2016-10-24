package com.phylogeny.extrabitmanipulation.config;

public class ConfigBitToolSettingBase extends ConfigNamed
{
	private boolean perTool, displayInChat;
	
	public ConfigBitToolSettingBase(String name, boolean perTool, boolean displayInChat)
	{
		super(name);
		this.perTool = perTool;
		this.displayInChat = displayInChat;
	}
	
	public boolean isPerTool()
	{
		return perTool;
	}
	
	public boolean shouldDisplayInChat()
	{
		return displayInChat;
	}
	
}