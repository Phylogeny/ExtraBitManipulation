package com.phylogeny.extrabitmanipulation.config;

public class ConfigProperty extends ConfigNamed
{
	public boolean takesDamage;
	public int maxDamage;
	private boolean takesDamageDefault;
	private int maxDamageDefault;
	
	public ConfigProperty(String itemName, boolean takesDamageDefault, int maxDamageDefault)
	{
		super(itemName);
		this.takesDamageDefault = takesDamageDefault;
		this.maxDamageDefault = maxDamageDefault;
	}
	
	public boolean getTakesDamageDefault()
	{
		return takesDamageDefault;
	}
	
	public int getMaxDamageDefault()
	{
		return maxDamageDefault;
	}
	
}