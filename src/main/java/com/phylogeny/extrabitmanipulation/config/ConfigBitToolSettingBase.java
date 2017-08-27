package com.phylogeny.extrabitmanipulation.config;

public class ConfigBitToolSettingBase<T> extends ConfigNamed
{
	protected T value, defaultValue;
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
	
	public T getDefaultValue()
	{
		return defaultValue;
	}
	
	public T getValue()
	{
		return value;
	}
	
	public void setValue(T value)
	{
		this.value = value;
	}
	
	public boolean isAtDefaultValue()
	{
		return value.equals(defaultValue);
	}
	
}