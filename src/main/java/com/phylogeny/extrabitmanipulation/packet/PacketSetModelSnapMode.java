package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSetModelSnapMode implements IMessage
{
	private int mode;
	
	public PacketSetModelSnapMode() {}
	
	public PacketSetModelSnapMode(int mode)
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
	
	public static class Handler implements IMessageHandler<PacketSetModelSnapMode, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetModelSnapMode message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					BitToolSettingsHelper.setModelSnapMode(player, player.getCurrentEquippedItem(), message.mode, null);
				}
			});
			return null;
		}
		
	}
	
}