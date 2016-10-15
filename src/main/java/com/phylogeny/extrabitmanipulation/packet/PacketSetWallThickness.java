package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSetWallThickness implements IMessage
{
	private int wallThickness;
	
	public PacketSetWallThickness() {}
	
	public PacketSetWallThickness(int wallThickness)
	{
		this.wallThickness = wallThickness;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(wallThickness);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		wallThickness = buffer.readInt();
	}
	
	public static class Handler implements IMessageHandler<PacketSetWallThickness, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetWallThickness message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					BitToolSettingsHelper.setWallThickness(player, player.getCurrentEquippedItem(), message.wallThickness);
				}
			});
			return null;
		}
		
	}
	
}