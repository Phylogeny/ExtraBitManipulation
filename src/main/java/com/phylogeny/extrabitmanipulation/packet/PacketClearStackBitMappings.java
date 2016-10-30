package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.helper.BitIOHelper;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketClearStackBitMappings implements IMessage
{
	public PacketClearStackBitMappings() {}
	
	@Override
	public void toBytes(ByteBuf buffer) {}
	
	@Override
	public void fromBytes(ByteBuf buffer) {}
	
	public static class Handler implements IMessageHandler<PacketClearStackBitMappings, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketClearStackBitMappings message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					ItemStack stack = player.getHeldItemMainhand();
					if (ItemStackHelper.isModelingToolStack(stack))
					{
						BitIOHelper.clearAllBitMapsFromNbt(stack);
						player.inventoryContainer.detectAndSendChanges();
					}
				}
			});
			return null;
		}
		
	}
	
}