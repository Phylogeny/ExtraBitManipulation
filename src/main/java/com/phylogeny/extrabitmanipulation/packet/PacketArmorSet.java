package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketArmorSet implements IMessage
{
	protected int indexArmorSet;
	
	public PacketArmorSet() {}
	
	public PacketArmorSet(int indexArmorSet)
	{
		this.indexArmorSet = indexArmorSet;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(indexArmorSet);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		indexArmorSet = buffer.readInt();
	}
	
}