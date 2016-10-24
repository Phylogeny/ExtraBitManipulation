package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

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
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					BitToolSettingsHelper.setModelGuiOpen(player, player.getCurrentEquippedItem(), message.openGui, null);
				}
			});
			return null;
		}
		
	}
	
}