package com.phylogeny.extrabitmanipulation.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

public class PacketSetWrechMode extends PacketInt
{
	public PacketSetWrechMode() {}
	
	public PacketSetWrechMode(int mode)
	{
		super(mode);
	}
	
	public static class Handler implements IMessageHandler<PacketSetWrechMode, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetWrechMode message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().player;
					ItemStack stack = player.getHeldItemMainhand();
					if (ItemStackHelper.isBitWrenchStack(stack))
					{
						((ItemBitWrench) stack.getItem()).initialize(stack);
						ItemStackHelper.getNBT(stack).setInteger(NBTKeys.WRENCH_MODE, message.value);
						player.inventoryContainer.detectAndSendChanges();
					}
				}
			});
			return null;
		}
		
	}
	
}