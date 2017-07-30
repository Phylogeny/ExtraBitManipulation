package com.phylogeny.extrabitmanipulation.config;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class ConfigReplacementBits
{
	private ConfigBitStack defaultReplacementBit;
	private boolean useDefaultReplacementBit, useAnyBitsAsReplacements, useAirAsReplacement;
	
	public ConfigReplacementBits() {}
	
	public ConfigReplacementBits(ConfigBitStack defaultReplacementBit, boolean useDefaultReplacementBit,
			boolean useAnyBitsAsReplacements, boolean useAirAsReplacement)
	{
		this.defaultReplacementBit = defaultReplacementBit;
		this.useDefaultReplacementBit = useDefaultReplacementBit;
		this.useAnyBitsAsReplacements = useAnyBitsAsReplacements;
		this.useAirAsReplacement = useAirAsReplacement;
	}
	
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeItemStack(buffer, defaultReplacementBit.getDefaultValue());
		buffer.writeBoolean(useDefaultReplacementBit);
		buffer.writeBoolean(useAnyBitsAsReplacements);
		buffer.writeBoolean(useAirAsReplacement);
	}
	
	public void fromBytes(ByteBuf buffer)
	{
		defaultReplacementBit = new ConfigBitStack(ByteBufUtils.readItemStack(buffer));
		useDefaultReplacementBit = buffer.readBoolean();
		useAnyBitsAsReplacements = buffer.readBoolean();
		useAirAsReplacement = buffer.readBoolean();
	}
	
	public void initDefaultReplacementBit()
	{
		defaultReplacementBit.init();
	}
	
	public ConfigBitStack getDefaultReplacementBit()
	{
		return defaultReplacementBit;
	}
	
	public boolean useDefaultReplacementBit()
	{
		return useDefaultReplacementBit;
	}
	
	public boolean useAnyBitsAsReplacements()
	{
		return useAnyBitsAsReplacements;
	}
	
	public boolean useAirAsReplacement()
	{
		return useAirAsReplacement;
	}
	
}