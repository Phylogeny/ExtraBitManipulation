package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.entity.EntityBit;

public class PacketPlaceEntityBit implements IMessage
{
	private ItemStack bitStack;
	private BlockPos pos;
	private Vec3d hitVec;
	private EnumFacing sideHit;
	
	public PacketPlaceEntityBit() {}
	
	public PacketPlaceEntityBit(ItemStack bitStack, BlockPos pos, RayTraceResult result)
	{
		this.bitStack = bitStack;
		this.pos = pos;
		this.hitVec = result.hitVec;
		this.sideHit = result.sideHit;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeItemStack(buffer, bitStack);
		buffer.writeLong(pos.toLong());
		buffer.writeDouble(hitVec.x);
		buffer.writeDouble(hitVec.y);
		buffer.writeDouble(hitVec.z);
		buffer.writeInt(sideHit.ordinal());
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		bitStack = ByteBufUtils.readItemStack(buffer);
		pos = BlockPos.fromLong(buffer.readLong());
		hitVec = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
		sideHit = EnumFacing.getFront(buffer.readInt());
	}
	
	public static class Handler implements IMessageHandler<PacketPlaceEntityBit, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketPlaceEntityBit message, final MessageContext ctx)
		{
			ClientHelper.getThreadListener().addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityBit.placeBit(ClientHelper.getWorld(), message.bitStack, message.pos, message.hitVec, message.sideHit, false);
				}
			});
			return null;
		}
		
	}
	
}