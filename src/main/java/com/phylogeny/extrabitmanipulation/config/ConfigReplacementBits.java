package com.phylogeny.extrabitmanipulation.config;

public class ConfigReplacementBits
{
	public ConfigBitStack defaultReplacementBit;
	public boolean useDefaultReplacementBit, useAnyBitsAsReplacements, useAirAsReplacement;
	private boolean useDefaultReplacementBitDefault, useAnyBitsAsReplacementsDefault, useAirAsReplacementDefault;
	
	public ConfigReplacementBits(boolean useDefaultReplacementBitDefault, boolean useAnyBitsAsReplacementsDefault, boolean useAirAsReplacementDefault)
	{
		this.useDefaultReplacementBitDefault = useDefaultReplacementBitDefault;
		this.useAnyBitsAsReplacementsDefault = useAnyBitsAsReplacementsDefault;
		this.useAirAsReplacementDefault = useAirAsReplacementDefault;
	}
	
	public boolean getUseDefaultReplacementBitDefault()
	{
		return useDefaultReplacementBitDefault;
	}
	
	public boolean getUseAnyBitsAsReplacementsDefault()
	{
		return useAnyBitsAsReplacementsDefault;
	}
	
	public boolean getUseAirAsReplacementDefault()
	{
		return useAirAsReplacementDefault;
	}
	
}