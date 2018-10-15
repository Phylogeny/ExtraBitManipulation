package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class PacketBitMapIO implements IMessage
{
	protected String nbtKey;
	protected boolean saveStatesById;
	
	public PacketBitMapIO() {}
	
	public PacketBitMapIO(String nbtKey, boolean saveStatesById)
	{
		this.nbtKey = nbtKey;
		this.saveStatesById = saveStatesById;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, nbtKey);
		buffer.writeBoolean(saveStatesById);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		nbtKey = ByteBufUtils.readUTF8String(buffer);
		saveStatesById = buffer.readBoolean();
	}
	
}