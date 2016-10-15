package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSetHollowShape implements IMessage
{
	private boolean isWire;
	private boolean hollowShape;
	
	public PacketSetHollowShape() {}
	
	public PacketSetHollowShape(boolean hollowShape, boolean isWire)
	{
		this.hollowShape = hollowShape;
		this.isWire = isWire;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeBoolean(hollowShape);
		buffer.writeBoolean(isWire);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		hollowShape = buffer.readBoolean();
		isWire = buffer.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<PacketSetHollowShape, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetHollowShape message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					BitToolSettingsHelper.setHollowShape(player, player.getHeldItemMainhand(), message.isWire, message.hollowShape);
				}
			});
			return null;
		}
		
	}
	
}