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

public class PacketSetArmorScale extends PacketArmorSlotInt
{
	public PacketSetArmorScale() {}
	
	public PacketSetArmorScale(int scale, @Nullable ArmorType armorType, int indexArmorSet)
	{
		super(armorType, indexArmorSet, scale);
	}
	
	public static class Handler implements IMessageHandler<PacketSetArmorScale, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetArmorScale message, final MessageContext ctx)
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
						BitToolSettingsHelper.setArmorScale(player, stack, message.value, null, message.armorType, message.indexArmorSet);
				}
			});
			return null;
		}
		
	}
	
}