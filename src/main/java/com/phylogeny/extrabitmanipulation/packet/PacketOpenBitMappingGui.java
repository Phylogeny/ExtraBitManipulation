package com.phylogeny.extrabitmanipulation.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.reference.GuiIDs;

public class PacketOpenBitMappingGui extends PacketEmpty
{
	public PacketOpenBitMappingGui() {}
	
	public static class Handler implements IMessageHandler<PacketOpenBitMappingGui, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketOpenBitMappingGui message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().player;
					ItemStack stack = player.getHeldItemMainhand();
					if (ItemStackHelper.isModelingToolStack(stack) || ItemStackHelper.isDesignStack(stack))
					{
						player.openGui(ExtraBitManipulation.instance, GuiIDs.BIT_MAPPING.getID(), player.world, 0, 0, 0);
					}
				}
			});
			return null;
		}
		
	}
	
}