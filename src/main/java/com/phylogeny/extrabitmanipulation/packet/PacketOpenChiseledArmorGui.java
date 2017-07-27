package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.reference.GuiIDs;

public class PacketOpenChiseledArmorGui implements IMessage
{
	public PacketOpenChiseledArmorGui() {}
	
	@Override
	public void toBytes(ByteBuf buffer) {}
	
	@Override
	public void fromBytes(ByteBuf buffer) {}
	
	public static class Handler implements IMessageHandler<PacketOpenChiseledArmorGui, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketOpenChiseledArmorGui message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					player.openGui(ExtraBitManipulation.instance, GuiIDs.CHISELED_ARMOR.getID(), player.world, 0, 0, 0);
				}
			});
			return null;
		}
		
	}
	
}