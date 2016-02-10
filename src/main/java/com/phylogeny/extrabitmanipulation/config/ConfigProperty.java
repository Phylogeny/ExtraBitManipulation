package com.phylogeny.extrabitmanipulation.config;

public class ConfigProperty extends Config
{
	public boolean takesDamage;
	private boolean takesDamageDefault, hasSemiDiameter;
	public int maxDamage, defaultRemovalSemiDiameter, maxRemovalSemiDiameter;
	private int maxDamageDefault, defaultRemovalSemiDiameterDefault, maxRemovalSemiDiameterDefault;
	
	public ConfigProperty(String itemTitle, boolean takesDamageDefault, int maxDamageDefault)
	{
		this(itemTitle, takesDamageDefault, maxDamageDefault, 0, 0);
		hasSemiDiameter = false;
	}
	
	public ConfigProperty(String itemName, boolean takesDamageDefault, int maxDamageDefault,
			int defaultRemovalSemiDiameterDefault, int maxRemovalSemiDiameterDefault)
	{
		super(itemName);
		this.takesDamageDefault = takesDamageDefault;
		this.maxDamageDefault = maxDamageDefault;
		this.defaultRemovalSemiDiameterDefault = defaultRemovalSemiDiameterDefault;
		this.maxRemovalSemiDiameterDefault = maxRemovalSemiDiameterDefault;
		hasSemiDiameter = true;
	}

	public boolean getTakesDamageDefault()
	{
		return takesDamageDefault;
	}

	public int getMaxDamageDefault()
	{
		return maxDamageDefault;
	}
	
	public boolean hasSemiDiameter()
	{
		return hasSemiDiameter;
	}

	public int getDefaultRemovalSemiDiameterDefault()
	{
		return defaultRemovalSemiDiameterDefault;
	}

	public int getMaxRemovalSemiDiameterDefault()
	{
		return maxRemovalSemiDiameterDefault;
	}
	
}