package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCursorStack implements IMessage
{
	private ItemStack stack;
	
	public PacketCursorStack() {}
	
	public PacketCursorStack(ItemStack stack)
	{
		this.stack = stack;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		ItemStackHelper.stackToBytes(buffer, stack);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		stack = ItemStackHelper.stackFromBytes(buffer);
	}
	
	public static class Handler implements IMessageHandler<PacketCursorStack, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketCursorStack message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayerMP player = ctx.getServerHandler().playerEntity;
					if (player.capabilities.isCreativeMode)
						player.inventory.setItemStack(message.stack);
				}
			});
			return null;
		}
		
	}
	
}