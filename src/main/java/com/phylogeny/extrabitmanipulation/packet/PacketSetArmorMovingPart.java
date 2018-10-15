package com.phylogeny.extrabitmanipulation.packet;

import javax.annotation.Nullable;

import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetArmorMovingPart extends PacketArmorSlotInt
{
	public PacketSetArmorMovingPart() {}
	
	public PacketSetArmorMovingPart(int partIndex, @Nullable ArmorType armorType, int indexArmorSet)
	{
		super(armorType, indexArmorSet, partIndex);
	}
	
	public static class Handler implements IMessageHandler<PacketSetArmorMovingPart, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetArmorMovingPart message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().player;
					ItemStack stack = getArmorStack(player, message);
					if (!stack.isEmpty())
						BitToolSettingsHelper.setArmorMovingPart(player, stack, message.value, null, message.armorType, 0);
				}
			});
			return null;
		}
		
	}
	
}