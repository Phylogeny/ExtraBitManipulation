package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.helper.SculptSettingsHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSetBitStack implements IMessage
{
	private boolean isWire;
	private ItemStack bitStack;
	
	public PacketSetBitStack() {}
	
	public PacketSetBitStack(boolean isCurved, ItemStack bitStack)
	{
		this.isWire = isCurved;
		this.bitStack = bitStack;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeBoolean(isWire);
		ItemStackHelper.stackToBytes(buffer, bitStack);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		isWire = buffer.readBoolean();
		bitStack = ItemStackHelper.stackFromBytes(buffer);
	}
	
	public static class Handler implements IMessageHandler<PacketSetBitStack, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetBitStack message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					SculptSettingsHelper.setBitStack(player, player.getHeldItemMainhand(), message.isWire, message.bitStack);
				}
			});
			return null;
		}
		
	}
	
}