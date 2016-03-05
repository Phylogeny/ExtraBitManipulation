package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.extendedproperties.SculptSettingsPlayerProperties;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSyncAllSculptingData implements IMessage
{
	private int rotation;
	
	public PacketSyncAllSculptingData() {}
	
	public PacketSyncAllSculptingData(int rotation)
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
	
	public static class Handler implements IMessageHandler<PacketSyncAllSculptingData, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSyncAllSculptingData message, final MessageContext ctx)
		{
			IThreadListener mainThread = Minecraft.getMinecraft();
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = Minecraft.getMinecraft().thePlayer;
					SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
					if (sculptProp != null)
					{
						sculptProp.setRotation(message.rotation, false);
					}
				}
			});
			return null;
		}
		
	}
	
}