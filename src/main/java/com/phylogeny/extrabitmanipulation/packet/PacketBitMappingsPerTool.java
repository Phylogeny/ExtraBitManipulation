package com.phylogeny.extrabitmanipulation.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

public class PacketBitMappingsPerTool extends PacketBoolean
{
	public PacketBitMappingsPerTool() {}
	
	public PacketBitMappingsPerTool(boolean perTool)
	{
		super(perTool);
	}
	
	public static class Handler implements IMessageHandler<PacketBitMappingsPerTool, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketBitMappingsPerTool message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().player;
					ItemStack stack = player.getHeldItemMainhand();
					if (ItemStackHelper.isModelingToolStack(stack))
					{
						NBTTagCompound nbt = ItemStackHelper.getNBT(stack);
						nbt.setBoolean(NBTKeys.BIT_MAPS_PER_TOOL, message.value);
						player.inventoryContainer.detectAndSendChanges();
					}
				}
			});
			return null;
		}
		
	}
	
}