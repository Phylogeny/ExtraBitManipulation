package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
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
		ByteBufUtils.writeItemStack(buffer, stack);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		stack = ByteBufUtils.readItemStack(buffer);
	}
	
	public static class Handler implements IMessageHandler<PacketCursorStack, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketCursorStack message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					ctx.getServerHandler().player.inventory.setItemStack(message.stack);
				}
			});
			return null;
		}
		
	}
	
}