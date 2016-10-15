package com.phylogeny.extrabitmanipulation.config;

public class ConfigBitToolSettingBase
{
	private boolean perTool, displayInChat;
	
	public ConfigBitToolSettingBase(boolean perTool, boolean displayInChat)
	{
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