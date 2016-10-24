package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSetEndsOpen implements IMessage
{
	private boolean openEnds;
	
	public PacketSetEndsOpen() {}
	
	public PacketSetEndsOpen(boolean openEnds)
	{
		this.openEnds = openEnds;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeBoolean(openEnds);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		openEnds = buffer.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<PacketSetEndsOpen, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetEndsOpen message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					BitToolSettingsHelper.setEndsOpen(player, player.getHeldItemMainhand(), message.openEnds, null);
				}
			});
			return null;
		}
		
	}
	
}