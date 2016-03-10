package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.helper.SculptSettingsHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSetRotation implements IMessage
{
	private int rotation;
	
	public PacketSetRotation() {}
	
	public PacketSetRotation(int rotation)
	{
		this.rotation = rotation;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(rotation);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		rotation = buffer.readInt();
	}
	
	public static class Handler implements IMessageHandler<PacketSetRotation, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetRotation message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					SculptSettingsHelper.setRotation(player, player.getCurrentEquippedItem(), message.rotation);
				}
			});
			return null;
		}
		
	}
	
}