package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class PacketBlockInteraction implements IMessage
{
	protected BlockPos pos;
	protected EnumFacing side;
	protected Vec3d hit;
	
	public PacketBlockInteraction() {}
	
	public PacketBlockInteraction(BlockPos pos, EnumFacing side, Vec3d hit)
	{
		this.pos = pos;
		this.side = side;
		this.hit = hit;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeLong(pos.toLong());
		buffer.writeInt(side.ordinal());
		buffer.writeDouble(hit.x);
		buffer.writeDouble(hit.y);
		buffer.writeDouble(hit.z);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		pos = BlockPos.fromLong(buffer.readLong());
		side = EnumFacing.getFront(buffer.readInt());
		hit = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
	}
	
}