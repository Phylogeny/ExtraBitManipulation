package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;

public class PacketSetTargetArmorBits implements IMessage
{
	private boolean targetBits;
	
	public PacketSetTargetArmorBits() {}
	
	public PacketSetTargetArmorBits(boolean targetBits)
	{
		this.targetBits = targetBits;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeBoolean(targetBits);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		targetBits = buffer.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<PacketSetTargetArmorBits, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetTargetArmorBits message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().player;
					BitToolSettingsHelper.setArmorBitsTargeted(player, player.getHeldItemMainhand(), message.targetBits, null);
				}
			});
			return null;
		}
		
	}
	
}