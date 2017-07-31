package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
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

public class PacketBitMappingsPerTool implements IMessage
{
	protected boolean perTool;
	
	public PacketBitMappingsPerTool() {}
	
	public PacketBitMappingsPerTool(boolean perTool)
	{
		this.perTool = perTool;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeBoolean(perTool);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		perTool = buffer.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<PacketBitMappingsPerTool, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketBitMappingsPerTool message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					ItemStack stack = player.getHeldItemMainhand();
					if (ItemStackHelper.isModelingToolStack(stack))
					{
						NBTTagCompound nbt = ItemStackHelper.getNBT(stack);
						nbt.setBoolean(NBTKeys.BIT_MAPS_PER_TOOL, message.perTool);
						player.inventoryContainer.detectAndSendChanges();
					}
				}
			});
			return null;
		}
		
	}
	
}