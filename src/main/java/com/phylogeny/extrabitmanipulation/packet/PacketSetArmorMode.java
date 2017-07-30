package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;

public class PacketSetArmorMode implements IMessage
{
	private int mode;
	
	public PacketSetArmorMode() {}
	
	public PacketSetArmorMode(int mode)
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
	
	public static class Handler implements IMessageHandler<PacketSetArmorMode, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetArmorMode message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().player;
					BitToolSettingsHelper.setArmorMode(player, player.getHeldItemMainhand(), message.mode, null);
				}
			});
			return null;
		}
		
	}
	
}