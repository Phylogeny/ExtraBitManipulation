package com.phylogeny.extrabitmanipulation.packet;

import java.util.UUID;

import com.phylogeny.extrabitmanipulation.armor.capability.ChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.armor.capability.IChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncArmorSlot implements IMessage
{
	private UUID playerID;
	private ItemStack stack;
	private int index;
	
	public PacketSyncArmorSlot() {}
	
	public PacketSyncArmorSlot(UUID playerID, ItemStack stack, int index)
	{
		this.playerID = playerID;
		this.stack = stack;
		this.index = index;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, playerID.toString());
		ByteBufUtils.writeItemStack(buffer, stack);
		buffer.writeInt(index);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		playerID = UUID.fromString(ByteBufUtils.readUTF8String(buffer));
		stack = ByteBufUtils.readItemStack(buffer);
		index = buffer.readInt();
	}
	
	public static class Handler implements IMessageHandler<PacketSyncArmorSlot, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSyncArmorSlot message, final MessageContext ctx)
		{
			ClientHelper.getThreadListener().addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ClientHelper.getWorld().getPlayerEntityByUUID(message.playerID);
					if (player == null)
						return;
					
					IChiseledArmorSlotsHandler cap = ChiseledArmorSlotsHandler.getCapability(player);
					if (cap != null)
						cap.setStackInSlot(message.index, message.stack);
				}
			});
			return null;
		}
		
	}
	
}