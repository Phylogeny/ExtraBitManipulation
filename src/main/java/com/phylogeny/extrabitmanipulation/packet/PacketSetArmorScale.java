package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;

public class PacketSetArmorScale extends PacketArmorSlot
{
	private int scale;
	
	public PacketSetArmorScale() {}
	
	public PacketSetArmorScale(int scale, @Nullable ArmorType armorType, int indexArmorSet)
	{
		super(armorType, indexArmorSet);
		this.scale = scale;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		buffer.writeInt(scale);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		scale = buffer.readInt();
	}
	
	public static class Handler implements IMessageHandler<PacketSetArmorScale, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetArmorScale message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					ItemStack stack = ItemStackHelper.getChiseledArmorStack(player, message.armorType, message.indexArmorSet);
					if (ItemStackHelper.isChiseledArmorStack(stack))
						BitToolSettingsHelper.setArmorScale(player, stack, message.scale, null, message.armorType, message.indexArmorSet);
				}
			});
			return null;
		}
		
	}
	
}