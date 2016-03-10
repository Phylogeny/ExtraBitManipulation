package com.phylogeny.extrabitmanipulation.config;

public class ConfigSculptSettingBase
{
	private boolean perTool, displayInChat;
	
	public ConfigSculptSettingBase(boolean perTool, boolean displayInChat)
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