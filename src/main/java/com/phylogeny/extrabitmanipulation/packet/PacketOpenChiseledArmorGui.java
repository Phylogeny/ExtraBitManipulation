package com.phylogeny.extrabitmanipulation.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.reference.GuiIDs;

public class PacketOpenChiseledArmorGui extends PacketEmpty
{
	public PacketOpenChiseledArmorGui() {}
	
	public static class Handler implements IMessageHandler<PacketOpenChiseledArmorGui, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketOpenChiseledArmorGui message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().player;
					player.openGui(ExtraBitManipulation.instance, GuiIDs.CHISELED_ARMOR.getID(), player.world, 0, 0, 0);
				}
			});
			return null;
		}
		
	}
	
}