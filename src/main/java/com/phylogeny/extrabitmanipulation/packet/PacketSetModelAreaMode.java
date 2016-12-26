package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSetModelAreaMode implements IMessage
{
	private int mode;
	
	public PacketSetModelAreaMode() {}
	
	public PacketSetModelAreaMode(int mode)
	{
		this.mode = mode;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(mode);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		mode = buffer.readInt();
	}
	
	public static class Handler implements IMessageHandler<PacketSetModelAreaMode, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetModelAreaMode message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					BitToolSettingsHelper.setModelAreaMode(player, player.getHeldItemMainhand(), message.mode, null);
				}
			});
			return null;
		}
		
	}
	
}