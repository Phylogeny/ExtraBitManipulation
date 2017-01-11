package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSetShapeOffset implements IMessage
{
	private boolean offsetShape;
	
	public PacketSetShapeOffset() {}
	
	public PacketSetShapeOffset(boolean offsetShape)
	{
		this.offsetShape = offsetShape;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeBoolean(offsetShape);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		offsetShape = buffer.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<PacketSetShapeOffset, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetShapeOffset message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					BitToolSettingsHelper.setShapeOffset(player, player.getHeldItemMainhand(), message.offsetShape, null);
				}
			});
			return null;
		}
		
	}
	
}