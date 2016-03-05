package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.extendedproperties.SculptSettingsPlayerProperties;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSyncRotation implements IMessage
{
	private int rotation;
	
	public PacketSyncRotation() {}
	
	public PacketSyncRotation(int rotation)
	{
		this.rotation = rotation;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(rotation);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		rotation = buffer.readInt();
	}
	
	public static class Handler implements IMessageHandler<PacketSyncRotation, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSyncRotation message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
					if (sculptProp != null)
					{
						sculptProp.setRotation(message.rotation, true);
					}
				}
			});
			return null;
		}
		
	}
	
}