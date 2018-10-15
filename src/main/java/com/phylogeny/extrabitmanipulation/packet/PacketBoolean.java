package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class PacketBoolean implements IMessage
{
	protected boolean value;
	
	public PacketBoolean() {}
	
	public PacketBoolean(boolean value)
	{
		this.value = value;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeBoolean(value);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		value = buffer.readBoolean();
	}
	
}