package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;

import java.util.List;

import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.ItemType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.container.ContainerChiseledArmor;

public class PacketRemoveStackFromSlotAsBits implements IMessage
{
	private int slotNumber;
	
	public PacketRemoveStackFromSlotAsBits() {}
	
	public PacketRemoveStackFromSlotAsBits(int slotNumber)
	{
		this.slotNumber = slotNumber;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(slotNumber);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		slotNumber = buffer.readInt();
	}
	
	public static class Handler implements IMessageHandler<PacketRemoveStackFromSlotAsBits, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketRemoveStackFromSlotAsBits message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					if (!(player.openContainer instanceof ContainerChiseledArmor))
						return;
					
					List<Slot> slots = player.openContainer.inventorySlots;
					if (message.slotNumber >= slots.size())
						return;
					
					Slot slot = slots.get(message.slotNumber);
					if (new StackProvider(player, slot.getStack()).giveStackToPlayer())
						slot.putStack(null);
				}
			});
			return null;
		}
		
	}
	
	public static class StackProvider extends PacketChangeArmorItemList.StackProvider
	{
		
		public StackProvider(EntityPlayer player, ItemStack stack)
		{
			super(player, stack);
		}
		
		@Override
		public boolean giveStackToPlayer()
		{
			if (api.getItemType(stack) != ItemType.CHISLED_BLOCK)
				return false;
			
			IBitAccess bitAccess = api.createBitItem(stack);
			if (bitAccess != null)
			{
				bitAccess.visitBits(this);
				return true;
			}
			return false;
		}
		
	}
	
}