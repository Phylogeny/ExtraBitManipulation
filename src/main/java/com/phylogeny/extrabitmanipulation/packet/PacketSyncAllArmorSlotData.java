package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.armor.capability.ChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.armor.capability.IChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;

public class PacketSyncAllArmorSlotData implements IMessage
{
	private NBTTagCompound nbt = new NBTTagCompound();
	
	public PacketSyncAllArmorSlotData() {}
	
	public PacketSyncAllArmorSlotData(NBTTagCompound nbt)
	{
		this.nbt = nbt;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeTag(buffer, nbt);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		nbt = ByteBufUtils.readTag(buffer);
	}
	
	public static class Handler implements IMessageHandler<PacketSyncAllArmorSlotData, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSyncAllArmorSlotData message, final MessageContext ctx)
		{
			ClientHelper.getThreadListener().addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					IChiseledArmorSlotsHandler cap = ChiseledArmorSlotsHandler.getCapability(ClientHelper.getPlayer());
					if (cap != null)
					{
						ChiseledArmorSlotsHandler handeler = new ChiseledArmorSlotsHandler();
						handeler.deserializeNBT(message.nbt);
						for (int i = 0; i < handeler.getSlots(); i++)
							cap.setStackInSlot(i, handeler.getStackInSlot(i));
					}
				}
			});
			return null;
		}
		
	}
	
}