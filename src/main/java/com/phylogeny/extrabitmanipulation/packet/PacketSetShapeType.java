package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;

public class PacketSetShapeType implements IMessage
{
	private boolean isCurved;
	private int shapeType;
	
	public PacketSetShapeType() {}
	
	public PacketSetShapeType(boolean isCurved, int shapeType)
	{
		this.isCurved = isCurved;
		this.shapeType = shapeType;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeBoolean(isCurved);
		buffer.writeInt(shapeType);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		isCurved = buffer.readBoolean();
		shapeType = buffer.readInt();
	}
	
	public static class Handler implements IMessageHandler<PacketSetShapeType, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetShapeType message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().player;
					BitToolSettingsHelper.setShapeType(player, player.getHeldItemMainhand(), message.isCurved, message.shapeType, null);
				}
			});
			return null;
		}
		
	}
	
}