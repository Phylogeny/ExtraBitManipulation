package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.item.ItemSculptingLoop;

public class PacketSculpt implements IMessage
{
	private BlockPos pos;
	private EnumFacing side;
	private Vec3 hit;
	
	public PacketSculpt() {}
	
	public PacketSculpt(BlockPos pos, EnumFacing side, Vec3 hit)
	{
		this.pos = pos;
		this.side = side;
		this.hit = hit;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(pos.getX());
		buffer.writeInt(pos.getY());
		buffer.writeInt(pos.getZ());
		buffer.writeInt(side.ordinal());
		buffer.writeDouble(hit.xCoord);
		buffer.writeDouble(hit.yCoord);
		buffer.writeDouble(hit.zCoord);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		pos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
		side = EnumFacing.getFront(buffer.readInt());
		hit = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
	}
	
	public static class Handler implements IMessageHandler<PacketSculpt, IMessage>
	{
		@Override
		public IMessage onMessage(PacketSculpt message, MessageContext ctx)
		{
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			ItemStack stack = player.getCurrentEquippedItem();
			if (stack != null && stack.getItem() instanceof ItemSculptingLoop)
			{
				ItemSculptingLoop.sculptBlock(stack, player, player.worldObj, message.pos, message.side, message.hit);
			}
			return null;
		}
		
	}
	
}