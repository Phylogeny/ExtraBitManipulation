package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;

public class PacketSetArmorMovingPart extends PacketEquipmentSlot
{
	private int partIndex;
	
	public PacketSetArmorMovingPart() {}
	
	public PacketSetArmorMovingPart(int partIndex, @Nullable EntityEquipmentSlot equipmentSlot)
	{
		super(equipmentSlot);
		this.partIndex = partIndex;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		buffer.writeInt(partIndex);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		partIndex = buffer.readInt();
	}
	
	public static class Handler implements IMessageHandler<PacketSetArmorMovingPart, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetArmorMovingPart message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					ItemStack stack = message.equipmentSlot == null ? player.getHeldItemMainhand() : player.getItemStackFromSlot(message.equipmentSlot);
					if (ItemStackHelper.isChiseledArmorStack(stack))
						BitToolSettingsHelper.setArmorMovingPart(player, stack, message.partIndex, null, null);
				}
			});
			return null;
		}
		
	}
	
}