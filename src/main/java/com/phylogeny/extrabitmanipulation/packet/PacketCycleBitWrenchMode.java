package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCycleBitWrenchMode implements IMessage
{
	private boolean forward;
	
	public PacketCycleBitWrenchMode() {}
	
	public PacketCycleBitWrenchMode(boolean forward)
	{
		this.forward = forward;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeBoolean(forward);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		forward = buffer.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<PacketCycleBitWrenchMode, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketCycleBitWrenchMode message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					ItemStack stack = player.getHeldItemMainhand();
					if (ItemStackHelper.isBitWrenchStack(stack))
						((ItemBitWrench) stack.getItem()).cycleModes(stack, message.forward);
				}
			});
			return null;
		}
		
	}
	
}