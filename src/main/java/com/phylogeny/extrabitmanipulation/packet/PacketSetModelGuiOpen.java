package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;

public class PacketSetModelGuiOpen implements IMessage
{
	private boolean openGui;
	
	public PacketSetModelGuiOpen() {}
	
	public PacketSetModelGuiOpen(boolean openEnds)
	{
		this.openGui = openEnds;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeBoolean(openGui);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		openGui = buffer.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<PacketSetModelGuiOpen, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetModelGuiOpen message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().player;
					BitToolSettingsHelper.setModelGuiOpen(player, player.getHeldItemMainhand(), message.openGui, null);
				}
			});
			return null;
		}
		
	}
	
}